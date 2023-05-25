#!/bin/bash

cd "$WORKING_DIR"/ || {
  echo "Error moving to the Tasks Service 'root' directory."
  exit 1
}

read -r -p 'Please, enter the <Git Provider> that hosts the source code: [default GitHub] ' git_provider
if [ -z "$git_provider" ]; then
  git_provider='GitHub'
fi

echo ""
echo "CREATING A CODESTAR CONNECTION ON AWS..."
aws codestar-connections create-connection    \
  --provider-type $git_provider               \
  --connection-name hiperium-city-tasks-conn

echo ""
echo "Please, go to the 'CodeBuild' service in the selected '$AWS_PROFILE' account, and select 'Connections' in the Settings section to complete the connection."
echo ""
echo "After that, return to this terminal and press any key to continue..."
read -n 1 -s -r -p ""

echo ""
echo "DONE!"
