#!/usr/bin/env bash
# Build and publish releases to Google Play Store.
#
# Usage:
#   ./scripts/release.sh status               Show current releases on each track
#   ./scripts/release.sh alpha [--dry-run]    Build + publish to alpha track
#   ./scripts/release.sh beta [--dry-run]     Promote alpha → beta
#   ./scripts/release.sh production [--dry-run] Promote beta → production
#   ./scripts/release.sh listing [--dry-run]  Publish store listing only
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

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Query the Play API for the version name + code on a given track.
# Sets VERSION_NAME and VERSION_CODE globals.
get_track_version() {
    local track="$1"
    local sa_path
    sa_path=$(resolve_sa_path)

    local result
    result=$(python3 "$SCRIPT_DIR/play_api.py" --sa-path "$sa_path" track-version "$track")

    VERSION_NAME=$(echo "$result" | cut -f1)
    VERSION_CODE=$(echo "$result" | cut -f2)

    if [[ -z "$VERSION_NAME" ]]; then
        warn "Could not determine version from ${track} track"
    else
        info "Version on ${track}: ${VERSION_NAME} (${VERSION_CODE})"
    fi
}

# Resolve service account path from local.properties or default.
resolve_sa_path() {
    local sa_path="play-service-account.json"
    local configured
    configured=$(read_local_property "play.service.account.key" || true)
    if [[ -n "$configured" ]]; then
        sa_path="$configured"
    fi
    echo "$sa_path"
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

    local wear_version_code=$(( VERSION_CODE + 100000000 ))
    local message_body
    message_body=$(cat <<EOF
${action} android v${VERSION_NAME} (phone: ${VERSION_CODE}, wear: ${wear_version_code}).
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
    local wear_apk_path="wear/build/outputs/apk/release/wear-release.apk"

    if ! command -v gh &>/dev/null; then
        warn "gh CLI not found, skipping GitHub release"
        return 0
    fi

    local tag="v${VERSION_NAME}"
    local commitlog
    commitlog=$(build_commitlog)
    if [[ -z "$commitlog" ]]; then
        commitlog="(no non-merge commits found)"
    fi

    local -a gh_args=(
        "$tag"
        --title "Android v${VERSION_NAME}"
        --notes "$commitlog"
        --repo "the-blue-alliance/the-blue-alliance-android"
    )

    if [[ "$VERSION_NAME" == *"-dev."* ]]; then
        gh_args+=(--prerelease)
    fi

    # Attach APKs that exist
    if [[ -f "$apk_path" ]]; then
        gh_args+=("${apk_path}#the-blue-alliance-android-v${VERSION_NAME}.apk")
    fi
    if [[ -f "$wear_apk_path" ]]; then
        gh_args+=("${wear_apk_path}#the-blue-alliance-wear-v${VERSION_NAME}.apk")
    fi

    if $DRY_RUN; then
        echo "Would run: gh release create ${gh_args[*]}"
        return 0
    fi

    info "Creating GitHub release for ${tag}..."
    if ! gh release create "${gh_args[@]}"; then
        warn "GitHub release creation failed"
    fi
}

# ── Preflight checks ────────────────────────────────────────────────────

preflight() {
    local mode="${1:-full}"   # "full" for alpha/production, "light" for beta/listing/status
    local ok=true

    if [[ "$mode" == "full" ]]; then
        if [[ ! -f local.properties ]]; then
            echo -e "${RED}✗${NC} local.properties missing (need signing config)"
            ok=false
        else
            if ! grep -q 'release.store.file' local.properties; then
                echo -e "${RED}✗${NC} local.properties missing release.store.file"
                ok=false
            fi
        fi

        if [[ ! -f app/src/release/google-services.json ]]; then
            echo -e "${RED}✗${NC} app/src/release/google-services.json missing"
            ok=false
        fi
        if [[ ! -f app/src/debug/google-services.json ]]; then
            echo -e "${RED}✗${NC} app/src/debug/google-services.json missing"
            ok=false
        fi
        if [[ ! -f wear/src/release/google-services.json ]]; then
            echo -e "${RED}✗${NC} wear/src/release/google-services.json missing"
            ok=false
        fi
    fi

    local sa_path
    sa_path=$(resolve_sa_path)
    if [[ ! -f "$sa_path" ]]; then
        echo -e "${RED}✗${NC} Play service account not found at ${sa_path}"
        ok=false
    fi

    if ! $ok; then
        die "Preflight checks failed. Fix the issues above and retry."
    fi

    if [[ "$mode" == "full" ]]; then
        # Require a clean working tree (no staged or unstaged changes)
        if [[ -n "$(git status --porcelain)" ]]; then
            die "Working directory is not clean. Commit or stash all changes before releasing."
        fi

        # Fetch from the configured remote so git describe sees up-to-date release tags
        info "Fetching tags from ${GIT_REMOTE}..."
        git fetch "$GIT_REMOTE" --tags --quiet
    fi

    info "Preflight checks passed"
}

# ── Version helpers ──────────────────────────────────────────────────────

get_version_info() {
    local desc
    desc=$(git describe --tags --long --match 'v[0-9]*' --exclude '*-dev*' 2>/dev/null || echo "")
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
    local wear_version_code=$(( VERSION_CODE + 100000000 ))
    echo ""
    echo -e "  ${BOLD}Version name:${NC}       $VERSION_NAME"
    echo -e "  ${BOLD}Phone version code:${NC} $VERSION_CODE"
    echo -e "  ${BOLD}Wear version code:${NC}  $wear_version_code"
    echo -e "  ${BOLD}Git describe:${NC}       v${V_MAJOR}.${V_MINOR}.${V_PATCH}-${COMMIT_DISTANCE}-g${COMMIT_HASH}"
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

# ── Device install ───────────────────────────────────────────────────────

maybe_install_on_device() {
    local apk_path="app/build/outputs/apk/release/app-release.apk"

    if ! command -v adb &>/dev/null; then
        return 0
    fi

    # Collect connected devices (skip the header line and empty lines)
    local devices
    devices=$(adb devices 2>/dev/null | tail -n +2 | grep -v '^\s*$' | grep 'device$' || true)

    if [[ -z "$devices" ]]; then
        return 0
    fi

    local device_count
    device_count=$(echo "$devices" | wc -l | tr -d ' ')

    echo ""
    info "Found ${device_count} connected device(s):"
    echo "$devices" | while IFS= read -r line; do
        echo "    $line"
    done
    echo ""

    read -rp "Install the release APK on connected device(s) before publishing? [y/N] " install_answer
    if [[ "$install_answer" != [yY] ]]; then
        info "Skipping device install"
        return 0
    fi

    if [[ ! -f "$apk_path" ]]; then
        warn "APK not found at ${apk_path}, skipping device install"
        return 0
    fi

    if $DRY_RUN; then
        echo -e "${YELLOW}[dry-run]${NC} adb install -r \"${apk_path}\""
        return 0
    fi

    info "Installing ${apk_path} on connected device(s)..."
    if ! adb install -r "$apk_path"; then
        warn "adb install failed — continuing with publish anyway"
    else
        info "APK installed successfully"
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

    info "Building release bundles..."
    run ./gradlew :app:bundleRelease :app:assembleRelease :wear:bundleRelease :wear:assembleRelease

    maybe_install_on_device

    info "Publishing phone app to alpha..."
    run ./gradlew :app:publishReleaseBundle
    info "Publishing wear app to alpha..."
    run ./gradlew :wear:publishReleaseBundle

    local wear_version_code=$(( VERSION_CODE + 100000000 ))
    echo ""
    echo -e "${GREEN}✓ Published to alpha${NC}"
    echo -e "  Version: ${VERSION_NAME}"
    echo -e "  Phone:   ${VERSION_CODE}"
    echo -e "  Wear:    ${wear_version_code}"
    echo -e "  Track:   alpha"

    if [[ "$VERSION_NAME" != *"-dev."* ]]; then
        create_github_release
    else
        info "Skipping GitHub release for dev version"
    fi
    announce_release_action "Published to alpha"
}

cmd_beta() {
    info "Promoting alpha → beta"
    echo ""

    # Resolve the version currently on the alpha track
    get_track_version alpha

    if [[ -z "$VERSION_NAME" ]]; then
        die "Could not determine version on alpha track"
    fi

    local tag="v${VERSION_NAME}"

    # Capture the current branch (or commit SHA if in detached HEAD state)
    local original_ref
    if original_ref=$(git symbolic-ref --quiet --short HEAD 2>/dev/null); then
        : # on a named branch
    else
        original_ref=$(git rev-parse HEAD)
    fi

    info "Fetching tags from ${GIT_REMOTE}..."
    run git fetch "$GIT_REMOTE" --tags --quiet

    if ! $DRY_RUN && ! git rev-parse "$tag" >/dev/null 2>&1; then
        die "Tag ${tag} not found. Ensure the release was properly tagged."
    fi

    info "Checking out ${tag}..."
    run git checkout "$tag"

    info "Promoting phone app alpha → beta..."
    run ./gradlew :app:promoteReleaseArtifact --from-track alpha --promote-track beta
    info "Promoting wear app alpha → beta..."
    run ./gradlew :wear:promoteReleaseArtifact --from-track "wear:alpha" --promote-track "wear:beta"

    info "Returning to ${original_ref}..."
    run git checkout "$original_ref"

    echo ""
    echo -e "${GREEN}✓ Promoted alpha → beta (phone + wear)${NC}"

    # Get the actual version from the beta track (now that it's been promoted)
    get_track_version beta
    announce_release_action "Promoted alpha to beta"
}

cmd_status() {
    local sa_path
    sa_path=$(resolve_sa_path)
    python3 "$SCRIPT_DIR/play_api.py" --sa-path "$sa_path" status
}

cmd_listing() {
    info "Publishing store listing (screenshots, descriptions, etc.)"
    echo ""

    run ./gradlew :app:publishReleaseListing

    echo ""
    echo -e "${GREEN}✓ Store listing updated${NC}"
}

cmd_production() {
    info "Promoting beta → production"
    echo ""

    echo -e "${RED}${BOLD}You are about to promote to PRODUCTION.${NC}"
    read -rp "Are you sure? Type 'yes' to confirm: " confirm
    if [[ "$confirm" != "yes" ]]; then
        die "Aborted."
    fi

    # Resolve the version currently on the beta track
    get_track_version beta

    if [[ -z "$VERSION_NAME" ]]; then
        die "Could not determine version on beta track"
    fi

    local tag="v${VERSION_NAME}"

    # Capture the current branch (or commit SHA if in detached HEAD state)
    local original_ref
    if original_ref=$(git symbolic-ref --quiet --short HEAD 2>/dev/null); then
        : # on a named branch
    else
        original_ref=$(git rev-parse HEAD)
    fi

    if ! $DRY_RUN && ! git rev-parse "$tag" >/dev/null 2>&1; then
        die "Tag ${tag} not found. Ensure the release was properly tagged."
    fi

    info "Checking out ${tag}..."
    run git checkout "$tag"

    info "Promoting phone app beta → production..."
    run ./gradlew :app:promoteReleaseArtifact --from-track beta --promote-track production
    info "Promoting wear app beta → production..."
    run ./gradlew :wear:promoteReleaseArtifact --from-track "wear:beta" --promote-track "wear:production"

    info "Publishing store listing..."
    run ./gradlew :app:publishReleaseListing

    info "Returning to ${original_ref}..."
    run git checkout "$original_ref"

    echo ""
    echo -e "${GREEN}✓ Promoted beta → production (phone + wear)${NC}"

    # Get the actual version from the production track (now that it's been promoted)
    get_track_version production
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
    echo "  listing     Publish store listing (screenshots, descriptions) only"
    echo ""
    echo "Options:"
    echo "  --dry-run   Show what would happen without executing"
}

# Parse args
COMMAND=""
for arg in "$@"; do
    case "$arg" in
        --dry-run) DRY_RUN=true ;;
        status|alpha|beta|production|listing) COMMAND="$arg" ;;
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

case "$COMMAND" in
    alpha|production) preflight full ;;
    *)               preflight light ;;
esac

case "$COMMAND" in
    status)     cmd_status ;;
    alpha)      cmd_alpha ;;
    beta)       cmd_beta ;;
    production) cmd_production ;;
    listing)    cmd_listing ;;
esac
