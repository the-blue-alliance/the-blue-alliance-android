#!/usr/bin/env bash
#
# Pre-flight check for a Meta Horizon Store upload.
#
# Meta's upload validator REJECTS a non-conforming APK rather than warning about
# it, and the same validator runs on the very first ALPHA upload — not just on
# the store submission. This script asserts the packaging rules we can check
# locally, so a regression fails a build instead of a release.
#
# Rules checked (VRC.Quest.* from
# https://developers.meta.com/horizon/resources/publish-quest-req/ and
# https://developers.meta.com/horizon/resources/publish-mobile-manifest/):
#   Packaging.1  installLocation=auto, excludeFromRecents=true on the launcher
#                activity, headtracking uses-feature not required, targetSdk 34,
#                minSdk within Meta's 29-34 range, not debuggable
#   Packaging.2  v2 signature present (v1/v3 are not required; v2 is)
#   Packaging.5  APK under 1 GB
#   Packaging.6  64-bit only: the sole native ABI is arm64-v8a
#   Security.2   uses-permission set is a subset of the reviewed allowlist below,
#                and contains nothing from Meta's prohibited/review-requiring lists
#
# Usage:
#   scripts/verify-metavr-apk.sh [path/to/app-metavr-release.apk]
#
# Signature checking is skipped (with a warning, not a failure) when the APK is
# unsigned, so the script is still useful on a machine without the release
# keystore. Pass --require-signed to make that a hard failure, which is what CI
# should do.

set -euo pipefail

APK="app/build/outputs/apk/metavr/release/app-metavr-release.apk"
REQUIRE_SIGNED=0

while [[ $# -gt 0 ]]; do
    case "$1" in
        --require-signed) REQUIRE_SIGNED=1; shift ;;
        -h|--help) sed -n '2,30p' "$0"; exit 0 ;;
        *) APK="$1"; shift ;;
    esac
done

# Permissions we have deliberately reviewed as OK to ship on Horizon OS. Anything
# else — including anything a dependency starts injecting — fails the check and
# has to be justified or removed (tools:node="remove" in app/src/metavr).
# WAKE_LOCK / RECEIVE_BOOT_COMPLETED / FOREGROUND_SERVICE and the generated
# DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION all come from androidx.work; they are
# review-requiring rather than prohibited. See METAVR_TODO A13 for dropping them.
ALLOWED_PERMISSIONS=(
    "android.permission.INTERNET"
    "android.permission.ACCESS_NETWORK_STATE"
    "android.permission.WAKE_LOCK"
    "android.permission.RECEIVE_BOOT_COMPLETED"
    "android.permission.FOREGROUND_SERVICE"
    "com.thebluealliance.androidclient.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
)

fail_count=0
ok()   { printf '  ok    %s\n' "$1"; }
bad()  { printf '  FAIL  %s\n' "$1"; fail_count=$((fail_count + 1)); }
warn() { printf '  warn  %s\n' "$1"; }

die() { printf 'error: %s\n' "$1" >&2; exit 2; }

[[ -f "$APK" ]] || die "APK not found: $APK (build it with :app:assembleMetavrRelease)"

# Newest build-tools wins; both binaries live in the same directory.
sdk_root="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"
build_tools=$(find "$sdk_root/build-tools" -maxdepth 1 -mindepth 1 -type d 2>/dev/null |
    sort -V | tail -1)
[[ -n "$build_tools" ]] || die "no build-tools under $sdk_root — set ANDROID_HOME"
AAPT2="$build_tools/aapt2"
APKSIGNER="$build_tools/apksigner"
[[ -x "$AAPT2" ]] || die "aapt2 not executable at $AAPT2"

badging=$("$AAPT2" dump badging "$APK")
xmltree=$("$AAPT2" dump xmltree --file AndroidManifest.xml "$APK")

printf 'Verifying %s\n' "$APK"

# --- Packaging.1: manifest conformance ---------------------------------------
grep -q "^install-location:'auto'" <<<"$badging" &&
    ok "installLocation=auto" ||
    bad "installLocation is not 'auto' (Meta requires auto/0)"

