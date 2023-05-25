#!/bin/bash

cd "$WORKING_DIR" || {
  echo "Error moving to the Tasks Service 'root' directory."
  exit 1
}

echo ""
echo "Getting information from AWS. Please wait..."

echo ""
authStack=$(aws cloudformation list-stacks --output text \
  --query "StackSummaries[?contains(StackName, 'authHiperiumCityIdP') && (StackStatus=='CREATE_COMPLETE' || StackStatus=='UPDATE_COMPLETE')].[StackName]" \
  --profile "$IDP_AWS_PROFILE")
if [ -z "$authStack" ]; then
  echo "No Tasks Service Identity Provider (IdP) stack found on CloudFormation."
  exit 0
fi
echo "IdP Service Stack found: $authStack"

user_pool_id=$(aws cloudformation describe-stacks   \
  --stack-name "$authStack"                         \
  --query "Stacks[0].Outputs[0].OutputValue"        \
  --output text                                     \
  --profile "$IDP_AWS_PROFILE")
if [ -z "$user_pool_id" ]; then
  echo "No Cognito User Pool found on CloudFormation."
  exit 0
fi
echo "User Pool ID found: $user_pool_id"

echo ""
echo "UPDATING API MANIFEST FILE..."
if [ "$TASK_SERVICE_ENV" == "prod" ]; then
  sed -i'.bak' -e "s/aws_region/$AWS_REGION/g; s/cognito_user_pool_id_prod/$user_pool_id/g" copilot/api/manifest.yml
else
  sed -i'.bak' -e "s/aws_region/$AWS_REGION/g; s/cognito_user_pool_id_pre/$user_pool_id/g" copilot/api/manifest.yml
fi
rm -f copilot/api/manifest.yml.bak
echo "DONE!"

echo ""
echo "COPILOT IAM ROLES ON AWS..."
copilot init                          \
  --app city-tasks-api                \
  --name tasks-api                    \
  --type 'Load Balanced Web Service'  \
  --dockerfile './Dockerfile'         \
  --port 8080                         \
  --tag 'aosolorzano/city-tasks-api:1.5.0'

echo ""
read -r -p "Please, enter the <AWS Profile> for the Deployment tools: [default] " deployment_profile_name
if [ -z "$deployment_profile_name" ]; then
    deployment_profile_name='default'
fi

echo ""
echo "COPILOT ENVIRONMENT ON AWS (ECR, KMS AND S3)..."
copilot env init                          \
  --app city-tasks-api                    \
  --name dev                              \
  --profile "$deployment_profile_name"    \
  --default-config

echo ""
echo "DEPLOYING COPILOT ENVIRONMENT (VPC, SUBNETS AND ECS) ON AWS..."
copilot env deploy          \
  --app city-tasks-api      \
  --name dev

echo ""
echo "DEPLOYING COPILOT ECS CLUSTER ON AWS..."
copilot deploy                  \
  --app city-tasks-api          \
  --name api                    \
  --env dev

echo ""
echo "PUSHING CHANGES TO GIT REPOSITORY..."
git add "$WORKING_DIR"/copilot/api/manifest.yml
git commit -m "Updating Copilot manifest.yml file with the latest automation scripts changes."
git push --set-upstream origin "$TASK_SERVICE_ENV"

echo ""
echo "DONE!"
