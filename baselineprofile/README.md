# Baseline Profile

This module generates the app's [Baseline Profile][bp] — a list of classes and methods that
the app touches on its critical startup path. Android AOT-compiles exactly those at install
time, so the first cold launch (and the events list → event detail journey) skips JIT warmup.

## How it's wired

- **The profile is committed to source:** `app/src/main/generated/baselineProfiles/`
  (`baseline-prof.txt` + `startup-prof.txt`). Git-tracked, code-reviewable plain text.
- **Release builds consume it with no emulator in the loop.** `app/build.gradle.kts` sets
  `baselineProfile { automaticGenerationDuringBuild = false }`, so a normal release build just
  bundles the committed profile — it never tries to spin up a device. The `profileinstaller`
  dependency applies it on-device at first run.
- **Regeneration is a separate, deliberate step** (below). A slightly stale profile is still a
  win — it just AOT-compiles slightly older paths — so you do **not** need to regenerate every
  release.

```
:baselineprofile (com.android.test, targetProjectPath = :app)
  ├─ BaselineProfileGenerator   → runs TbaJourney, emits baseline-prof.txt / startup-prof.txt
  ├─ StartupBenchmark           → measures cold-start TTID: None vs. Baseline-Profile compilation
  └─ TbaJourney                 → the shared journey both use (cold start → events list →
                                   deep-link a populated event → Rankings/Matches/Alliances)
```

## When to regenerate

Regenerate after a change that **meaningfully reworks the startup or events-list / event-detail
path** — new screens on that path, a navigation rewrite, a major dependency bump (Compose,
Navigation, Hilt). Cosmetic or unrelated-feature changes don't need it.

## How to regenerate (local, recommended)

You need a **running emulator** (API 28+) and a **TBA read key** so the journey loads real
content — the journey hard-fails on an empty events list rather than commit a content-free
profile.

1. Add to `local.properties` (both lines):
   ```properties
   tba.url.benchmark=https://www.thebluealliance.com/
   tba.api.key.benchmark=<your read-only TBA APIv3 key>
   ```
   Get a read key at <https://www.thebluealliance.com/account> → **Read API Keys**. This key is
   baked into the non-shippable `nonMinifiedRelease`/`benchmarkRelease` capture variants only;
   shipped releases resolve their key from Firebase Remote Config and bake nothing.
2. With an emulator booted:
   ```bash
   ./gradlew :app:generateBaselineProfile -PuseConnectedDevices=true
   ```
3. Review the diff under `app/src/main/generated/baselineProfiles/` and commit it. Sanity-check
   it still contains app code, not just framework:
   ```bash
   grep -c thebluealliance app/src/main/generated/baselineProfiles/baseline-prof.txt   # expect a few thousand
   ```

Omitting `-PuseConnectedDevices=true` captures against the hands-off Gradle Managed Device
(`pixel6Api34`, AOSP API 34) instead — deterministic but downloads the system image first.

## How to regenerate (CI)

`.github/workflows/baseline-profile.yaml` is **manual-trigger only** (`workflow_dispatch`):
Actions tab → **Regenerate Baseline Profile** → Run workflow. It boots an emulator, regenerates,
and opens a PR with the refreshed profile. It is deliberately **not** tied to release tags —
that would land the new profile only in the *following* release and couple every release to the
slowest job in the repo.

It needs one repo secret:

| Secret | Value |
| --- | --- |
| `BASELINE_PROFILE_TBA_READ_KEY` | A read-only TBA APIv3 key (same kind as the local `tba.api.key.benchmark`). Used only to load content during capture. |

## Measuring the win

`StartupBenchmark` reports cold-start time-to-initial-display under two compilation modes —
`None()` (JIT, worst case) and `Partial(BaselineProfileMode.Require)` (our committed profile):

```bash
./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest -PuseConnectedDevices=true
```

> **Run this on a physical device for a trustworthy number.** Macrobenchmark refuses to run on
> an emulator by default because emulator timing isn't representative of real hardware. You can
> force an emulator read with
> `-Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.suppressErrors=EMULATOR`,
> but treat the result as **directional only** — absolute milliseconds and the delta will both
> be noisier than on a real phone.

[bp]: https://developer.android.com/topic/performance/baselineprofiles/overview
