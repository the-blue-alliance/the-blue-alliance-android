#!/usr/bin/env bash
# Deep-link to a TBA screen on the connected device/emulator.
# Usage: ./navigate.sh /event/2024casf
#        ./navigate.sh /team/frc254
#
# The intent is pinned to the app's package (-p): debug builds aren't verified
# for thebluealliance.com, so an unpinned VIEW intent can resolve to Chrome.
# The package is $TBA_PACKAGE if set (it must be installed), else the debug
# build (com.thebluealliance.androidclient.development), else the release build.
# Honors $ANDROID_SERIAL like adb.
set -euo pipefail

PATH_ARG="${1:?Usage: navigate.sh <path> (e.g. /event/2024casf)}"

# pm's filter is a substring match; -x keeps only the exact package.
is_installed() {
  adb shell pm list packages "$1" | tr -d '\r' | grep -x "package:$1" >/dev/null
}

if [[ -n "${TBA_PACKAGE:-}" ]]; then
  is_installed "$TBA_PACKAGE" || { echo "navigate.sh: TBA_PACKAGE=$TBA_PACKAGE is not installed" >&2; exit 1; }
  PKG="$TBA_PACKAGE"
elif is_installed com.thebluealliance.androidclient.development; then
  PKG=com.thebluealliance.androidclient.development
elif is_installed com.thebluealliance.androidclient; then
  PKG=com.thebluealliance.androidclient
else
  echo "navigate.sh: no TBA app installed (set TBA_PACKAGE to pick one)" >&2
  exit 1
fi

adb shell am start -a android.intent.action.VIEW -p "$PKG" \
    -d "https://www.thebluealliance.com${PATH_ARG}"
