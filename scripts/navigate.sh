#!/usr/bin/env bash
# Deep-link to a TBA screen on the connected device/emulator.
# Usage: ./navigate.sh /event/2024casf
#        ./navigate.sh /team/frc254
set -euo pipefail

PATH_ARG="${1:?Usage: navigate.sh <path> (e.g. /event/2024casf)}"
adb shell am start -a android.intent.action.VIEW \
    -d "https://www.thebluealliance.com${PATH_ARG}"
