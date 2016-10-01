#! /bin/sh

# Script to generate Retrofit datafeed from swagger spec
# Usage ./scripts/update_datafeed.sh [-l <lib version>]

while getopts ":l:" opt; do
  case $opt in
    l) LIB_VERSION="$OPTARG" && echo "Setting GCE Library Version to $LIB_VERSION"
    ;;
    \?) echo "Unknown option -$OPTARG" && usage
    ;;
  esac
done
echo "Building Retrofit Datafeed for TBA Android App"

# This script should be run from the root of the TBA Android Repo
TBA_ANDROID_HOME=$(pwd)
if [ $(basename $TBA_ANDROID_HOME) = "scripts" ]; then
    echo "Please run from the TBA Android root directory"
    exit -1
fi

set -e

if [ ! -z "$LIB_VERSION" ]; then
    echo "Downloading swagger-codegen-cli jar version $LIB_VERSION"
    wget -O libTba/swagger/swagger-codegen-cli.jar https://github.com/the-blue-alliance/swagger-codegen/releases/download/$LIB_VERSION/swagger-codegen-cli.jar
fi

if [ ! -f libTba/swagger/swagger-codegen-cli.jar ]; then
    echo "swagger-codegen-cli.jar not found. Try running with -l <release-tag> to download"
    exit -1
fi

# Do our app mutations on the swagger spec
INITIAL_SPEC=libTba/swagger/apiv2-swagger.json
JSON_DIR=libTba/swagger/jsonFragments
SPEC=libTba/swagger/apiv2-swagger.mod.json
python libTba/swagger/mutate_spec.py --file $INITIAL_SPEC --json $JSON_DIR --out $SPEC

# Generate all the classes in a temporary directory so we can filter out what we want
rm -rf libTba/swagger/tmp
mkdir libTba/swagger/tmp

# rx version
java -jar libTba/swagger/swagger-codegen-cli.jar generate -i $SPEC -l java -o libTba/swagger/tmp -c libTba/swagger/tba-rx.json

# regular version
java -jar libTba/swagger/swagger-codegen-cli.jar generate -i $SPEC -l java -o libTba/swagger/tmp -c libTba/swagger/tba-call.json

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
OLD_NAME=DefaultApi
NEW_NAME=TbaApiV2
mv libTba/$PKG/call/DefaultApi.java libTba/$PKG/call/TbaApiV2.java
mv libTba/$PKG/rx/DefaultApi.java libTba/$PKG/rx/TbaApiV2.java
perl -pi -e "s/$OLD_NAME/$NEW_NAME/g" libTba/$PKG/{call,rx}/TbaApiV2.java

# Rename models to start with I<name>.java
CUR=$(pwd)
cd libTba/$PKG/model
for f in *.java; do mv "$f" "I$f"; done
cd $CUR

echo
echo "Cleaning up"
rm -rf libTba/swagger/tmp
