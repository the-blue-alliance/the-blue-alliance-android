#!/usr/bin/env bash
# Build and publish releases to Google Play Store.
#
# Usage:
#   ./scripts/release.sh status               Show current releases on each track
#   ./scripts/release.sh alpha [--dry-run]    Build + publish to alpha track
#   ./scripts/release.sh beta [--dry-run]     Promote alpha → beta
#   ./scripts/release.sh production [--dry-run] Promote beta → production
set -euo pipefail

cd "$(dirname "$0")/.."

export JAVA_HOME="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BOLD='\033[1m'
NC='\033[0m'

DRY_RUN=false

read_local_property() {
    local key="$1"
    [[ -f local.properties ]] || return 0
    awk -v key="$key" 'index($0, key "=") == 1 { print substr($0, length(key) + 2); exit }' local.properties
}

# Load git remote from local.properties (default: origin)
GIT_REMOTE="origin"
configured_remote=$(read_local_property "release.git.remote" || true)
if [[ -n "$configured_remote" ]]; then
    GIT_REMOTE="$configured_remote"
fi

SLACK_WEBHOOK_URL=""
configured_slack_webhook=$(read_local_property "release.slack.webhook" || true)
if [[ -n "$configured_slack_webhook" ]]; then
    SLACK_WEBHOOK_URL="$configured_slack_webhook"
fi

GITHUB_TOKEN=""
configured_github_token=$(read_local_property "release.github.token" || true)
if [[ -n "$configured_github_token" ]]; then
    GITHUB_TOKEN="$configured_github_token"
fi

# ── Helpers ──────────────────────────────────────────────────────────────

die()  { echo -e "${RED}ERROR:${NC} $*" >&2; exit 1; }
info() { echo -e "${GREEN}▸${NC} $*"; }
warn() { echo -e "${YELLOW}▸${NC} $*"; }

run() {
    if $DRY_RUN; then
        echo -e "${YELLOW}[dry-run]${NC} $*"
    else
        "$@"
    fi
}

build_commitlog() {
    local base_tag=""
    base_tag=$(git describe HEAD~1 --tags --abbrev=0 2>/dev/null || true)

    if [[ -n "$base_tag" ]]; then
        git shortlog "${base_tag}..HEAD" --oneline --no-merges 2>/dev/null || true
    else
        git shortlog HEAD --oneline --no-merges 2>/dev/null || true
    fi
}

post_to_slack() {
    local message_body="$1"

    if [[ -z "$SLACK_WEBHOOK_URL" ]]; then
        warn "Slack webhook is not configured (set release.slack.webhook in local.properties)"
        return 0
    fi

    local payload
    payload=$(SLACK_TEXT="$message_body" python3 - <<'PY'
import json
import os

print(json.dumps({
    "text": os.environ["SLACK_TEXT"],
    "username": "release-bot",
    "icon_emoji": ":tba:",
}))
PY
)

    if $DRY_RUN; then
        echo "Would have posted ${payload} to slack"
        return 0
    fi

    local response_file
    response_file=$(mktemp)
    local status_code=""
    if ! status_code=$(curl -sS -o "$response_file" -w "%{http_code}" \
        -H 'Content-Type: application/json' \
        -X POST \
        --data "$payload" \
        "$SLACK_WEBHOOK_URL"); then
        warn "Failed to post Slack announcement"
        rm -f "$response_file"
        return 0
    fi

    if [[ "$status_code" != "200" ]]; then
        warn "Slack webhook returned HTTP ${status_code}: $(cat "$response_file")"
    fi
    rm -f "$response_file"
}

