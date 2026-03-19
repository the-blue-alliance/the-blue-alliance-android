#!/usr/bin/env bash
#
# Play Store Screenshot Playbook
#
# Captures 7 phone screenshots for the Google Play Store listing.
# Uses the release build for authentic Play Store screenshots.
#
# Prerequisites:
#   - Android emulator running (1080x2400 device)
#   - emu tool available at scripts/emu (included in repo)
#   - Release build installed: ./gradlew :app:installRelease
#   - A Google account signed in on the emulator (for Google Sign-In)
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
#   bash scripts/screenshot-playbook.sh             # screenshots 1–6 only
#   bash scripts/screenshot-playbook.sh --widgets   # screenshots 1–7 (widgets need live event data)
#

set -euo pipefail

INCLUDE_WIDGETS=false
for arg in "$@"; do
  case "$arg" in
    --widgets) INCLUDE_WIDGETS=true ;;
  esac
done

EMU="$(dirname "$0")/emu"
PKG="com.thebluealliance.androidclient"
DEV_PKG="com.thebluealliance.androidclient.development"
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

# Force light mode so screenshots always use the light theme.
echo "==> Forcing light mode..."
adb shell cmd uimode night no

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

# Grant notification permission upfront so the dialog doesn't interrupt sign-in.
adb shell pm grant "$PKG" android.permission.POST_NOTIFICATIONS 2>/dev/null || true

echo "==> Signing in..."
$EMU launch "$PKG/$MAIN_ACTIVITY"
wait_for_ui 3
$EMU tap "More"
wait_for_ui 2
$EMU tap "myTBA"
wait_for_ui 2

# If not signed in, tap "Sign in with Google" and complete the Credential Manager flow.
if $EMU find "Sign in with Google" 2>/dev/null | grep -q "Sign in with Google"; then
  $EMU tap "Sign in with Google"
  wait_for_ui 3
  # Google Credential Manager shows a bottom sheet — tap "Continue" to select the account.
  $EMU tap "Continue"
  wait_for_ui 5
  echo "    Signed in successfully"
else
  echo "    Already signed in"
fi

# Favorite an event so the Events list shows a "Your Events" section.
deeplink "/event/2025casf"
wait_for_ui 3
if $EMU find "Add to favorites" 2>/dev/null | grep -q "Add to favorites"; then
  $EMU tap "Add to favorites"
  wait_for_ui 1
  echo "    Favorited 2025casf"
else
  echo "    2025casf already favorited"
fi

echo "==> Launching app..."
$EMU launch "$PKG/$MAIN_ACTIVITY"
wait_for_ui 5

# 1. Events list (home screen, showing "Your Events" section)
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

# 7. Home screen with Team Tracking widgets (177 at event, 254 upcoming)
# Uses MockWidgetActivity to inject deterministic data — no network needed.
if [ "$INCLUDE_WIDGETS" = true ]; then
  echo "==> Installing debug build for widget automation..."
  ./gradlew :app:installDebug -q
  adb shell appwidget grantbind --package "$DEV_PKG" --user 0
  echo "==> Screenshot 7: Home screen widgets"
  # Clean up any existing TBA widgets
  adb shell am start -n "$DEV_PKG/com.thebluealliance.android.widget.RemoveWidgetsActivity"
  wait_for_ui 1
  # Pin widget #0 (will be at-event)
  adb shell am start -n "$DEV_PKG/com.thebluealliance.android.widget.PinWidgetActivity" --es team 177
  wait_for_ui 1
  $EMU tap "Add to home screen"
  wait_for_ui 3
  # Pin widget #1 (will be upcoming)
  adb shell am start -n "$DEV_PKG/com.thebluealliance.android.widget.PinWidgetActivity" --es team 254
  wait_for_ui 3
  $EMU tap "Add to home screen"
  wait_for_ui 3
  # Inject mock data (no network needed — bypasses TeamTrackingWorker)
  adb shell am start -n "$DEV_PKG/com.thebluealliance.android.widget.MockWidgetActivity" --ei index 0 --es mock at-event
  wait_for_ui 2
  adb shell am start -n "$DEV_PKG/com.thebluealliance.android.widget.MockWidgetActivity" --ei index 1 --es mock upcoming
  wait_for_ui 2
  # Go to home screen — widgets are on the next page from the default
  adb shell input keyevent KEYCODE_HOME
  wait_for_ui 1
  adb shell input swipe 900 1200 200 1200 300
  wait_for_ui 1
  $EMU screenshot "$RAW_DIR/07-home-widgets.png"
else
  echo "==> Skipping widget screenshot (use --widgets to include)"
fi

# Copy to Play Store listing directory
echo "==> Copying to $DEST_DIR"
cp "$RAW_DIR/01-events-list.png"        "$DEST_DIR/1-events-list.png"
cp "$RAW_DIR/02-event-matches.png"      "$DEST_DIR/2-event-matches.png"
cp "$RAW_DIR/03-match-detail.png"       "$DEST_DIR/3-match-detail.png"
cp "$RAW_DIR/04-team-detail.png"        "$DEST_DIR/4-team-detail.png"
cp "$RAW_DIR/05-notification-prefs.png" "$DEST_DIR/5-notification-prefs.png"
cp "$RAW_DIR/06-district-detail.png"    "$DEST_DIR/6-district-detail.png"
if [ -f "$RAW_DIR/07-home-widgets.png" ]; then
  cp "$RAW_DIR/07-home-widgets.png"    "$DEST_DIR/7-home-widgets.png"
fi

# Clean up widgets from home screen
if [ "$INCLUDE_WIDGETS" = true ]; then
  echo "==> Cleaning up widgets..."
  adb shell am start -n "$DEV_PKG/com.thebluealliance.android.widget.RemoveWidgetsActivity"
  wait_for_ui 1
fi

# Disable demo mode to restore normal status bar
echo "==> Disabling demo mode..."
adb shell am broadcast -a com.android.systemui.demo -e command exit

echo "==> Done! Screenshots saved to $DEST_DIR"
ls -la "$DEST_DIR"
