#!/usr/bin/env bash
# Run a local TBA server for signed-in (myTBA) testing of debug builds.
#
# Usage: scripts/local-backend.sh up|seed|down|status
#
#   up      Start the server's docker compose stack (datastore, Firebase emulators, dev
#           appserver) under its own compose project, or reuse one already on :8080.
#   seed    Import a few real events (and their teams) from thebluealliance.com, then give
#           the emulator's test user (uid 2) favorites and subscriptions through the same
#           clientapi call the app makes.
#   down    Stop the stack this script started (its datastore volume is kept).
#   status  Show the containers, whether the API answers, and the test user's myTBA counts.
#
# Debug builds already use the Firebase Auth emulator at 10.0.2.2:9099, and in debug
# "Sign in with Google" signs in as uid 2 without Google. The emulator issues tokens for
# project demo-test, which is the project the server verifies against, so nothing else
# needs configuring. The build only has to target the local server; see the end of `up`.
#
# Environment:
#   TBA_SERVER_DIR       the-blue-alliance checkout (default: next to this repo's main clone)
#   TBA_COMPOSE_PROJECT  compose project name (default: tba-android)
#   TBA_APIV3_KEY        a thebluealliance.com read key for `seed` (default: the key saved
#                        by `tba auth login`, from github.com/the-blue-alliance/tba-cli)
#   TBA_SEED_EVENTS      events to import (default: 2026cthar 2026casnv 2026cur; the seeded
#                        favorites name teams and events from these)
set -euo pipefail

BASE=http://localhost:8080
AUTH_EMULATOR=http://localhost:9099
PROJECT="${TBA_COMPOSE_PROJECT:-tba-android}"
SEED_EVENTS="${TBA_SEED_EVENTS:-2026cthar 2026casnv 2026cur}"

repo_main_clone() {
  local common
  common="$(git -C "$(dirname "$0")" rev-parse --path-format=absolute --git-common-dir)"
  dirname "$common"
}
SERVER_DIR="${TBA_SERVER_DIR:-$(dirname "$(repo_main_clone)")/the-blue-alliance}"

die() { echo "local-backend: $*" >&2; exit 1; }

compose() {
  [[ -f "$SERVER_DIR/docker-compose.yml" ]] ||
    die "no docker-compose.yml in $SERVER_DIR (set TBA_SERVER_DIR)"
  (cd "$SERVER_DIR" && docker compose -p "$PROJECT" "$@")
}

# Both modules the app needs: web (/local, which also creates tba-dev-key) and api
# (/clientapi answers a JSON 401 without a token, and an HTML 404 until it has started).
api_up() {
  [[ "$(curl -s -o /dev/null -m 10 -w '%{http_code}' "$BASE/local/bootstrap")" == 200 ]] &&
    [[ "$(curl -s -m 10 -X POST -d '{}' "$BASE/clientapi/tbaClient/v9/favorites/list")" == *'"code":401'* ]]
}

ours_running() {
  [[ -n "$(compose ps -q tba 2>/dev/null)" ]]
}

wait_for_api() {
  echo "Waiting for the dev appserver (first start installs dependencies, ~2-5 min)..."
  local i
  for i in $(seq 1 200); do
    api_up && return 0
    sleep 3
  done
  die "server did not answer at $BASE/local/bootstrap; see: docker compose -p $PROJECT logs tba"
}

print_build_steps() {
  cat <<EOF

Local server: $BASE   Auth emulator: $AUTH_EMULATOR (UI on :4000)

Build a debug APK that targets it (the -P flags beat local.properties):
  ./gradlew :app:assembleDebug -Ptba.url.debug=http://10.0.2.2:8080/ -Ptba.api.key.debug=tba-dev-key

Install on one emulator and sign in (never ./gradlew installDebug, it installs on every device):
  adb -s \$ANDROID_SERIAL install -r app/build/outputs/apk/debug/app-debug.apk
  scripts/emu launch com.thebluealliance.androidclient.development/com.thebluealliance.android.MainActivity
  scripts/emu tap More && scripts/emu tap myTBA && scripts/emu tap "Sign in with Google"  # as uid 2
EOF
}

cmd_up() {
  if api_up; then
    if ours_running; then
      echo "Already running (compose project $PROJECT)."
    else
      echo "A TBA server already answers on $BASE; reusing it (not started by this script)."
    fi
  else
    if lsof -nP -iTCP:8080 -iTCP:9099 -sTCP:LISTEN >/dev/null 2>&1 && ! ours_running; then
      die "ports 8080/9099 are taken by something that isn't a TBA server; free them first"
    fi
    echo "Starting $SERVER_DIR as compose project $PROJECT..."
    compose up -d --build firebase datastore tba
    wait_for_api
  fi
  print_build_steps
}

