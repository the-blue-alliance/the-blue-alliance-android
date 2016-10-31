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
        ./gradlew testProdDebugProguardUnitTest --stacktrace
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
        filter_code $?
        ;;

    "PROD-BUILD")
        echo "Making sure we can build a prod apk (although with different keys)"

        # Move local.properties and tba.properties to proper location
        openssl aes-256-cbc -K $encrypted_5e22a99c7891_key -iv $encrypted_5e22a99c7891_iv -in config/ci-keys.tar.enc -out config/ci-keys.tar -d
        cd config
        tar xf ci-keys.tar
        mv local.properties ..
        mv tba.properties ../android/src/main/assets
        mv ci-google-services.json ../android/src/prod
        cd ..
        ./gradlew assembleProdRelease
        ;;

    *)
        echo "Unknown job type $JOB"
        exit -1
        ;;
esac
