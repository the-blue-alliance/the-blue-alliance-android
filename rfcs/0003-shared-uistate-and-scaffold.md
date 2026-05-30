# RFC 0003: Shared `UiState<T>` + screen scaffold

- **Status:** Draft / Request for Comments
- **Author:** @gregmarra
- **Date:** 2026-05-29
- **Type:** Refactor (cross-cutting UI)

## Summary

Every screen and ViewModel re-implements loading / empty / error / refresh
handling by hand, with two incompatible conventions and **34 empty
`catch (_: Exception) {}` blocks** that swallow all network errors. Introduce one
`UiState<T>` type, one `StateContent` composable, and one `refreshing { }` helper,
then migrate the 10 screens onto them incrementally.

## Problem

1. **No shared refresh primitive.** The triplet
   ```
   private val _isRefreshing = MutableStateFlow(false)
   val isRefreshing = _isRefreshing.asStateFlow()
   ```
   is copy-pasted in all 10 ViewModels (`TeamsViewModel.kt:27`,
   `DistrictsViewModel.kt:33`, `EventDetailViewModel.kt:54`, …), and all 10
   screens then wire `collectAsStateWithLifecycle()` + `PullToRefreshBox`
   themselves.

2. **Two incompatible `UiState` conventions.**
   - Sealed `Loading`/`Success` (sometimes `Error`): `TeamsUiState`,
     `DistrictsUiState`, `EventsUiState.kt:22-34`.
   - Nullable-field data class with no explicit loading/error: `TeamDetailUiState`,
     `MatchDetailUiState`, `TeamEventDetailUiState.kt:14-26`, `EventDetailUiState`.
   Only `EventsUiState` even has an `Error` case; elsewhere "loading vs empty" is
   inferred from `list.isEmpty()`, so a failed load looks identical to an empty
   result.

3. **Loading/empty UI hand-rolled despite shared components existing.**
   `common/LoadingBox.kt` and `common/EmptyBox.kt` exist, but ~8 screens re-inline
   `Box(contentAlignment = Center) { CircularProgressIndicator() }` and bespoke
   `Text("No …")` empty states (`TeamsScreen.kt:84-96`,
   `DistrictsScreen.kt:113-123`, `MatchList.kt:61-70`, …). There's no
   `Scaffold + PullToRefreshBox + when(state)` wrapper, so every screen rewrites it.

4. **Errors are swallowed everywhere.** 34 `catch (_: Exception) {}` blocks across
   the UI tree (e.g. `TeamEventDetailViewModel.kt:151-392`,
   `EventDetailViewModel.kt:254-518`), where `refreshAll()` and per-tab
   `refreshTab()` each hand-list every repository call wrapped in its own empty
   catch — and `refreshTab` re-lists the same calls as `refreshAll`. This both
   duplicates ~400 lines and hides every failure (against the project's "don't
   swallow exceptions" guidance).

## Proposal

### 1. One state type
```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(val message: String? = null) : UiState<Nothing>
}
```

### 2. One screen scaffold (reuses the existing `LoadingBox`/`EmptyBox`)
```kotlin
@Composable
fun <T> StateContent(
    state: UiState<T>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    empty: @Composable () -> Unit = { EmptyBox() },
    content: @Composable (T) -> Unit,
) {
    PullToRefreshBox(isRefreshing, onRefresh) {
        when (state) {
            UiState.Loading -> LoadingBox()
            UiState.Empty   -> empty()
            is UiState.Error -> ErrorBox(state.message, onRetry = onRefresh)
            is UiState.Success -> content(state.data)
        }
    }
}
```

### 3. One refresh helper (kills the empty catches + the refreshTab/refreshAll dup)
```kotlin
protected fun ViewModel.refreshing(vararg tasks: suspend () -> Unit) {
    viewModelScope.launch {
        _isRefreshing.value = true
        try {
            coroutineScope { tasks.forEach { t -> launch { runCatching { t() }
                .onFailure { Log.w(TAG, "refresh failed", it) } } } }
        } finally { _isRefreshing.value = false }
    }
}
```
Define each tab's task list once (a `Map<Tab, List<suspend () -> Unit>>` or
`when`) and reuse it from both full-refresh and per-tab refresh.

## Migration plan (incremental — one PR each)

1. Land the three primitives (`UiState`, `StateContent`, `refreshing`) plus an
   `ErrorBox`. No call-site changes yet.
2. Migrate the simple list screens first (Teams, Districts, Events) — mechanical.
3. Migrate the detail screens (EventDetail, TeamEventDetail, TeamDetail,
   MatchDetail), folding their `refreshAll`/`refreshTab` onto `refreshing`.
4. Delete the now-unused bespoke loading/empty boxes and `_isRefreshing` triplets.

Each screen migrates independently and is separately reviewable/revertable.

## Behavior change (intentional)

Failures stop being silent: they log and surface an `Error` state with retry.
This is a UX improvement and aligns with the "don't swallow exceptions" rule, but
reviewers should confirm we're happy showing error states where today a failed
refresh silently shows stale/empty content.

## Alternatives

- **Leave the two conventions, add only the scaffold.** Smaller, but the
  nullable-data-class screens can't express `Error`, so we'd still swallow there.
- **Adopt a library (e.g. Molecule / a Result wrapper).** Heavier dependency for
  what a ~40-line local primitive covers.

## Scope / risk

Large surface (10 screens + 10 VMs) but each migration is mechanical and
isolated. Risk is mostly visual (loading/empty rendering) — verify each screen in
the emulator. Recommend landing the primitives first behind no behavior change.

## Decision requested

Approve the `UiState<T>` shape and the three primitives? Any objection to
surfacing error states (item: "Behavior change") instead of silently swallowing?
