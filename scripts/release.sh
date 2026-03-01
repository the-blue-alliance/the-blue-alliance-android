#!/usr/bin/env bash
# Build and publish releases to Google Play Store.
#
# Usage:
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

# Load git remote from local.properties (default: origin)
GIT_REMOTE="origin"
if [[ -f local.properties ]]; then
    local configured
    configured=$(grep 'release.git.remote' local.properties 2>/dev/null | cut -d= -f2- || true)
    if [[ -n "$configured" ]]; then
        GIT_REMOTE="$configured"
    fi
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
    if [[ -f local.properties ]]; then
        local configured
        configured=$(grep 'play.service.account.key' local.properties 2>/dev/null | cut -d= -f2- || true)
        if [[ -n "$configured" ]]; then
            sa_path="$configured"
        fi
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

    # Verify branch and working tree
    local branch
    branch=$(git rev-parse --abbrev-ref HEAD)
    if [[ "$branch" != "main" ]]; then
        die "Must be on main branch (currently on ${branch})"
    fi
    if [[ -n "$(git diff --stat HEAD)" ]]; then
        die "Working tree has uncommitted changes. Commit or stash them first."
    fi

    get_version_info
    print_version
    maybe_create_tag
    # Refresh after potential tag creation
    get_version_info
    print_version

    info "Building release bundle..."
    run ./gradlew :app:bundleRelease

    info "Publishing to alpha..."
    run ./gradlew publishReleaseBundle

    echo ""
    echo -e "${GREEN}✓ Published to alpha${NC}"
    echo -e "  Version: ${VERSION_NAME} (${VERSION_CODE})"
    echo -e "  Track:   alpha"
}

cmd_beta() {
    info "Promoting alpha → beta"
    echo ""

    run ./gradlew promoteReleaseArtifact --from-track alpha --promote-track beta

    echo ""
    echo -e "${GREEN}✓ Promoted alpha → beta${NC}"
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
}

# ── Main ─────────────────────────────────────────────────────────────────

usage() {
    echo "Usage: $0 <command> [--dry-run]"
    echo ""
    echo "Commands:"
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
        alpha|beta|production) COMMAND="$arg" ;;
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
    alpha)      cmd_alpha ;;
    beta)       cmd_beta ;;
    production) cmd_production ;;
esac
