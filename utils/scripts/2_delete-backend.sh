#!/bin/bash

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}

if [ -z "$AWS_WORKLOADS_ENV" ]; then
  read -r -p 'Enter the <AWS Environment> used to deploy the Service: [dev] ' env_name
  if [ -z "$env_name" ]; then
    AWS_WORKLOADS_ENV='dev'
  else
    AWS_WORKLOADS_ENV=$env_name
  fi
  export AWS_WORKLOADS_ENV
fi

echo ""
echo "DELETING COPILOT APP FROM AWS..."
copilot app delete --yes
sh "$WORKING_DIR"/utils/scripts/helper/1_revert-automated-scripts.sh

echo ""
echo "DONE!"
