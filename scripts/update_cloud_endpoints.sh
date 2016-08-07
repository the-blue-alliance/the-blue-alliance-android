#! /bin/sh

# Script to generate code for Cloud Endpoints client lib
# Usage: ./scripts/update_cloud_endpoints.sh -t <path-to-tba> -s <path-to-gae-sdk> -a <debug-app-id> -l <gce2retrofit-tag>

usage() { echo "Usage: $0 [-t <path-to-tba>] [-s <path-to-gae-sdk> [-l <gce2retrofit-tag>]" 1>&2; exit 1; }

# Default to packaging for prod
TBA_APP_ID="tbatv-prod-hrd"

while getopts ":t:s:a:l:" opt; do
  case $opt in
    t) TBA_HOME="$OPTARG" && echo "Setting TBA Home to $TBA_HOME"
    ;;
    p) GAE_HOME="$OPTARG" && echo "Setting GAE Home to $GAE_HOME"
    ;;
    a) TBA_APP_ID="$OPTARG"
    ;;
    l) LIB_VERSION="$OPTARG" && echo "Setting GCE Library Version to $LIB_VERSION"
    ;;
    \?) echo "Unknown option -$OPTARG" && usage
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
set -e

# Set the proper app id and modify mobile API file to remove sitevar references
perl -pi -e "s/tbatv-dev-hrd/$TBA_APP_ID/g" *.yaml
git apply $TBA_ANDROID_HOME/scripts/patches/endpoints_remove_sitevar.patch

# Generate discovery document
# $GAE_HOME/endpointscfg.py get_client_lib java -o $TBA_ANDROID_HOME -bs gradle mobile_main.MobileAPI
echo "Getting discovery.json for $TBA_APP_ID"
$GAE_HOME/endpointscfg.py get_discovery_doc -o $TBA_ANDROID_HOME/libTba/gce/ mobile_main.MobileAPI
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
mv libTba/gce/tbaMobile-v*.discovery libTba/gce/gce_discovery.json

if [ ! -z "$LIB_VERSION" ]; then
    echo "Downloading gce2retrofit jar version $LIB_VERSION"
    wget -P libTba/gce/ https://github.com/the-blue-alliance/gce2retrofit/releases/download/$LIB_VERSION/gce2retrofit.jar
fi

if [ ! -f libTba/gce/gce2retrofit.jar ]; then
    echo "gce2retrofit.jar not found. Try running with -l <release-tag> to download"
    exit -1
fi

echo "Generating retrofit services"
java -jar libTba/gce/gce2retrofit.jar libTba/gce/gce_discovery.json ./libTba/src/main/java/ -methods async,reactive

exit $RES
