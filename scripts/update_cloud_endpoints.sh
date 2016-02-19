#! /bin/sh

# Script to generate code for Cloud Endpoints client lib
# Usage: ./scripts/update_cloud_endpoints.sh -t <path-to-tba> -s <path-to-gae-sdk> -a <debug-app-id>

# Default to packaging for prod
TBA_APP_ID="tbatv-prod-hrd"

while getopts ":t:s:a:" opt; do
  case $opt in
    t) TBA_HOME="$OPTARG" && echo "Setting TBA Home to $TBA_HOME"
    ;;
    p) GAE_HOME="$OPTARG" && echo "Setting GAE Home to $GAE_HOME"
    ;;
    a) TBA_APP_ID="$OPTARG"
    ;;
    \?) echo "Invalid option -$OPTARG" >&2 && exit -1
    ;;
  esac
done

echo "Building Cloud Endpoints library for TBA App ID $TBA_APP_ID"

# This script should be run from the root of the TBA Android Repo
TBA_ANDROID_HOME=$(pwd)
if [ $(basename $TBA_ANDROID_HOME) = "scripts" ]; then
    echo "Please run from the TBA Android root directory"
    exit -1
fi

# Get directory of TBA code. Default to $TBA_HOME environment variable, path paramter otherwise
if [ -z "$TBA_HOME" ]; then
    echo "Please export TBA_HOME to your environment with the path to the-blue-alliance repo, or pass it as the first paramter"
    exit -1
fi

# Get directory of GAE SDK. Default to $GAE_HOME environment variable, path paramter otherwise
if [ -z "$TBA_HOME" ]; then
    echo "Please export GAE_HOME to your environment with the path to Google App Engine SDK, or pass it as the second paramter"
    exit -1
fi

# Set up & generate endpoints client library
cd $TBA_HOME

# Set the proper app id and modify mobile API file to remove sitevar references
perl -pi -e "s/tbatv-dev-hrd/$TBA_APP_ID/g" *.yaml
git apply $TBA_ANDROID_HOME/scripts/patches/endpoints_remove_sitevar.patch

# Generate discovery document
# $GAE_HOME/endpointscfg.py get_client_lib java -o $TBA_ANDROID_HOME -bs gradle mobile_main.MobileAPI
$GAE_HOME/endpointscfg.py get_discovery_doc -o $TBA_ANDROID_HOME/gce/ mobile_main.MobileAPI
RES="$?"

# Undo our changes
git apply -R $TBA_ANDROID_HOME/scripts/patches/endpoints_remove_sitevar.patch
perl -pi -e "s/$TBA_APP_ID/tbatv-dev-hrd/g" *.yaml
cd $TBA_ANDROID_HOME

if [ $RES != 0 ]; then
    echo "Failed to generate cloud endpoints discovery doc"
    exit $RES
fi

# Now generate retrofit services
echo "Renaming discovery document to gce/gce_discovery.json"
mv gce/tbaMobile-v*.discovery gce/gce_discovery.json

echo "Generating retrofit services"
java -jar gce/gce2retrofit.jar gce/gce_discovery.json ./tbaMobile/src/main/java/ -methods sync,async,reactive

exit $RES
