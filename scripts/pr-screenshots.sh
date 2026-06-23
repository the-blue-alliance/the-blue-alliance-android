#!/usr/bin/env bash
#
# Host PR screenshots on GitHub WITHOUT committing them to the repo's history.
#
# Why this exists: GitHub has no scriptable API for the drag-and-drop image
# uploads you get in the web editor, this org has *immutable releases* on (so
# release assets can't be used as a re-writable bucket), and we don't want
# screenshot PNGs landing in `main`. The trick: park the images on a dedicated
# orphan branch (`screenshot-assets`) with its own unrelated history that is
# never merged. They serve publicly from raw.githubusercontent.com and render
# inline in any PR description.
#
# This touches origin only via one `git push` of the asset branch. It does the
# whole commit with pure plumbing and an isolated index, so your working tree,
# index, and current branch are never disturbed.
#
# Usage:
#   scripts/pr-screenshots.sh [--pr N] [--branch NAME] \
#       --row "Label" before.png after.png \
#       [--row "Other"  before2.png after2.png] ... \
#       [--shot "Label" single.png] ...
#
#   --row LABEL BEFORE AFTER   A before/after pair (renders a 2-col table row).
#   --shot LABEL FILE          A single image.
#   --pr N                     Also rewrite PR #N's body with a Screenshots
#                              section (between markers, so re-runs replace it).
#                              Omit to just print the markdown to stdout.
#   --branch NAME              Asset branch to push to (default: screenshot-assets).
#
# Examples:
#   scripts/pr-screenshots.sh --row "District rankings" \
#       artifacts/1428_districts_before.png artifacts/1428_districts_after.png
#
#   scripts/pr-screenshots.sh --pr 1428 \
#       --row "District rankings" before.png after.png \
#       --shot "New empty state" empty.png
#
set -euo pipefail

BRANCH="screenshot-assets"
PR=""
declare -a KINDS=() LABELS=() FILES_A=() FILES_B=()

die() { echo "error: $*" >&2; exit 1; }

while [[ $# -gt 0 ]]; do
  case "$1" in
    --pr)     PR="${2:?--pr needs a number}"; shift 2 ;;
    --branch) BRANCH="${2:?--branch needs a value}"; shift 2 ;;
    --row)
      LABELS+=("${2:?--row needs LABEL}"); FILES_A+=("${3:?--row needs BEFORE}")
      FILES_B+=("${4:?--row needs AFTER}"); KINDS+=("row"); shift 4 ;;
    --shot)
      LABELS+=("${2:?--shot needs LABEL}"); FILES_A+=("${3:?--shot needs FILE}")
      FILES_B+=(""); KINDS+=("shot"); shift 3 ;;
    -h|--help) sed -n '2,40p' "$0"; exit 0 ;;
    *) die "unknown argument: $1" ;;
  esac
done

[[ ${#KINDS[@]} -gt 0 ]] || die "nothing to upload — pass at least one --row or --shot"
command -v gh >/dev/null || die "gh CLI not found"
command -v git >/dev/null || die "git not found"

REPO="$(gh repo view --json nameWithOwner -q .nameWithOwner)"
SLUG="$(git rev-parse --abbrev-ref HEAD 2>/dev/null | tr -c 'a-zA-Z0-9._-' '-' | sed 's/-\{2,\}/-/g;s/^-//;s/-$//')"
[[ -n "$SLUG" ]] || SLUG="shots"

# Stage every requested file into an isolated index so the real index/worktree
# are never touched. Each asset path embeds an 8-char content hash, which both
# de-dupes identical re-uploads and guarantees a fresh URL when content changes
# (GitHub's camo image proxy caches by URL, so a stable name would go stale).
export GIT_INDEX_FILE
GIT_INDEX_FILE="$(mktemp -t tba-shots-idx.XXXXXX)"
rm -f "$GIT_INDEX_FILE"
trap 'rm -f "$GIT_INDEX_FILE"' EXIT

# Resolve the existing asset branch (if any) so images accumulate over time.
PARENT=""
if git fetch -q origin "$BRANCH" 2>/dev/null; then
  PARENT="$(git rev-parse FETCH_HEAD)"
  git read-tree "$PARENT^{tree}"
fi

# Stage a local file; echo back its raw.githubusercontent URL.
stage() {
  local path="$1"
  [[ -f "$path" ]] || die "file not found: $path"
  local base ext stem sha blob asset
  base="$(basename "$path")"
  ext="${base##*.}"; stem="${base%.*}"
  blob="$(git hash-object -w --path "$path" "$path")"
  sha="${blob:0:8}"
  asset="$(printf '%s-%s.%s' "$stem" "$sha" "$ext" | tr -c 'a-zA-Z0-9._-' '-')"
  local relpath="shots/${SLUG}/${asset}"
  git update-index --add --cacheinfo "100644,${blob},${relpath}"
  echo "https://raw.githubusercontent.com/${REPO}/${BRANCH}/${relpath}"
}

echo "Staging ${#KINDS[@]} item(s) for branch '${BRANCH}'…" >&2

MD="## Screenshots"$'\n'
HAVE_TABLE=false
for i in "${!KINDS[@]}"; do
  label="${LABELS[$i]}"
  if [[ "${KINDS[$i]}" == "row" ]]; then
    b="$(stage "${FILES_A[$i]}")"; a="$(stage "${FILES_B[$i]}")"
    if ! $HAVE_TABLE; then
      MD+=$'\n'"| | Before | After |"$'\n'"|---|---|---|"$'\n'; HAVE_TABLE=true
    fi
    MD+="| **${label}** | <img src=\"${b}\" width=\"300\"> | <img src=\"${a}\" width=\"300\"> |"$'\n'
  else
    u="$(stage "${FILES_A[$i]}")"
    MD+=$'\n'"**${label}**"$'\n\n'"<img src=\"${u}\" width=\"320\">"$'\n'
  fi
done

# Commit the staged tree and push just that ref. Orphan (no parent) on first
# use; otherwise parented on the current tip so history accumulates.
TREE="$(git write-tree)"
if [[ -n "$PARENT" ]]; then
  COMMIT="$(git commit-tree "$TREE" -p "$PARENT" -m "screenshots: ${SLUG}")"
else
  COMMIT="$(git commit-tree "$TREE" -m "screenshots: ${SLUG}")"
fi
echo "Pushing asset commit to origin/${BRANCH}…" >&2
git push origin "${COMMIT}:refs/heads/${BRANCH}"

if [[ -n "$PR" ]]; then
  echo "Updating PR #${PR} body…" >&2
  BODY="$(gh pr view "$PR" --repo "$REPO" --json body -q .body)"
  START="<!-- screenshots:start -->"; END="<!-- screenshots:end -->"
  BLOCK="${START}"$'\n'"${MD}${END}"
  if [[ "$BODY" == *"$START"* ]]; then
    NEW="$(BODY="$BODY" BLOCK="$BLOCK" START="$START" END="$END" python3 - <<'PY'
import os, re
print(re.sub(re.escape(os.environ["START"]) + r".*?" + re.escape(os.environ["END"]),
             lambda _: os.environ["BLOCK"], os.environ["BODY"], flags=re.S), end="")
PY
)"
  else
    NEW="${BODY}"$'\n\n'"${BLOCK}"
  fi
  gh pr edit "$PR" --repo "$REPO" --body "$NEW" >/dev/null
  echo "✓ PR #${PR} updated." >&2
else
  echo >&2
  echo "─── paste into your PR description ───" >&2
  printf '%s\n' "$MD"
fi
