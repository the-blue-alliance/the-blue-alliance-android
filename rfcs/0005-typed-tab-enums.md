# RFC 0005: Typed per-screen `Tab` enums for `refreshTab(...)`

- **Status:** Draft / Request for Comments
- **Author:** @gregmarra
- **Date:** 2026-05-30
- **Type:** Refactor (mechanical, contained)

## Summary

Four detail ViewModels still take a raw `Int` page index in `refreshTab(tab: Int)`
and dispatch with `when (tab) { 0 -> …, 1 -> … }`. The screen-side caller passes
the `HorizontalPager` page number directly. The mapping of "0 means …, 1 means
…" lives only in `//` comments next to each case. Replace each with a typed
per-screen enum so the dispatch becomes exhaustive and the meaning of each
position is named.

The precedent already exists in this repo: `EventDetailViewModel.refreshTab(tab:
EventDetailTab)` does exactly this — its screen calls
`viewModel.refreshTab(EventDetailTab.entries[page])`.

## Problem — `refreshTab(Int)` open-codes the tab order in two places

`when (tab) { 0 -> …; 1 -> … }` with `// Info` / `// Events` / `// Media` comments
is the form in four ViewModels. The same ordering is independently re-declared
in each screen's `private val TABS = listOf("…")`. The two lists have to stay in
lockstep but the type system doesn't enforce it.

**Evidence (raw-`Int` sites):**

- `app/src/main/kotlin/com/thebluealliance/android/ui/districts/DistrictDetailViewModel.kt:117`
  — `fun refreshTab(tab: Int)`, `when (tab) { 0 -> events, 1 -> rankings }`;
  paired with `DistrictDetailScreen.kt:56` `private val TABS = listOf("Events", "Rankings")`
  and `DistrictDetailScreen.kt:154` `onRefresh = { viewModel.refreshTab(page) }`.
- `app/src/main/kotlin/com/thebluealliance/android/ui/teams/TeamDetailViewModel.kt:203`
  — `fun refreshTab(tab: Int)`, `when (tab) { 0 -> // Info, 1 -> // Events, 2 -> // Media }`;
  paired with `TeamDetailScreen.kt:78` `listOf("Info", "Events", "Media")`
  and `TeamDetailScreen.kt:321` `onRefresh = { viewModel.refreshTab(page) }`.
- `app/src/main/kotlin/com/thebluealliance/android/ui/mytba/MyTBAViewModel.kt:139`
  — `fun refreshTab(tab: Int)`, `when (tab) { 0 -> favorites, 1 -> subscriptions }`;
  paired with `MyTBAScreen.kt:74` `listOf("Favorites", "Notifications")`
  and `MyTBAScreen.kt:273` `onRefresh = { viewModel.refreshTab(page) }`.
- `app/src/main/kotlin/com/thebluealliance/android/ui/teamevent/TeamEventDetailViewModel.kt:256`
  — `fun refreshTab(tab: Int)`, `when (tab) { 0 -> // Summary, 1 -> // Matches, 2 -> // Media, 3 -> // Stats, 4 -> // Awards }`;
  paired with `TeamEventDetailScreen.kt:70` `listOf("Summary", "Matches", "Media", "Stats", "Awards")`
  and `TeamEventDetailScreen.kt:201` `onRefresh = { viewModel.refreshTab(page) }`.

**Precedent (already typed):**

- `app/src/main/kotlin/com/thebluealliance/android/ui/events/detail/EventDetailScreen.kt:72`
  defines `enum class EventDetailTab(val readableName: (Event?) -> String)` with
  `INFO`, `TEAMS`, `RANKINGS`, `MATCHES`, `ALLIANCES`, `INSIGHTS`,
  `ADVANCEMENT_POINTS`, `AWARDS`.
- `EventDetailViewModel.kt:377` `fun refreshTab(tab: EventDetailTab)` with
  exhaustive `when (tab) { EventDetailTab.INFO -> … }`.
- `EventDetailScreen.kt:336` `onRefresh = { viewModel.refreshTab(EventDetailTab.entries[page]) }`.

## Why this matters

