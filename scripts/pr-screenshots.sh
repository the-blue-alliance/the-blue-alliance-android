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
#   --row LABEL BEFORE AFTER   A before/after pair. Renders one table row:
#                              Before | After | Diff. The diff is computed here
#                              (changed pixels in magenta over a dimmed "after")
#                              with the % of pixels changed under it, so every
#                              visual claim carries its proof in the same row.
#                              Before and after must be the same size — a
#                              mismatched crop is not a valid comparison.
#   --shot LABEL FILE          A single image (e.g. a state with no "before").
#   --no-diff                  Skip the Diff column (needs python3 + Pillow
#                              otherwise: `pip3 install Pillow`).
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
DIFF=true
declare -a KINDS=() LABELS=() FILES_A=() FILES_B=()

die() { echo "error: $*" >&2; exit 1; }

while [[ $# -gt 0 ]]; do
  case "$1" in
    --pr)     PR="${2:?--pr needs a number}"; shift 2 ;;
    --branch) BRANCH="${2:?--branch needs a value}"; shift 2 ;;
    --no-diff) DIFF=false; shift ;;
    --row)
      LABELS+=("${2:?--row needs LABEL}"); FILES_A+=("${3:?--row needs BEFORE}")
      FILES_B+=("${4:?--row needs AFTER}"); KINDS+=("row"); shift 4 ;;
    --shot)
      LABELS+=("${2:?--shot needs LABEL}"); FILES_A+=("${3:?--shot needs FILE}")
      FILES_B+=(""); KINDS+=("shot"); shift 3 ;;
    -h|--help) sed -n '2,46p' "$0"; exit 0 ;;
    *) die "unknown argument: $1" ;;
  esac
done

[[ ${#KINDS[@]} -gt 0 ]] || die "nothing to upload — pass at least one --row or --shot"
command -v gh >/dev/null || die "gh CLI not found"
command -v git >/dev/null || die "git not found"
if $DIFF; then
  python3 -c 'import PIL' 2>/dev/null \
    || die "the Diff column needs python3 + Pillow (pip3 install Pillow), or pass --no-diff"
fi

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
DIFF_DIR="$(mktemp -d -t tba-shots-diff.XXXXXX)"
trap 'rm -f "$GIT_INDEX_FILE"; rm -rf "$DIFF_DIR"' EXIT

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

# Write a diff image of BEFORE vs AFTER to OUT; echo the % of pixels changed.
# A pixel counts as changed when its luminance difference exceeds 16/255, which
# ignores compression noise but catches any real rendering change.
make_diff() {
  python3 - "$1" "$2" "$3" <<'PY'
import sys
from PIL import Image, ImageChops
b, a, out = sys.argv[1:4]
B, A = Image.open(b).convert("RGB"), Image.open(a).convert("RGB")
if B.size != A.size:
    sys.exit(f"error: size mismatch {B.size} vs {A.size}: {b} / {a} (crop both the same way)")
mask = ImageChops.difference(B, A).convert("L").point(lambda v: 255 if v > 16 else 0)
changed = mask.histogram()[255]
diff = Image.blend(Image.new("RGB", A.size), A, 0.25)
diff.paste(Image.new("RGB", A.size, (255, 0, 200)), mask=mask)
diff.save(out, format="PNG")
print(f"{100 * changed / (A.size[0] * A.size[1]):.1f}%")
PY
}

echo "Staging ${#KINDS[@]} item(s) for branch '${BRANCH}'…" >&2

# Rows render as one table and single shots follow it, whatever order the flags
# came in: a shot between two rows would otherwise split the markdown table.
TABLE=""; SHOTS=""
for i in "${!KINDS[@]}"; do
  label="${LABELS[$i]}"
  if [[ "${KINDS[$i]}" == "row" ]]; then
    b="$(stage "${FILES_A[$i]}")"; a="$(stage "${FILES_B[$i]}")"
    if $DIFF; then
      after_name="$(basename "${FILES_B[$i]}")"
      diff_path="${DIFF_DIR}/${after_name%.*}_diff.png"
      pct="$(make_diff "${FILES_A[$i]}" "${FILES_B[$i]}" "$diff_path")" || die "diff failed for: ${label}"
      d="$(stage "$diff_path")"
      TABLE+="| **${label}** | <img src=\"${b}\" width=\"240\"> | <img src=\"${a}\" width=\"240\"> | <img src=\"${d}\" width=\"240\"><br><sub>${pct} of pixels changed</sub> |"$'\n'
    else
      TABLE+="| **${label}** | <img src=\"${b}\" width=\"300\"> | <img src=\"${a}\" width=\"300\"> |"$'\n'
    fi
  else
    u="$(stage "${FILES_A[$i]}")"
    SHOTS+=$'\n'"**${label}**"$'\n\n'"<img src=\"${u}\" width=\"320\">"$'\n'
  fi
done

MD="## Screenshots"$'\n'
if [[ -n "$TABLE" ]]; then
  if $DIFF; then
    MD+=$'\n'"| | Before | After | Diff |"$'\n'"|---|---|---|---|"$'\n'
  else
    MD+=$'\n'"| | Before | After |"$'\n'"|---|---|---|"$'\n'
  fi
  MD+="$TABLE"
fi
MD+="$SHOTS"

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
