#!/usr/bin/env bash
# Start an Android emulator. Pass --headless for CI (no window).
set -euo pipefail

SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
EMULATOR="$SDK/emulator/emulator"
ADB="$SDK/platform-tools/adb"
AVD="${1:-Medium_Phone}"

# Snapshot the set of running emulators so we can identify the one we launch,
# rather than `adb wait-for-device` (which is ambiguous with multiple devices).
before="$("$ADB" devices | awk '/^emulator-/{print $1}' | sort)"

if [[ "${2:-}" == "--headless" ]]; then
    "$EMULATOR" -avd "$AVD" -no-window -no-audio -gpu swiftshader_indirect &
else
    "$EMULATOR" -avd "$AVD" &
fi

echo "Waiting for emulator to boot..."
serial=""
for _ in $(seq 1 60); do
    sleep 1
    after="$("$ADB" devices | awk '/^emulator-/{print $1}' | sort)"
    serial="$(comm -13 <(echo "$before") <(echo "$after") | head -1)"
    [[ -n "$serial" ]] && break
done
[[ -n "$serial" ]] || { echo "Emulator did not register with adb." >&2; exit 1; }

"$ADB" -s "$serial" wait-for-device
"$ADB" -s "$serial" shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done'
echo "Emulator ready: $serial ($AVD)"