1. **Two ordered lists must agree.** The screen's `TABS = listOf("…")` and the
   ViewModel's `when (tab)` cases are independent declarations of the same tab
   order. Reordering or inserting a tab requires editing both; the compiler
   doesn't notice if you forget. We already discovered an asymmetry while
   auditing this: `TeamEventDetailViewModel.refreshTab` only handles `0..3` even
   though `TeamEventDetailScreen.TABS` has 5 entries — i.e. tapping pull-to-
   refresh on tab `4` (Awards) silently does nothing.
2. **`when (Int)` is non-exhaustive.** The compiler can't tell you a case is
   missing, only `// fallthrough` comments can — and they don't. With an enum,
   `when` over `EnumClass` is exhaustive and the missing branch is a build
   error.
3. **The `// Info` / `// Events` comments are the only documentation.** A named
   enum constant is self-documenting and survives a rename refactor; a comment
   doesn't.
4. **Inconsistency.** One detail screen (Event) already uses the typed pattern;
   the other four don't. Picking one approach removes the "which detail screen
   am I in" cognitive tax.

## Proposal

For each of the four screens, add a top-level `enum class <Screen>Tab` next to
the existing `enum class EventDetailTab` pattern (defined in the screen file,
since the readable name is a UI concern), change the ViewModel's `refreshTab`
signature to take the enum, and update the single screen call site to do
`<Screen>Tab.entries[page]`.

```kotlin
// DistrictDetailScreen.kt — replace `private val TABS = listOf("Events", "Rankings")`
enum class DistrictDetailTab(val readableName: String) {
    EVENTS("Events"),
    RANKINGS("Rankings"),
}

// DistrictDetailViewModel.kt
fun refreshTab(tab: DistrictDetailTab) {
    viewModelScope.launch {
        _isRefreshing.value = true
        try {
            val key = "${_selectedYear.value}$districtAbbreviation"
            when (tab) {
                DistrictDetailTab.EVENTS -> runCatching { eventRepository.refreshDistrictEvents(key) }
                DistrictDetailTab.RANKINGS -> runCatching { districtRepository.refreshDistrictRankings(key) }
            }
        } finally {
            _isRefreshing.value = false
        }
    }
}

// DistrictDetailScreen.kt — at the call site
onRefresh = { viewModel.refreshTab(DistrictDetailTab.entries[page]) },
```

The screen-side `TabRow` iterates over `<Screen>Tab.entries` and reads
`tab.readableName` instead of indexing into the parallel `TABS` list.

**Repeat for:**
- `enum class TeamDetailTab { INFO, EVENTS, MEDIA }`
- `enum class MyTBATab { FAVORITES, NOTIFICATIONS }`
- `enum class TeamEventDetailTab { SUMMARY, MATCHES, MEDIA, STATS, AWARDS }`

`EventDetailTab` is already in this shape — no changes there.

## Alternatives considered

1. **Leave as-is.** Risk: silent drift between the screen's `TABS` list and
   the ViewModel's `when` cases (already observed in `TeamEventDetail`'s
   missing tab-4 case). Rejected.
2. **One shared `enum class Tab`** across screens. Tabs are per-screen by
   nature (Info/Events/Media doesn't fit MyTBA, Favorites/Notifications doesn't
   fit District); a shared enum would be a grab-bag. Rejected.
3. **`@IntDef`-style typed-int**. Doesn't get exhaustive `when`, doesn't
   self-document. Rejected — Kotlin enums are the idiom and we already have
   one in the codebase.
4. **Move the enum into the ViewModel file.** The readable label is a UI
   concern (string resources eventually, ideally), so it lives more naturally
   in the screen file alongside the existing `EventDetailTab` precedent.

## Migration plan

Mechanical, single PR per screen or one bundled — both are fine since each
screen is independent. Suggested order (smallest blast radius first):
1. `DistrictDetail` — 2 tabs, simplest VM body.
2. `MyTBA` — 2 tabs.
3. `TeamDetail` — 3 tabs.
4. `TeamEventDetail` — 5 tabs; **fold in the missing Awards-tab refresh case**
   while we're there (currently `when (tab) { 0..3 }` only).

No external API impact; no persisted state involved (the tab index in nav state
remains an `Int` — only the in-process function call is typed). Removing the
`private val TABS = listOf(…)` is the only deletion at each site.

## Decision requested

Approve typing `refreshTab(tab: …)` across the four remaining detail ViewModels
to per-screen enums, matching the existing `EventDetailTab` precedent. On
approval, implementation will follow as one PR (or four — bidder's choice; they
don't depend on each other).
