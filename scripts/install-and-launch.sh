#!/usr/bin/env bash
# Build debug APK, install on connected device/emulator, and launch.
set -euo pipefail

cd "$(dirname "$0")/.."

export JAVA_HOME="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}"

./gradlew :app:installDebug

adb shell am start -n com.thebluealliance.android.dev/com.thebluealliance.android.MainActivity
