#! /usr/bin/env sh

# Worker script to process the various travis builds
# Calls different things based on command argument

filter_code () {
    # Allow a return code of 137, we'll whitelist robolectric OOMs
    RET=$?
    if test "$RET" = "137" ; then
        echo "Allowing gradle return code 137. Probably a Robolectric OOM bug :("
    elif test "$RET" != "0" ; then
        echo "Error with job, exited with code $RET"
        exit $RET
    fi
}

case "$1" in

    "UNIT")
        echo "Running project unit tests"
        ./gradlew testProdDebugProguardUnitTest --stacktrace -PdisablePreDex
        filter_code
        ;;

    "COVERAGE")
        echo "Generating project code coverage"
        ./gradlew jacocoTestReport coveralls -PdisablePreDex
        filter_code
        ;;

    "CHECKSTYLE")
        echo "Running project checkstyle"
        ./gradlew androidCheckstyle -PdisablePreDex
        filter_code
        ;;

    "SCREENSHOT")
        echo "Running project screenshot tests"
        ./gradlew verifyMode screenshotTests -PdisablePreDex
        ;;

    *)
        echo "Unknown job type $JOB"
        exit -1
        ;;
esac
