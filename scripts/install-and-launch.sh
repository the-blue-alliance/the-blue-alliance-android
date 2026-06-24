#!/usr/bin/env bash
# Build debug APK, install on connected device/emulator, and launch.
set -euo pipefail

cd "$(dirname "$0")/.."

export JAVA_HOME="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}"

./gradlew :app:installDebug

# Android 17 (targetSdk 37) gates local-network access (the 10.0.2.2 dev server is in
# 10.0.0.0/8) behind the runtime ACCESS_LOCAL_NETWORK permission. It's declared in the debug
# manifest, but being a dangerous permission it isn't auto-granted — grant it so the debug
# build reaches the local backend without a manual step. No-op on pre-17 / if already granted.
adb shell pm grant com.thebluealliance.androidclient.development \
    android.permission.ACCESS_LOCAL_NETWORK 2>/dev/null || true

adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.MainActivity