announce_release_action() {
    local action="$1"
    local release_link="https://github.com/the-blue-alliance/the-blue-alliance-android/releases"
    if [[ "$VERSION_NAME" != *"-dev."* ]]; then
        release_link="https://github.com/the-blue-alliance/the-blue-alliance-android/releases/tag/v${VERSION_NAME}"
    fi

    local commitlog
    commitlog=$(build_commitlog)
    if [[ -z "$commitlog" ]]; then
        commitlog="(no non-merge commits found)"
    fi

    local message_body
    message_body=$(cat <<EOF
${action} android v${VERSION_NAME} (${VERSION_CODE}).
\`\`\`
${commitlog}
\`\`\`
${release_link}
EOF
)

    post_to_slack "$message_body"
}

create_github_release() {
    local apk_path="app/build/outputs/apk/release/app-release.apk"

    if [[ -z "$GITHUB_TOKEN" ]]; then
        warn "GitHub token not configured (set release.github.token in local.properties), skipping GitHub release"
        return 0
    fi

    if [[ ! -f "$apk_path" ]]; then
        warn "Release APK not found at ${apk_path}, skipping GitHub release"
        return 0
    fi

    local tag="v${VERSION_NAME}"

    local commitlog
    commitlog=$(build_commitlog)
    if [[ -z "$commitlog" ]]; then
        commitlog="(no non-merge commits found)"
    fi

    local is_prerelease="false"
    if [[ "$VERSION_NAME" == *"-dev."* ]]; then
        is_prerelease="true"
    fi

    local create_payload
    create_payload=$(RELEASE_TAG="$tag" RELEASE_NAME="Android v${VERSION_NAME}" RELEASE_BODY="$commitlog" IS_PRERELEASE="$is_prerelease" python3 - <<'PY'
import json, os
print(json.dumps({
    "tag_name": os.environ["RELEASE_TAG"],
    "name": os.environ["RELEASE_NAME"],
    "body": os.environ["RELEASE_BODY"],
    "draft": False,
    "prerelease": os.environ["IS_PRERELEASE"] == "true",
}))
PY
)

    if $DRY_RUN; then
        echo "Would create GitHub release for ${tag} and upload ${apk_path}"
        return 0
    fi

    info "Creating GitHub release for ${tag}..."
    local response_file
    response_file=$(mktemp)
    local status_code
    status_code=$(curl -sS -o "$response_file" -w "%{http_code}" \
        -H "Authorization: Bearer ${GITHUB_TOKEN}" \
        -H "Content-Type: application/json" \
        -X POST \
        --data "$create_payload" \
        "https://api.github.com/repos/the-blue-alliance/the-blue-alliance-android/releases")

    if [[ "$status_code" != "201" ]]; then
        warn "GitHub release creation returned HTTP ${status_code}: $(cat "$response_file")"
        rm -f "$response_file"
        return 0
    fi

    local upload_url
    upload_url=$(python3 -c "
import json, sys
data = json.load(sys.stdin)
if 'upload_url' not in data:
    raise SystemExit('upload_url missing from GitHub API response')
print(data['upload_url'].split('{')[0])
" < "$response_file")
    rm -f "$response_file"

    info "Uploading APK to GitHub release..."
    local apk_name="the-blue-alliance-android-v${VERSION_NAME}.apk"
    local upload_response_file
    upload_response_file=$(mktemp)
    local upload_status
    upload_status=$(curl -sS -o "$upload_response_file" -w "%{http_code}" \
        -H "Authorization: Bearer ${GITHUB_TOKEN}" \
        -H "Content-Type: application/vnd.android.package-archive" \
        -X POST \
        --data-binary "@${apk_path}" \
        "${upload_url}?name=${apk_name}")

    if [[ "$upload_status" != "201" ]]; then
        warn "APK upload returned HTTP ${upload_status}: $(cat "$upload_response_file")"
    else
        info "APK uploaded to GitHub release"
    fi
    rm -f "$upload_response_file"
}

# ── Preflight checks ────────────────────────────────────────────────────

preflight() {
    local ok=true

    if [[ ! -f local.properties ]]; then
        echo -e "${RED}✗${NC} local.properties missing (need signing config)"
        ok=false
    else
        if ! grep -q 'release.store.file' local.properties; then
            echo -e "${RED}✗${NC} local.properties missing release.store.file"
            ok=false
        fi
    fi

    # Resolve service account path from local.properties or default
    local sa_path="play-service-account.json"
    configured=$(read_local_property "play.service.account.key" || true)
    if [[ -n "$configured" ]]; then
        sa_path="$configured"
    fi
    if [[ ! -f "$sa_path" ]]; then
        echo -e "${RED}✗${NC} Play service account not found at ${sa_path}"
        ok=false
    fi

    if [[ ! -f app/src/release/google-services.json ]]; then
        echo -e "${RED}✗${NC} app/src/release/google-services.json missing"
        ok=false
    fi
    if [[ ! -f app/src/debug/google-services.json ]]; then
        echo -e "${RED}✗${NC} app/src/debug/google-services.json missing"
        ok=false
    fi

    if ! $ok; then
        die "Preflight checks failed. Fix the issues above and retry."
    fi

    # Require a clean working tree (no staged or unstaged changes)
    if [[ -n "$(git status --porcelain)" ]]; then
        die "Working directory is not clean. Commit or stash all changes before releasing."
    fi

    # Fetch from the configured remote so git describe sees up-to-date release tags
    info "Fetching tags from ${GIT_REMOTE}..."
    git fetch "$GIT_REMOTE" --tags --quiet

    info "Preflight checks passed"
}

# ── Version helpers ──────────────────────────────────────────────────────

get_version_info() {
    local desc
    desc=$(git describe --tags --long --match 'v[0-9]*' 2>/dev/null || echo "")
    if [[ -z "$desc" ]]; then
        die "No version tags found. Create one first: git tag v1.0.0"
    fi

    # Parse vMAJOR.MINOR.PATCH-DISTANCE-gHASH
    if [[ "$desc" =~ ^v([0-9]+)\.([0-9]+)\.([0-9]+)-([0-9]+)-g([0-9a-f]+)$ ]]; then
        V_MAJOR="${BASH_REMATCH[1]}"
        V_MINOR="${BASH_REMATCH[2]}"
        V_PATCH="${BASH_REMATCH[3]}"
        COMMIT_DISTANCE="${BASH_REMATCH[4]}"
        COMMIT_HASH="${BASH_REMATCH[5]}"
    else
        die "Could not parse git describe output: $desc"
    fi

    VERSION_CODE=$(( V_MAJOR * 1000000 + V_MINOR * 10000 + V_PATCH * 100 + COMMIT_DISTANCE ))
    if [[ "$COMMIT_DISTANCE" -eq 0 ]]; then
        VERSION_NAME="${V_MAJOR}.${V_MINOR}.${V_PATCH}"
    else
        VERSION_NAME="${V_MAJOR}.${V_MINOR}.${V_PATCH}-dev.${COMMIT_DISTANCE}"
    fi
}

print_version() {
    echo ""
    echo -e "  ${BOLD}Version name:${NC}  $VERSION_NAME"
    echo -e "  ${BOLD}Version code:${NC}  $VERSION_CODE"
    echo -e "  ${BOLD}Git describe:${NC}  v${V_MAJOR}.${V_MINOR}.${V_PATCH}-${COMMIT_DISTANCE}-g${COMMIT_HASH}"
    echo ""
}

maybe_create_tag() {
    if [[ "$COMMIT_DISTANCE" -eq 0 ]]; then
        info "HEAD is already tagged v${V_MAJOR}.${V_MINOR}.${V_PATCH}"
        return
    fi

    local next_minor=$((V_MINOR + 1))
    local suggested="v${V_MAJOR}.${next_minor}.0"

    warn "HEAD is ${COMMIT_DISTANCE} commit(s) ahead of v${V_MAJOR}.${V_MINOR}.${V_PATCH}"
    echo ""
    read -rp "Create a new tag? [y/N] " create_tag
    if [[ "$create_tag" != [yY] ]]; then
        warn "Continuing without a new tag (version will include -dev suffix)"
        return
    fi

    read -rp "Tag version [${suggested}]: " tag_input
    local new_tag="${tag_input:-$suggested}"

    if [[ ! "$new_tag" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        die "Invalid tag format: $new_tag (expected vMAJOR.MINOR.PATCH)"
    fi

    run git tag "$new_tag"
    run git push "$GIT_REMOTE" "$new_tag"
    info "Created and pushed tag ${new_tag}"

    # Re-read version info with new tag
    if ! $DRY_RUN; then
        get_version_info
    fi
}

# ── Subcommands ──────────────────────────────────────────────────────────

cmd_alpha() {
    info "Publishing to alpha track"
    echo ""

    # Verify branch
    local branch
    branch=$(git rev-parse --abbrev-ref HEAD)
    if [[ "$branch" != "main" ]]; then
        die "Must be on main branch (currently on ${branch})"
    fi

    get_version_info
    print_version
    maybe_create_tag
    # Refresh after potential tag creation
    get_version_info
    print_version

    info "Building release bundle..."
    run ./gradlew :app:bundleRelease :app:assembleRelease

    info "Publishing to alpha..."
    run ./gradlew publishReleaseBundle

    echo ""
    echo -e "${GREEN}✓ Published to alpha${NC}"
    echo -e "  Version: ${VERSION_NAME} (${VERSION_CODE})"
    echo -e "  Track:   alpha"

    create_github_release
    announce_release_action "Published to alpha"
}

cmd_beta() {
    info "Promoting alpha → beta"
    echo ""

    get_version_info
    run ./gradlew promoteReleaseArtifact --from-track alpha --promote-track beta

    echo ""
    echo -e "${GREEN}✓ Promoted alpha → beta${NC}"

    announce_release_action "Promoted alpha to beta"
}

cmd_status() {
    info "Fetching Google Play track status..."
    echo ""

    # Resolve service account path
    local sa_path="play-service-account.json"
    local configured
    configured=$(read_local_property "play.service.account.key" || true)
    if [[ -n "$configured" ]]; then
        sa_path="$configured"
    fi

    SA_PATH="$sa_path" python3 - <<'PY'
import json, os, sys, time, urllib.request, urllib.error

sa_path = os.environ["SA_PATH"]
with open(sa_path) as f:
    sa = json.load(f)

# Build JWT for Google OAuth2
import hashlib, hmac, base64, struct

def b64url(data):
    return base64.urlsafe_b64encode(data).rstrip(b"=").decode()

header = b64url(json.dumps({"alg": "RS256", "typ": "JWT"}).encode())
now = int(time.time())
claims = b64url(json.dumps({
    "iss": sa["client_email"],
    "scope": "https://www.googleapis.com/auth/androidpublisher",
    "aud": "https://oauth2.googleapis.com/token",
    "iat": now,
    "exp": now + 3600,
}).encode())
signing_input = f"{header}.{claims}".encode()

# RS256 signing using the service account private key
try:
    from cryptography.hazmat.primitives import hashes, serialization
    from cryptography.hazmat.primitives.asymmetric import padding

    private_key = serialization.load_pem_private_key(sa["private_key"].encode(), password=None)
    signature = private_key.sign(signing_input, padding.PKCS1v15(), hashes.SHA256())
except ImportError:
    # Fall back to openssl subprocess
    import subprocess, tempfile
    with tempfile.NamedTemporaryFile(mode="w", suffix=".pem", delete=False) as kf:
        kf.write(sa["private_key"])
        kf_path = kf.name
    try:
        result = subprocess.run(
            ["openssl", "dgst", "-sha256", "-sign", kf_path],
            input=signing_input, capture_output=True, check=True,
        )
        signature = result.stdout
    finally:
        os.unlink(kf_path)

jwt_token = f"{header}.{claims}.{b64url(signature)}"

# Exchange JWT for access token
token_req = urllib.request.Request(
    "https://oauth2.googleapis.com/token",
    data=urllib.parse.urlencode({
        "grant_type": "urn:ietf:params:oauth:grant-type:jwt-bearer",
        "assertion": jwt_token,
    }).encode(),
    headers={"Content-Type": "application/x-www-form-urlencoded"},
)
import urllib.parse
try:
    with urllib.request.urlopen(token_req) as resp:
        token_data = json.loads(resp.read())
except urllib.error.HTTPError as e:
    print(f"Failed to get access token: {e.code} {e.read().decode()}", file=sys.stderr)
    sys.exit(1)

access_token = token_data["access_token"]
package = "com.thebluealliance.androidclient"

# Create an edit to read track info
edit_req = urllib.request.Request(
    f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{package}/edits",
    data=b"{}",
    headers={
        "Authorization": f"Bearer {access_token}",
        "Content-Type": "application/json",
    },
    method="POST",
)
try:
    with urllib.request.urlopen(edit_req) as resp:
        edit = json.loads(resp.read())
except urllib.error.HTTPError as e:
    print(f"Failed to create edit: {e.code} {e.read().decode()}", file=sys.stderr)
    sys.exit(1)

edit_id = edit["id"]

BOLD = "\033[1m"
GREEN = "\033[0;32m"
YELLOW = "\033[1;33m"
RED = "\033[0;31m"
DIM = "\033[2m"
NC = "\033[0m"

# Google Play API track names → Play Console display names
tracks = [
    ("production", "Production"),
    ("beta", "Open testing"),
    ("alpha", "Closed testing"),
    ("internal", "Internal testing"),
]
for track_id, display_name in tracks:
    url = f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{package}/edits/{edit_id}/tracks/{track_id}"
    req = urllib.request.Request(url, headers={"Authorization": f"Bearer {access_token}"})
    try:
        with urllib.request.urlopen(req) as resp:
            track_data = json.loads(resp.read())
    except urllib.error.HTTPError as e:
        if e.code == 404:
            print(f"{BOLD}{display_name}{NC} {DIM}({track_id}){NC}: (empty)")
            continue
        print(f"Failed to fetch track {track_id}: {e.code} {e.read().decode()}", file=sys.stderr)
        continue

    releases = track_data.get("releases", [])
    print(f"{BOLD}{display_name}{NC} {DIM}({track_id}){NC}")
    if not releases:
        print(f"  (no releases)")
    for release in releases:
        status = release.get("status", "unknown")
        version_codes = release.get("versionCodes", [])
        name = release.get("name", "")
        fraction = release.get("userFraction")

        status_colors = {
            "completed": GREEN,
            "inProgress": YELLOW,
            "draft": YELLOW,
            "halted": RED,
        }
        color = status_colors.get(status, "")
        status_str = f"{color}{status}{NC}" if color else status

        version_str = ", ".join(str(v) for v in version_codes)
        line = f"  {status_str}  {BOLD}{name}{NC}" if name else f"  {status_str}"
        if version_str:
            line += f"  {DIM}(versionCode {version_str}){NC}"
        if fraction is not None:
            line += f"  {YELLOW}{fraction:.0%} rollout{NC}"
        print(line)
    print()

print(f"{DIM}Note: Google's review status is not available via the API.{NC}")
print(f"{DIM}Check the Play Console for review progress.{NC}")

# Delete the edit (we only read, don't commit)
delete_url = f"https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{package}/edits/{edit_id}"
delete_req = urllib.request.Request(delete_url, headers={"Authorization": f"Bearer {access_token}"}, method="DELETE")
try:
    urllib.request.urlopen(delete_req)
except urllib.error.HTTPError:
    pass  # Best effort cleanup
PY
}

cmd_production() {
    info "Promoting beta → production"
    echo ""

    echo -e "${RED}${BOLD}You are about to promote to PRODUCTION.${NC}"
    read -rp "Are you sure? Type 'yes' to confirm: " confirm
    if [[ "$confirm" != "yes" ]]; then
        die "Aborted."
    fi

    run ./gradlew promoteReleaseArtifact --from-track beta --promote-track production

    info "Publishing store listing..."
    run ./gradlew publishListing

    echo ""
    echo -e "${GREEN}✓ Promoted beta → production${NC}"

    get_version_info
    announce_release_action "Promoted beta to production"
}

# ── Main ─────────────────────────────────────────────────────────────────

usage() {
    echo "Usage: $0 <command> [--dry-run]"
    echo ""
    echo "Commands:"
    echo "  status      Show current releases on each Google Play track"
    echo "  alpha       Build release bundle and publish to alpha track"
    echo "  beta        Promote current alpha to beta"
    echo "  production  Promote current beta to production"
    echo ""
    echo "Options:"
    echo "  --dry-run   Show what would happen without executing"
}

# Parse args
COMMAND=""
for arg in "$@"; do
    case "$arg" in
        --dry-run) DRY_RUN=true ;;
        status|alpha|beta|production) COMMAND="$arg" ;;
        -h|--help) usage; exit 0 ;;
        *) die "Unknown argument: $arg" ;;
    esac
done

if [[ -z "$COMMAND" ]]; then
    usage
    exit 1
fi

if $DRY_RUN; then
    warn "Dry-run mode — no changes will be made"
    echo ""
fi

preflight

case "$COMMAND" in
    status)     cmd_status ;;
    alpha)      cmd_alpha ;;
    beta)       cmd_beta ;;
    production) cmd_production ;;
esac
