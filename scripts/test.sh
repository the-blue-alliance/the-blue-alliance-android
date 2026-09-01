#!/usr/bin/env bash
# Run all tests.
set -euo pipefail

cd "$(dirname "$0")/.."

export JAVA_HOME="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}"

echo "=== Unit tests ==="
./gradlew :app:testGmsDebugUnitTest :app:testQuestDebugUnitTest

if adb devices | grep -q 'device$'; then
    echo "=== Instrumentation tests ==="
    ./gradlew :app:connectedGmsDebugAndroidTest
else
    echo "No device connected, skipping instrumentation tests."
fi
