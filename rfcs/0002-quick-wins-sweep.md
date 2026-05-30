# RFC 0002: Quick-wins sweep — remove dead weight & dedup

- **Status:** Draft / Request for Comments
- **Author:** @gregmarra
- **Date:** 2026-05-29
- **Type:** Cleanup / refactor (low risk)

## Summary

A batch of small, independent, low-risk simplifications surfaced by a
maintainability audit. None changes behavior. Each sub-item can be its own small
PR, or we land them together — this RFC is to agree on the set and priorities.

## Background

The codebase is genuinely clean on debt markers (2 TODOs total, no
FIXME/commented-out blocks, a well-structured version catalog). The wins below
are about removing leftovers and collapsing copy-paste, not paying down rot.

## Proposed items

### 1. Delete stale legacy module directories — confidence: high
`tba-api/`, `libTba/`, `libImgur/` at repo root contain **only** `build/`
artifacts (e.g. `libTba/build/libs/tbaMobile-v9-1.21.0-SNAPSHOT.jar`), zero
source, zero git-tracked files, and zero references. They are not Gradle modules
(`settings.gradle` includes only `:app`, `:wear`) — leftovers from the
pre-Compose app.
**Action:** `rm -rf tba-api libTba libImgur` (local cleanup — no diff since
nothing is tracked). Optional: add them to `.gitignore` if they keep
regenerating.

### 2. Build convention plugin for the duplicated app/wear config — confidence: high
`app/build.gradle.kts:24-66` and `wear/build.gradle.kts:20-58` are byte-for-byte
identical git-tag versioning except the wear offset constant; `signingConfigs`,
debug `buildTypes` (TBA_BASE_URL / API_KEY), `compileOptions`, and `lint` blocks
are near-duplicated too.
**Action:** extract a `build-logic/` (or `buildSrc/`) convention plugin
(`tba.android.app`) holding versioning + signing + buildTypes; both modules
apply it. (Overlaps with RFC 0004; can land independently.)

### 3. Dedup formatting/display helpers — confidence: high
- `RpDots` is defined twice, near-identically: `MatchDetailScreen.kt:472` and
  `MatchList.kt:369` → move one copy into `ui/components/`.
- `formatMatchTime` exists in `MatchList.kt:360` and `MatchDetailViewModel.kt:111`
  (and again in the widget/wear workers — see RFC 0004) → one shared helper.
- Team-number display via `removePrefix("frc")` appears in **13 places** across 8
  files (e.g. `EventRankingsTab.kt:283`), and `EventInsightsTab.kt:645` uses
  `teamKey.substring(3)` for the same thing → one `String.teamNumber` extension.
- Win-loss-tie record string `"${wins}-${losses}-${ties}"` is inlined in
  `EventRankingsTab.kt:292` and `TeamEventDetailScreen.kt:372` → `Ranking.recordString`.

### 4. One section-header component — confidence: high
The indigo `.background(TBAIndigo400)` header is reimplemented ~4 ways:
`EventsScreen.kt:490` (`SimpleSectionHeader`), `TeamEventDetailScreen.kt:349-363`
(inline, 3× in one file), `EventRankingsTab.kt`, separate from the sticky variant
in `components/SectionHeader.kt`.
**Action:** one `SectionHeader(label, sticky = false)` covering both cases.

### 5. Drop 18 redundant DAO providers — confidence: high
`DatabaseModule.kt:44-84` hand-writes a `provideXDao` for every DAO, yet each
repository also injects `TBADatabase`. Either inject only `TBADatabase` and call
`db.xDao()` in repos (delete all 18 providers), or stop double-injecting `db` +
`dao` where only the DAO is used.

### 6. Delete dead code — confidence: high
`EventRepository.observeTeamEventKeys` (`:105-106`) has zero callers.

### 7. Collapse the duplicate PitMap route — confidence: medium
`Screen.EventPitMap` (`Screen.kt:60-69`, `TBANavigation.kt:389-409`) exists only
to carry a raw `teamsCsv` string, which the nav entry then re-parses into a
`PitMap` and renders with the same screen/VM. Have `DeeplinkMatcher`
(`DeeplinkMatcher.kt:14`) split the CSV and emit `Screen.PitMap` directly; drop
the redundant route + entry.

### 8. Hoist duplicated navigation transitions — confidence: high
`TBANavigation.kt:96-125` defines `transitionSpec`, `popTransitionSpec`, and
`predictivePopTransitionSpec` — the latter two identical, all three the same
horizontal-slide pattern. Hoist into the existing `navigation/Transitions.kt`.
Also standardize the ~10 `hiltViewModel(creationCallback = …)` blocks (two
spellings exist) behind one `tbaViewModel` helper.

## Suggested order

1, 5, 6 (pure deletions) → 3, 4, 8 (mechanical dedup) → 2, 7 (slightly more
involved). Items are independent; pick any subset.

## Scope / risk

Low across the board — no behavior change. Each item is independently testable
(build + existing unit tests; spot-check affected screens in the emulator for the
UI items). Confirm each audit-identified line reference before implementing.

## Decision requested

Which items are in scope, and do you want one bundled PR per item or a single
sweep PR? On approval I'll implement the agreed subset.
