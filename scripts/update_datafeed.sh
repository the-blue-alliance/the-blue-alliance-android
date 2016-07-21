#! /bin/sh

# Script to generate Retrofit datafeed from swagger spec
# Usage ./scripts/update_datafeed.sh

echo "Building Retrofit Datafeed for TBA Android App"

# This script should be run from the root of the TBA Android Repo
TBA_ANDROID_HOME=$(pwd)
if [ $(basename $TBA_ANDROID_HOME) = "scripts" ]; then
    echo "Please run from the TBA Android root directory"
    exit -1
fi

set -e

# Generate all the classes in a temporary directory so we can filter out what we want
rm -rf libTba/swagger/tmp
mkdir libTba/swagger/tmp

# rx version
java -jar libTba/swagger/swagger-codegen-cli.jar generate -i libTba/swagger/apiv2-swagger.json -l java -o libTba/swagger/tmp -c libTba/swagger/tba-rx.json

# regular version
java -jar libTba/swagger/swagger-codegen-cli.jar generate -i libTba/swagger/apiv2-swagger.json -l java -o libTba/swagger/tmp -c libTba/swagger/tba-call.json

echo
echo "Moving generated files"

# Move generated files to module
PKG=src/main/java/com/thebluealliance/api
DST=libTba/$PKG/
rm -rf $DST
mkdir -p $DST
cp -r libTba/swagger/tmp/$PKG/call/ $DST
cp -r libTba/swagger/tmp/$PKG/rx/ $DST
cp -r libTba/swagger/tmp/$PKG/model/ $DST

echo "Patching"
mv libTba/$PKG/call/DefaultApi.java libTba/$PKG/call/TbaApiV2.java
mv libTba/$PKG/rx/DefaultApi.java libTba/$PKG/rx/TbaApiV2.java
git apply scripts/patches/swagger-api-names.patch

echo
echo "Cleaning up"
rm -rf libTba/swagger/tmp
