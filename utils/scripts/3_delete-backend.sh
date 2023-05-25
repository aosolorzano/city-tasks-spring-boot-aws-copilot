#!/bin/bash

cd "$WORKING_DIR" || {
  echo "Error moving to the Tasks Service 'root' directory."
  exit 1
}
# The original AWS_PROFILE where the ECS cluster will be created.
workloads_profile_name=$AWS_PROFILE

read -r -p "Please, enter the <AWS Profile> for the service Deployment tools: [profile $workloads_profile_name] " deployment_profile_name
if [ "$deployment_profile_name" ]; then
  AWS_PROFILE=$deployment_profile_name
  export AWS_PROFILE
fi

echo ""
echo "DELETING COPILOT APP FROM AWS.."
copilot app delete --yes
echo "DONE!"
