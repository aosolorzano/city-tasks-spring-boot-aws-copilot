#!/bin/bash

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}

function assignS3BucketForAccessLogs() {
  echo ""
  read -r -p 'Do you want to create a new <S3 Bucket> to store the ALB access logs? [Y/n] ' create_s3_bucket
  if [ -z "$create_s3_bucket" ] || [ "$create_s3_bucket" == "Y" ] || [ "$create_s3_bucket" == "y" ]; then
    read -r -p 'Please, enter the <Bucket> name: ' s3_bucket_name
    if [ -z "$s3_bucket_name" ]; then
      echo "Error: S3 Bucket name is required."
      exit 1
    fi
    echo ""
    echo "CREATING S3 BUCKET..."
    aws s3api create-bucket                   \
        --bucket "$s3_bucket_name"            \
        --profile "$AWS_WORKLOADS_PROFILE"
    echo "DONE!"

    echo ""
    echo "ASSIGNING S3 BUCKET POLICY..."
    workloads_account_id=$(aws configure get sso_account_id --profile "$AWS_WORKLOADS_PROFILE")
    sed -i'.bak' -e "s/aws_account_id/$workloads_account_id/g; s/bucket_name/$s3_bucket_name/g" \
          "$WORKING_DIR"/utils/aws/iam/s3-alb-access-logs-policy.json
    rm -f "$WORKING_DIR"/utils/aws/iam/s3-alb-access-logs-policy.json.bak

    aws s3api put-bucket-policy               \
        --bucket "$s3_bucket_name"            \
        --policy file://"$WORKING_DIR"/utils/aws/iam/s3-alb-access-logs-policy.json \
        --profile "$AWS_WORKLOADS_PROFILE"
    echo "DONE!"
  else
    read -r -p 'Please, enter the existing <S3 Bucket> name: [city-tasks-alb-dev]' s3_bucket_name
    if [ -z "$s3_bucket_name" ]; then
      s3_bucket_name='city-tasks-alb-dev'
    fi
    echo "DONE!"
  fi
  ### UPDATING ENVIRONMENT MANIFEST FILE
  sed -i'.bak' -e "s/s3_bucket_name/$s3_bucket_name/g" \
        "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml
  rm -f "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml.bak
}

assignS3BucketForAccessLogs
echo ""
echo "Getting information from AWS. Please wait..."

echo ""
user_pool_id=$(aws cognito-idp list-user-pools --max-results 1 --output text  \
  --query "UserPools[?contains(Name, 'CityUserPool')].[Id]"                   \
  --profile "$AWS_IDP_PROFILE")
if [ -z "$user_pool_id" ]; then
  echo "No Cognito User Pool found on CF with the given name."
  exit 0
fi
echo "Cognito User Pool found: $user_pool_id"

### UPDATING API MANIFEST FILE
aws_idp_region=$(aws configure get region --profile "$AWS_IDP_PROFILE")
sed -i'.bak' -e "s/aws_idp_region/$aws_idp_region/g; s/user_pool_id/$user_pool_id/g" copilot/api/manifest.yml
rm -f copilot/api/manifest.yml.bak

echo ""
echo "INITIALIZING COPILOT STACK ON AWS..."
copilot init                              \
  --app city-tasks                        \
  --name api                              \
  --type 'Load Balanced Web Service'      \
  --dockerfile './Dockerfile'             \
  --port 8080                             \
  --tag '1.5.0'
echo ""
echo "DONE!"

echo ""
echo "INITIALIZING ENVIRONMENT ON AWS..."
copilot env init                          \
  --app city-tasks                        \
  --name "$AWS_WORKLOADS_ENV"             \
  --profile "$AWS_WORKLOADS_PROFILE"      \
  --container-insights                    \
  --default-config
echo ""
echo "DONE!"

echo ""
echo "DEPLOYING ENVIRONMENT NETWORKING ON AWS..."
copilot env deploy                        \
  --app city-tasks                        \
  --name "$AWS_WORKLOADS_ENV"
echo ""
echo "DONE!"

echo ""
echo "DEPLOYING CONTAINER APPLICATION ON AWS..."
copilot deploy                            \
  --app city-tasks                        \
  --name api                              \
  --env "$AWS_WORKLOADS_ENV"              \
  --tag '1.5.0'                           \
  --resource-tags project=Hiperium,copilot-application-type=api,copilot-application-version=1.5.0
echo ""
echo "DONE!"
