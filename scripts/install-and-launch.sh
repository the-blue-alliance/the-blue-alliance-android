#!/usr/bin/env bash
# Build debug APK, install on connected device/emulator, and launch.
set -euo pipefail

cd "$(dirname "$0")/.."

export JAVA_HOME="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}"

./gradlew :app:installDebug

# Launch via scripts/emu, which also grants the Android 17 ACCESS_LOCAL_NETWORK permission so
# the debug build can reach the 10.0.2.2 local backend (the grant lives in one place — cmd_launch).
scripts/emu launch com.thebluealliance.androidclient.development/com.thebluealliance.android.MainActivity
