#! /bin/sh

# Push a newly built release to github
# Done in bash for ease of managing tokens
# Depends on github-release
# https://github.com/aktau/github-release

# Usage: ./scripts/github_release.sh <tag> <name> <changelog_path> <shortlog_path> <apk file>

# Variables
USER="the-blue-alliance"
REPO="the-blue-alliance-android"
TAG=$1
NAME=$2
DESC_PATH=$3
SHORTLOG_PATH=$4
APK=$5
APK_NAME=$(basename $APK)
DESC=$(cat $DESC_PATH)
DESC+="\nShortlog:\n$(cat $SHORTLOG_PATH)"

echo "Creating GitHub Release for $USER/$REPO @ $TAG"

# Load github auth token
TOKEN=$(cat scripts/github_token)
export GITHUB_TOKEN=$TOKEN

# Create Release
github-release release \
    --user $USER \
    --repo $REPO \
    --tag $TAG \
    --name "$NAME" \
    --description "$DESC"

# Add apk
github-release upload \
    --user $USER \
    --repo $REPO \
    --tag $TAG \
    --name $APK_NAME \
    --file $APK

unset GITHUB_TOKEN
