# RFC 0004: Extract shared `:core` module(s) so `:wear` stops forking `:app`

- **Status:** Draft / Request for Comments
- **Author:** @gregmarra
- **Date:** 2026-05-29
- **Type:** Architecture (multi-step)

## Summary

The `:wear` module re-implements the entire networking + DTO + tracking-domain
stack that already exists in `:app`, because there is no shared module. The two
copies have **already silently diverged** (cache size, a battery constraint),
which is a latent-bug factory. Extract `:core-network` and `:core-domain` so both
modules consume one implementation.

## Problem — `:wear` is a fork of `:app`

Only `:app` and `:wear` exist (`settings.gradle`); `:wear` does not depend on
`:app`, so it duplicates:

**Networking & DTOs**
- `wear/.../data/WearNetworkModule.kt` (~83 lines) re-implements
  `app/.../di/NetworkModule.kt`: same OkHttp builder, same `X-TBA-Auth-Key`
  interceptor, same kotlinx-serialization converter, same 30s timeouts, same disk
  cache — but **5 MB vs the app's 20 MB**.
- `wear/.../data/WearTbaApi.kt` re-declares 4 endpoints (`getTeamEvents`,
  `getEventMatches`, `getTeamMedia`, `getTeam`) already in
  `app/.../data/remote/TbaApi.kt`.
- `wear/.../data/dto/{Event,Match,Team,Media}Dto.kt` (~67 lines) duplicate
  `app/.../data/remote/dto/` with identical `@SerialName` mappings.

**Tracking-domain logic** — `wear/.../worker/TeamTrackingComplicationWorker.kt`
(564 lines) re-implements `app/.../widget/TeamTrackingWorker.kt` (427) against
parallel model types:
- `findCurrentEvent` (app `:328-377` vs wear `:463-507` — even the "concurrent
  champs events, prefer unplayed" comment is copied),
- record computation (app `computeRecord :397-415` vs wear `:334-356`),
- next/last-match selection,
- `getShortLabel` (wear `:520-528` reimplements
  `app/.../domain/MatchGroupExtension.kt:112`),
- `formatMatchTime` (app `:417` vs wear `:531` — a 3rd copy; see RFC 0002),
- RP-bonus extraction (wear `extractBonusRp :416-429` vs
  `app/.../domain/MatchScoreBreakdown.kt rpBonuses()`),
- `compLevelOrder` (wear `:509-517` vs `CompLevel.order`).

**Tracked-team storage** — forked and inconsistent:
- `:app` widget persists via Glance DataStore (`TeamTrackingWidgetKeys`).
- `:wear` uses raw `SharedPreferences("team_tracker_app")`
  (`TeamTrackerPreferences`, 159 lines of hand-written getter/setter pairs over
  ~22 keys) whose keys overlap ~1:1 with the widget's.
- `TeamTrackingComplicationWorker.updateAppTracker (:287-402)` re-derives and
  **dual-writes** the same match data into wear prefs alongside the complication
  update.

## Why this matters now

Recent commits hardened the Wear refresh workers (network constraints, OkHttp
disk cache, schedule-only-when-tracked). Because the logic is duplicated, those
fixes had to be applied per-copy — and the copies have already diverged:
- disk cache **5 MB (wear) vs 20 MB (app)**,
- `setRequiresBatteryNotLow(true)` on wear's periodic work
  (`TeamTrackingComplicationWorker.kt:64`) but **not** the app's
  (`TeamTrackingWorker.kt:71`).

A shared module removes ~300–400 duplicated lines and makes these
divergences impossible by construction.

## Proposal

Introduce shared Gradle module(s) consumed by both `:app` and `:wear`:

1. **`:core-network`** — OkHttp / Retrofit / `Json` / `X-TBA-Auth-Key`
   interceptor + `TbaApi` + DTOs, with **one** cache/timeout config. Delete
   `WearNetworkModule`, `WearTbaApi`, and the wear DTOs.
2. **`:core-domain`** — shared models + the tracking algorithms (`findCurrentEvent`,
   record computation, match selection, `formatMatchTime`, RP bonuses,
   `CompLevel.order`, `getShortLabel`). Both workers shrink to fetch + persist.
3. **(Optional) `:core-data`** — one tracked-team store (DataStore) + a serialized
   snapshot model, replacing `TeamTrackerPreferences` and the dual-write.
4. A build convention plugin to share module config (see RFC 0002 item 2).

## Migration plan (incremental — each step its own PR)

1. Create `:core-network`; move the API/DTO/Json/OkHttp layer; point `:app` at it
   (no behavior change). Then switch `:wear` off `WearTbaApi`/`WearNetworkModule`.
2. Create `:core-domain`; move shared models + tracking logic; repoint both
   workers. Pick **one** battery/cache policy deliberately as part of this.
3. (Optional) unify tracked-team storage; remove the wear dual-write.

## Related but separable

`app/.../widget/TeamTrackingWidget.kt` is **1356 lines** mixing 5 size-tier
layouts, shared composables, the `WidgetData` model + formatting, hand-rolled
prefs (de)serialization, and **32 `@Preview` functions** (~125 lines). Splitting
it into `WidgetLayouts` / `WidgetComponents` / `WidgetData` / `WidgetPreviews` is
self-contained within `:app` and can land independently of this RFC.

## Alternatives

- **Make `:wear` depend on `:app`.** Wrong direction — pulls the whole phone UI /
  Compose-for-phone graph into the watch build. A thin `:core` is the right cut.
- **Leave it duplicated, add a lint/test that diffs the copies.** Treats the
  symptom; the divergence already happened.

## Scope / risk

Largest of the four RFCs. Risk is in module boundaries and cross-module Hilt
wiring (and Glance/Wear specifics for step 3). Recommend sequencing **after**
RFCs 0001–0003; each step here is independently shippable and reversible.

## Decision requested

Approve the `:core-network` + `:core-domain` split (vs. alternatives)? Is the
optional unified tracked-team store (step 3) in scope now or later?