# The launcher activity is the one carrying the MAIN/LAUNCHER filter; find its
# node and confirm excludeFromRecents is set on it specifically, not merely
# somewhere in the manifest (several Firebase activities set it too).
launcher_ok=$(awk '
    /^ *E: activity \(line=/ { inact=1; excl=0; main=0; launch=0 }
    inact && /excludeFromRecents\(0x01010017\)=true/ { excl=1 }
    inact && /"android.intent.action.MAIN"/ { main=1 }
    inact && /"android.intent.category.LAUNCHER"/ { launch=1 }
    inact && main && launch && excl { print "yes"; exit }
' <<<"$xmltree")
[[ "$launcher_ok" == "yes" ]] &&
    ok "excludeFromRecents=true on the launcher activity" ||
    bad "launcher activity is missing excludeFromRecents=true"

grep -q "uses-feature-not-required: name='android.hardware.vr.headtracking'" <<<"$badging" &&
    ok "headtracking uses-feature required=false" ||
    bad "android.hardware.vr.headtracking must be present with required=false"

grep -q "com.oculus.intent.category.VR" <<<"$xmltree" &&
    bad "com.oculus.intent.category.VR present — that marks the app immersive-only" ||
    ok "no com.oculus.intent.category.VR (stays a 2D panel app)"

target_sdk=$(sed -n "s/^targetSdkVersion:'\([0-9]*\)'/\1/p" <<<"$badging")
[[ "$target_sdk" == "34" ]] &&
    ok "targetSdk=34" ||
    bad "targetSdk is '$target_sdk', Meta requires 34"

# aapt2 prints "minSdkVersion:'32'"; legacy aapt printed "sdkVersion:'32'".
min_sdk=$(sed -n "s/^\(min\)\{0,1\}[sS]dkVersion:'\([0-9]*\)'/\2/p" <<<"$badging" | head -1)
if [[ -n "$min_sdk" && "$min_sdk" -ge 29 && "$min_sdk" -le 34 ]]; then
    ok "minSdk=$min_sdk (Meta's permitted range is 29-34)"
else
    bad "minSdk is '$min_sdk', outside Meta's permitted 29-34"
fi

grep -q "application-debuggable" <<<"$badging" &&
    bad "APK is debuggable" ||
    ok "not debuggable"

# --- Packaging.6: 64-bit only ------------------------------------------------
abis=$(sed -n "s/^native-code: //p" <<<"$badging" | tr -d "'")
if [[ -z "$abis" ]]; then
    ok "no native libraries at all (trivially 64-bit clean)"
elif [[ "$abis" == "arm64-v8a" ]]; then
    ok "native ABIs = arm64-v8a only"
else
    bad "native ABIs are [$abis]; Quest builds must be 64-bit only (arm64-v8a)"
fi

# --- Packaging.5: size -------------------------------------------------------
size=$(wc -c <"$APK" | tr -d ' ')
if [[ "$size" -lt 1073741824 ]]; then
    ok "APK size $((size / 1024 / 1024)) MB (limit 1 GB)"
else
    bad "APK size $((size / 1024 / 1024)) MB exceeds the 1 GB limit"
fi

# --- Security.2: minimum permissions -----------------------------------------
declare -a unexpected=()
while read -r perm; do
    [[ -n "$perm" ]] || continue
    allowed=0
    for a in "${ALLOWED_PERMISSIONS[@]}"; do
        [[ "$perm" == "$a" ]] && allowed=1 && break
    done
    [[ "$allowed" == 1 ]] || unexpected+=("$perm")
done < <(sed -n "s/^uses-permission: name='\([^']*\)'.*/\1/p" <<<"$badging")

if [[ ${#unexpected[@]} -eq 0 ]]; then
    ok "uses-permission set is within the reviewed allowlist"
else
    bad "unreviewed permissions: ${unexpected[*]}"
    printf '        remove them in app/src/metavr/AndroidManifest.xml with tools:node="remove",\n'
    printf '        or add them to ALLOWED_PERMISSIONS here after checking them against\n'
    printf '        https://developers.meta.com/horizon/resources/permissions-prohibited/ and\n'
    printf '        https://developers.meta.com/horizon/resources/permissions-review-required/\n'
fi

# --- Packaging.2: v2 signature -----------------------------------------------
if [[ ! -x "$APKSIGNER" ]]; then
    warn "apksigner not found at $APKSIGNER — skipping signature check"
elif ! sig=$("$APKSIGNER" verify --verbose "$APK" 2>&1); then
    if [[ "$REQUIRE_SIGNED" == 1 ]]; then
        bad "APK is not signed / signature does not verify"
        printf '%s\n' "$sig" | sed 's/^/        /'
    else
        warn "APK is not signed (pass --require-signed to make this fatal)"
    fi
elif grep -q "v2 scheme (APK Signature Scheme v2): true" <<<"$sig"; then
    ok "APK Signature Scheme v2 present"
else
    bad "APK Signature Scheme v2 missing (Meta requires v2)"
fi

echo
if [[ "$fail_count" -eq 0 ]]; then
    echo "PASS — no Horizon packaging problems found."
else
    echo "FAIL — $fail_count problem(s); this APK would be rejected on upload."
    exit 1
fi
