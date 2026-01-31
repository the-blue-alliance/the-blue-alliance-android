#!/usr/bin/env bash
# Capture a screenshot from the connected device/emulator.
# Usage: ./screenshot.sh [output.png]
set -euo pipefail

OUT="${1:-screenshot-$(date +%Y%m%d-%H%M%S).png}"
adb exec-out screencap -p > "$OUT"
echo "Saved: $OUT"
