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
        echo "Running project unit tests"
        ./gradlew testDebugProguardUnitTest --stacktrace
        filter_code $?
        ;;

    "COVERAGE")
        echo "Generating project code coverage"
        ./gradlew jacocoTestReport coveralls
        filter_code $?
        ;;

    "CHECKSTYLE")
        echo "Running project checkstyle"
        ./gradlew androidCheckstyle
        CODE=$?
        html2text android/build/outputs/checkstyle/checkstyle.html
        filter_code $CODE
        ;;

    "PROD-BUILD")
        echo "Making sure we can build a prod apk (although with different keys)"

        # Move local.properties and tba.properties to proper location
        mv config/local.properties.ci local.properties

	# Generate a key (with the default debug creds) to use for this test build
	keytool -genkey -v -keystore /home/travis/.android/debug.keystore -storepass android -alias androiddebugkey -keypass android -dname "CN=Android Debug,O=Android,C=US"
        ./gradlew assembleRelease
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
