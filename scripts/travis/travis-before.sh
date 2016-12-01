#! /usr/bin/env sh

# Perform the before_script for travis builds
# Depends on the current job
# (so we only start an emulator when we need)

common () {
    mkdir android/src/main/assets
    touch android/src/main/assets/tba.properties
}

start_emulator () {
    pip install --user pillow
    android list targets
    echo no | android create avd --force -n sdk23 -t "android-23" --abi armeabi-v7a --tag google_apis --device "Nexus 5"
    emulator -avd sdk23 -no-audio -no-window -no-skin -data ~/.android/avd/sdk23.avd/userdata.img &
    android-wait-for-emulator
    adb shell input keyevent 82 &
}

case "$1" in

    "UNIT")
        echo "Setting up environment for project unit tests"
        common
        ;;

    "COVERAGE")
        echo "Setting up environment for project code coverage"
        common
        ;;

    "CHECKSTYLE")
        echo "Setting up environment for project checkstyle"
        common
        ;;

    "PROD-BUILD")
        echo "Setting up environment for test production build"
        common
        ;;

    "SCREENSHOT")
        echo "Setting up environment for screenshot tests"
        common
        start_emulator
        ;;

    *)
        echo "Unknown job type for configuration $JOB"
        exit -1
        ;;
esac
