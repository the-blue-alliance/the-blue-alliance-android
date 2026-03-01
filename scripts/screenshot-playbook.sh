#!/usr/bin/env bash
#
# Play Store Screenshot Playbook
#
# Captures 6 phone screenshots for the Google Play Store listing.
# Uses the release build for authentic Play Store screenshots.
#
# Prerequisites:
#   - Android emulator running (1080x2400 device)
#   - emu tool available at scripts/emu (included in repo)
#   - Release build installed: ./gradlew :app:installRelease
#   - App has data loaded (events, teams, districts for current year)
#   - Emulator DNS must work: if Private DNS is enabled, disable it with:
#       adb shell settings put global private_dns_mode off
#   - Release signing must use tba-keys (the SHA-1 registered in Firebase):
#       In local.properties, uncomment the "tba-keys" signing block and
#       comment out the "Google Play upload key" block. The upload key SHA-1
#       is not registered in the Firebase/GCP project, so Remote Config and
#       Auth will fail if the upload key is used on the emulator.
#
# Usage:
#   bash scripts/screenshot-playbook.sh
#

set -euo pipefail

EMU="$(dirname "$0")/emu"
PKG="com.thebluealliance.androidclient"
MAIN_ACTIVITY="com.thebluealliance.android.MainActivity"
RAW_DIR="screenshots/play-store/phone"
DEST_DIR="app/src/main/play/listings/en-US/graphics/phone-screenshots"

mkdir -p "$RAW_DIR" "$DEST_DIR"

wait_for_ui() {
  sleep "${1:-2}"
}

deeplink() {
  adb shell am start -a android.intent.action.VIEW \
    -d "https://www.thebluealliance.com$1" "$PKG"
}

scroll_down() {
  adb shell input swipe 540 1600 540 600 300
}

# Enable demo mode for a clean status bar.
# Demo mode is set BEFORE launching the app so that enableEdgeToEdge() in
# MainActivity properly sets dark status bar icons on top of demo mode.
echo "==> Enabling demo mode..."
adb shell settings put global sysui_demo_allowed 1
adb shell am broadcast -a com.android.systemui.demo -e command enter
adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 0254
adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false
adb shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4 -e fully true
adb shell am broadcast -a com.android.systemui.demo -e command network -e mobile show -e level 4
adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false

echo "==> Launching app..."
$EMU launch "$PKG/$MAIN_ACTIVITY"
wait_for_ui 5

# 1. Events list (home screen)
echo "==> Screenshot 1: Events list"
$EMU screenshot "$RAW_DIR/01-events-list.png"
wait_for_ui

# 2. Event detail — Matches tab
# Using 2025casf (San Francisco Regional) which has full score breakdowns.
# Switch to a 2026 week 1+ event once one has been played.
echo "==> Screenshot 2: Event matches"
deeplink "/event/2025casf"
wait_for_ui
$EMU tap "Matches"
wait_for_ui
$EMU screenshot "$RAW_DIR/02-event-matches.png"

# 3. Match detail — tap a completed qual match
echo "==> Screenshot 3: Match detail"
$EMU tap "Q10"
wait_for_ui
$EMU screenshot "$RAW_DIR/03-match-detail.png"

# 4. Team detail — Info tab (Team 177 — Bobcat Robotics)
echo "==> Screenshot 4: Team detail"
deeplink "/team/177"
wait_for_ui
$EMU screenshot "$RAW_DIR/04-team-detail.png"

# 5. Team notification preferences — all toggles ON
echo "==> Screenshot 5: Team notification preferences"
$EMU tap "Notification preferences"
wait_for_ui
$EMU screenshot "$RAW_DIR/05-notification-prefs.png"

# 6. District detail — New England
echo "==> Screenshot 6: District detail"
$EMU launch "$PKG/$MAIN_ACTIVITY" # Back to Main
wait_for_ui
$EMU tap "Districts"
wait_for_ui
scroll_down
wait_for_ui
$EMU tap "New England"
wait_for_ui
$EMU screenshot "$RAW_DIR/06-district-detail.png"

# Copy to Play Store listing directory
echo "==> Copying to $DEST_DIR"
cp "$RAW_DIR/01-events-list.png"        "$DEST_DIR/1-events-list.png"
cp "$RAW_DIR/02-event-matches.png"      "$DEST_DIR/2-event-matches.png"
cp "$RAW_DIR/03-match-detail.png"       "$DEST_DIR/3-match-detail.png"
cp "$RAW_DIR/04-team-detail.png"        "$DEST_DIR/4-team-detail.png"
cp "$RAW_DIR/05-notification-prefs.png" "$DEST_DIR/5-notification-prefs.png"
cp "$RAW_DIR/06-district-detail.png"    "$DEST_DIR/6-district-detail.png"

# Disable demo mode to restore normal status bar
echo "==> Disabling demo mode..."
adb shell am broadcast -a com.android.systemui.demo -e command exit

echo "==> Done! Screenshots saved to $DEST_DIR"
ls -la "$DEST_DIR"
