#!/usr/bin/env bash
# Start an Android emulator. Pass --headless for CI (no window).
set -euo pipefail

EMULATOR="${ANDROID_HOME:-$HOME/Library/Android/sdk}/emulator/emulator"
AVD="${1:-Pixel_8_API_35}"

if [[ "${2:-}" == "--headless" ]]; then
    "$EMULATOR" -avd "$AVD" -no-window -no-audio -gpu swiftshader_indirect &
else
    "$EMULATOR" -avd "$AVD" &
fi

echo "Waiting for emulator to boot..."
adb wait-for-device
adb shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done'
echo "Emulator ready."
