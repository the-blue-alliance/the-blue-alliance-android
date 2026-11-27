#!/usr/bin/env bash
# Deep-link to a TBA screen on the connected device/emulator.
# Usage: ./navigate.sh /event/2024casf
#        ./navigate.sh /team/frc254
#
# The intent is pinned to the app's package (-p): debug builds aren't verified
# for thebluealliance.com, so an unpinned VIEW intent can resolve to Chrome.
# The package is the first of these that is installed: $TBA_PACKAGE, the debug
# build (com.thebluealliance.androidclient.development), the release build.
# Honors $ANDROID_SERIAL like adb.
set -euo pipefail

PATH_ARG="${1:?Usage: navigate.sh <path> (e.g. /event/2024casf)}"

installed="$(adb shell pm list packages com.thebluealliance.androidclient | tr -d '\r')"
PKG=""
for candidate in ${TBA_PACKAGE:-} \
    com.thebluealliance.androidclient.development \
    com.thebluealliance.androidclient; do
  if grep -qx "package:${candidate}" <<<"$installed"; then
    PKG="$candidate"
    break
  fi
done
if [[ -z "$PKG" ]]; then
  echo "navigate.sh: no TBA app installed (set TBA_PACKAGE to override)" >&2
  exit 1
fi

adb shell am start -a android.intent.action.VIEW -p "$PKG" \
    -d "https://www.thebluealliance.com${PATH_ARG}"
