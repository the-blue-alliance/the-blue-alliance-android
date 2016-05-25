#! /usr/bin/env sh
set -e

# Worker script to process the various travis builds
# Calls different things based on command argument

case "$1" in

    "UNIT")
        echo "Running project unit tests"
        ./gradlew testProdDebugProguardUnitTest --stacktrace
        ;;

    "COVERAGE")
        echo "Generating project code coverage"
        ./gradlew jacocoTestReport coveralls
        ;;

    "CHECKSTYLE")
        echo "Running project checkstyle"
        ./gradlew androidCheckstyle
        ;;

    *)
        echo "Unknown job type $JOB"
        exit -1
        ;;
esac