apiv3_key() {
  if [[ -n "${TBA_APIV3_KEY:-}" ]]; then
    printf '%s' "$TBA_APIV3_KEY"
    return
  fi
  local cfg="$HOME/.config/tba/auth.yaml"
  [[ -f "$cfg" ]] && awk '/thebluealliance\.com\/api\/v3:/ {v = $2; gsub(/^["\047]|["\047]$/, "", v); print v; exit}' "$cfg"
}

# A Firebase ID token for uid 2 from the Auth emulator, minted the way the app's debug
# sign-in does it: a fake Google ID token whose sub matches the seeded user's Google link.
emulator_id_token() {
  curl -s -m 20 -X POST \
    "$AUTH_EMULATOR/identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=local" \
    -H 'Content-Type: application/json' \
    -d '{"postBody":"id_token={\"sub\":\"2\",\"email\":\"user@thebluealliance.com\",\"email_verified\":true}&providerId=google.com","requestUri":"http://localhost","returnSecureToken":true}' |
    python3 -c 'import json, sys; print(json.load(sys.stdin)["idToken"])'
}

clientapi() {
  curl -s -m 60 -X POST "$BASE/clientapi/tbaClient/v9/$1" \
    -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d "$2"
}

# set_pref <model_key> <model_type: 0 event, 1 team, 2 match> <favorite> <notifications JSON>
set_pref() {
  local out
  out="$(clientapi model/setPreferences \
    "{\"model_key\":\"$1\",\"model_type\":$2,\"device_key\":\"\",\"favorite\":$3,\"notifications\":$4}")"
  case "$out" in
    *'code\": 200'* | *'code\": 304'*) echo "  $1" ;;
    *) die "setPreferences $1 failed: $out" ;;
  esac
}

cmd_seed() {
  api_up || die "server isn't up; run: scripts/local-backend.sh up"
  local key
  key="$(apiv3_key)"
  [[ -n "$key" ]] ||
    die "set TBA_APIV3_KEY to a thebluealliance.com read key (thebluealliance.com/account)"

  echo "Importing events from thebluealliance.com:"
  local event status
  for event in $SEED_EVENTS; do
    status="$(curl -s -m 300 -o /dev/null -w '%{redirect_url}' -X POST "$BASE/local/bootstrap" \
      --data-urlencode "bootstrap_key=$event" --data-urlencode "apiv3_key=$key")"
    [[ "$status" == *status=success* ]] || die "bootstrap $event failed ($status)"
    echo "  $event"
  done

  TOKEN="$(emulator_id_token)" || die "couldn't get a token from the Auth emulator"
  echo "Favorites and subscriptions for uid 2:"
  set_pref frc177 1 true '["upcoming_match","match_score","awards_posted"]'
  set_pref frc254 1 true '["upcoming_match","match_score","alliance_selection","awards_posted","match_video"]'
  set_pref frc125 1 true '[]'
  set_pref frc1987 1 false '["match_score"]'
  set_pref 2026cthar 0 true '["upcoming_match","match_score","starting_comp_level","awards_posted"]'
  set_pref 2026cur 0 true '[]'
  set_pref 2026casnv 0 false '["schedule_updated","alliance_selection"]'
  cmd_status
}

cmd_status() {
  if [[ -f "$SERVER_DIR/docker-compose.yml" ]]; then compose ps || true; fi
  if ! api_up; then
    echo "API: not answering at $BASE"
    return
  fi
  echo "API: up at $BASE"
  TOKEN="$(emulator_id_token 2>/dev/null)" || { echo "Auth emulator: not answering"; return; }
  python3 -c 'import json, sys
fav = json.loads(sys.argv[1]).get("favorites", [])
sub = json.loads(sys.argv[2]).get("subscriptions", [])
print("uid 2: %d favorites (%s), %d subscriptions (%s)" % (
    len(fav), " ".join(f["model_key"] for f in fav),
    len(sub), " ".join(s["model_key"] for s in sub)))' \
    "$(clientapi favorites/list '{}')" "$(clientapi subscriptions/list '{}')"
}

cmd_down() {
  ours_running || { echo "Compose project $PROJECT isn't running; nothing to stop."; return; }
  compose down
}

case "${1:-}" in
  up) cmd_up ;;
  seed) cmd_seed ;;
  down) cmd_down ;;
  status) cmd_status ;;
  *) sed -n '2,25p' "$0" | sed 's/^# \{0,1\}//'; exit 2 ;;
esac
