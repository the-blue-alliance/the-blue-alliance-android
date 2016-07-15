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

    *)
        echo "Unknown job type $JOB"
        exit -1
        ;;
esac
