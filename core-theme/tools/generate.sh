#!/usr/bin/env bash
# Regenerates the color token files from the TBA seed with Material Color Utilities, which ships
# inside Material Components for Android. Needs a JDK and python3; downloads one pinned AAR.
set -euo pipefail
MDC_VERSION=1.14.0
MDC_SHA256=19e8fab43df6870f8e4322059624384be0279da56dc910529ff591dc674008cd
here="$(cd "$(dirname "$0")" && pwd)"
out="$here/../src/main/kotlin/com/thebluealliance/android/core/theme"
work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT
curl -sSfL -o "$work/material.aar" \
  "https://dl.google.com/android/maven2/com/google/android/material/material/$MDC_VERSION/material-$MDC_VERSION.aar"
echo "$MDC_SHA256  $work/material.aar" | shasum -a 256 -c - >/dev/null
unzip -q "$work/material.aar" classes.jar -d "$work"
javac -nowarn -cp "$work/classes.jar" -d "$work" "$here/GenerateColorTokens.java"
java -cp "$work/classes.jar:$work" GenerateColorTokens | python3 -I "$here/emit_tokens.py" "$out"
echo "Regenerated tokens in $out"
