#!/usr/bin/env bash
# Per-worktree Android emulator instances for parallel verification.
#
# Model: ONE shared base AVD is your "real", interactive emulator. You bless its
# current state into a named snapshot; each worktree then boots a *read-only*
# instance from that snapshot on its own console port / serial. Read-only
# instances write to a throwaway copy-on-write overlay, so they can NEVER modify
# the base AVD — verification runs can't corrupt your real emulator.
#
#   worktree-emu.sh bless            # save the running base AVD's state as the verify snapshot
#   worktree-emu.sh up    <slot>     # boot a read-only verify instance for <slot>; prints its serial
#   worktree-emu.sh serial <slot>    # print the serial for <slot> (no boot)
#   worktree-emu.sh down  <slot>     # shut down the instance for <slot>
#   worktree-emu.sh list             # show all slots and their status
#
# Orchestrator usage:
#   export ANDROID_SERIAL="$(scripts/worktree-emu.sh up "$SLOT")"
#   ./gradlew :app:installDebug          # adb + AGP both honor ANDROID_SERIAL
#   scripts/emu screenshot out.png       # scripts/emu reads ANDROID_SERIAL too
#
# Env overrides:
#   TBA_BASE_AVD         (default: Medium_Phone)  the shared base AVD
#   TBA_VERIFY_SNAPSHOT  (default: verify-base)   named snapshot the fleet boots from
#   TBA_WT_CAP           (default: 3)             max concurrent instances this host sustains
#   TBA_WT_PORT_BASE     (default: 5580)          first console port; slot N -> base + N*2
#                                                 (keeps 5554-5578 free for interactive use)
set -euo pipefail

SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
EMULATOR="$SDK/emulator/emulator"
ADB="$SDK/platform-tools/adb"
BASE_AVD="${TBA_BASE_AVD:-Medium_Phone}"
SNAPSHOT="${TBA_VERIFY_SNAPSHOT:-verify-base}"
CAP="${TBA_WT_CAP:-3}"
PORT_BASE="${TBA_WT_PORT_BASE:-5580}"
BOOT_TIMEOUT="${TBA_WT_BOOT_TIMEOUT:-180}"

port_for()   { echo $(( PORT_BASE + $1 * 2 )); }
serial_for() { echo "emulator-$(port_for "$1")"; }

die() { echo "worktree-emu: $*" >&2; exit 1; }

require_slot() {
  [[ "${1:-}" =~ ^[0-9]+$ ]] || die "slot must be a non-negative integer"
  (( $1 < CAP )) || die "slot $1 exceeds cap $CAP (this host sustains $CAP concurrent instances; raise TBA_WT_CAP if you have the RAM)"
}

is_running() { "$ADB" devices | grep -q "^$1[[:space:]]"; }

# Find a running RW base instance of BASE_AVD. Fleet slots also report the same
# avd name, so only consider emulators on console ports BELOW the fleet range.
base_serial() {
  local s port
  for s in $("$ADB" devices | awk '/^emulator-/{print $1}'); do
    port="${s#emulator-}"
    (( port >= PORT_BASE )) && continue   # skip fleet slots
    if [[ "$("$ADB" -s "$s" emu avd name 2>/dev/null | head -1 | tr -d '\r')" == "$BASE_AVD" ]]; then
      echo "$s"; return 0
    fi
  done
  return 1
}

wait_boot() {
  local serial="$1" i=0
  "$ADB" -s "$serial" wait-for-device
  until [[ "$("$ADB" -s "$serial" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; do
    (( i++ >= BOOT_TIMEOUT )) && die "$serial did not finish booting within ${BOOT_TIMEOUT}s (see /tmp/worktree-emu-*.log)"
    sleep 1
  done
}

cmd_bless() {
  local s
  s="$(base_serial)" || die "base AVD '$BASE_AVD' is not running — start it (e.g. scripts/run-emulator.sh $BASE_AVD), get it into the state you want, then bless"
  echo "Blessing snapshot '$SNAPSHOT' from $s ($BASE_AVD)..." >&2
  "$ADB" -s "$s" emu avd snapshot save "$SNAPSHOT"
  echo "Done. Worktrees can now boot from it: scripts/worktree-emu.sh up <slot>" >&2
}

cmd_up() {
  require_slot "$1"
  local port serial base; port="$(port_for "$1")"; serial="$(serial_for "$1")"
  if is_running "$serial"; then echo "$serial"; return 0; fi
  [[ -d "$HOME/.android/avd/${BASE_AVD}.avd/snapshots/${SNAPSHOT}" ]] \
    || die "snapshot '$SNAPSHOT' not found for $BASE_AVD — run 'scripts/worktree-emu.sh bless' first"
  # Sequential model: the read-only fleet can't share an AVD with a read-write
  # base instance. Fail clearly here instead of the emulator's cryptic lock error.
  if base="$(base_serial)"; then
    die "base AVD '$BASE_AVD' is running read-write on $base — close it before booting the read-only fleet (sequential model). Bless first if you have unsaved golden state."
  fi
  echo "Booting read-only verify instance: slot=$1 serial=$serial avd=$BASE_AVD snapshot=$SNAPSHOT" >&2
  "$EMULATOR" -avd "$BASE_AVD" \
    -read-only \
    -snapshot "$SNAPSHOT" \
    -no-snapshot-save \
    -ports "$port,$((port + 1))" \
    -no-window -no-audio -no-boot-anim \
    -gpu swiftshader_indirect \
    >"/tmp/worktree-emu-$1.log" 2>&1 &
  wait_boot "$serial"
  echo "$serial"
}

cmd_down() {
  require_slot "$1"
  local serial; serial="$(serial_for "$1")"
  is_running "$serial" && "$ADB" -s "$serial" emu kill >/dev/null 2>&1 || true
  echo "stopped $serial" >&2
}

cmd_list() {
  printf '%-5s %-18s %s\n' SLOT SERIAL STATUS
  local i serial
  for (( i = 0; i < CAP; i++ )); do
    serial="$(serial_for "$i")"
    if is_running "$serial"; then
      printf '%-5s %-18s %s\n' "$i" "$serial" "running ($BASE_AVD @ $SNAPSHOT)"
    else
      printf '%-5s %-18s %s\n' "$i" "$serial" "-"
    fi
  done
}

case "${1:-}" in
  bless)  cmd_bless ;;
  up)     cmd_up "${2:-}" ;;
  serial) require_slot "${2:-}"; serial_for "$2" ;;
  down)   cmd_down "${2:-}" ;;
  list)   cmd_list ;;
  *) echo "usage: $0 {bless | up <slot> | serial <slot> | down <slot> | list}" >&2; exit 2 ;;
esac
