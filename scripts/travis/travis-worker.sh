#! /usr/bin/env sh

# Worker script to process the various travis builds
# Calls different things based on command argument

filter_code () {
    # Allow a return code of 137, we'll whitelist robolectric OOMs
    if test "$1" = "137" ; then
        echo "Allowing gradle return code 137. Probably a Robolectric OOM bug :("
    elif test "$1" != "0" ; then
        echo "Error with job, exited with code $1"
        exit $1
    fi
}

case "$1" in

    "UNIT")
        echo "Downloading robolectric depenrencies..."
        ./gradlew filesForHermeticBuild
        echo "Downloaded:"
        ls -l android/build/output/libs

        echo "Running project unit tests"
        ./gradlew testDebugProguardUnitTest --stacktrace -Drobolectric_offline=true
        CODE=$?
        if test "$TRAVIS" = "true" ; then
            html2text android/build/reports/tests/testDebugProguardUnitTest/index.html
        fi
        filter_code $CODE
        ;;

    "CHECKSTYLE")
        echo "Running project checkstyle"
        ./gradlew checkstyle
        CODE=$?
        if test "$TRAVIS" = "true" ; then
            html2text android/build/reports/checkstyle/checkstyle.html
        fi
        filter_code $CODE
        ;;

    "DATAFEED_CODEGEN")
        echo "Running datafeed codegen"
        set -e
        # Run the regeneration ourselves
        ./scripts/update_datafeed.sh -l v2.2.1-FORKED2

        # Fail if there are an uncommitted changes
        git diff --exit-code --ignore-submodules
        ;;

    "PROD-BUILD")
        echo "Making sure we can build a prod apk (although with different keys)"

        # Move local.properties and tba.properties to proper location
        mv config/local.properties.ci local.properties

    	# Generate a key (with the default debug creds) to use for this test build
	    keytool -genkey -v -keystore /home/travis/.android/debug.keystore -keyalg RSA -keysize 2048 -storepass android -alias androiddebugkey -keypass android -dname "CN=Android Debug,O=Android,C=US"
        ./gradlew assembleRelease --stacktrace
        ;;

    "SCREENSHOT")
        echo "Running project screenshot tests"
        ./gradlew verifyMode screenshotTests --info
        ;;

    *)
        echo "Unknown job type $JOB"
        exit -1
        ;;
esac
