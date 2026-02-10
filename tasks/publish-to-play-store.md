# Publish New TBA Android App to Google Play

## Context

The new TBA Android app (Compose rewrite) needs to be published to Google Play, replacing the legacy app (`com.thebluealliance.androidclient`). The applicationId already matches the Play Store identity, so this is an in-place upgrade.

**Version strategy:**
- `10.9.0` — initial alpha release (continues from legacy app's version lineage)
- `11.0.0` — full production release

**Key reference:** [gradle-play-publisher](https://github.com/Triple-T/gradle-play-publisher) for CLI-based publishing.

---

## Phase 1: Firebase Crashlytics ✅

- [x] Add `firebase-crashlytics` dependency to `app/build.gradle.kts`
- [x] Add `com.google.firebase.crashlytics` plugin to `app/build.gradle.kts`
- [x] Add Crashlytics Gradle plugin to `libs.versions.toml` and root `build.gradle.kts`
- [x] Verify crash reporting works: force a test crash in debug build, confirm it appears in Firebase Console (`tbatv-prod-hrd` for release, `gregmarra-tba-dev` for debug)
- [x] Add `FirebaseCrashlytics.setUserId()` call after sign-in (use Firebase UID, not PII)
- [x] Disable Crashlytics in debug builds by default (via `firebase_crashlytics_collection_enabled=false` in debug AndroidManifest.xml)

## Phase 2: Version Number & Code Setup ✅

- [x] Implement tag-based versioning in `app/build.gradle.kts` using `git describe --tags --long --match "v[0-9]*"`
  - versionCode formula: `MAJOR * 1_000_000 + MINOR * 10_000 + PATCH * 100 + commitDistance`
  - versionName: `MAJOR.MINOR.PATCH` on tagged commits, `MAJOR.MINOR.PATCH-dev.N` otherwise
- [x] Create `v10.9.0` git tag → produces `versionCode = 10090000`, `versionName = "10.9.0"`
- [x] Confirm `applicationId = "com.thebluealliance.androidclient"` matches Play Store (already correct)

## Phase 3: Release Signing

- [ ] Ensure release keystore is set up (already have `release.keystore` and config in `local.properties`)
- [ ] Verify release signing config in `app/build.gradle.kts` produces a signed AAB
- [ ] Test building a release AAB: `./gradlew :app:bundleRelease`
- [ ] Confirm the signed AAB installs and runs correctly on a device

## Phase 4: Gradle Play Publisher Integration

- [ ] Add [gradle-play-publisher](https://github.com/Triple-T/gradle-play-publisher) plugin to `libs.versions.toml` and `app/build.gradle.kts`
- [ ] Create a Google Play service account with appropriate permissions:
  1. Create service account in Google Cloud Console
  2. Grant "Release Manager" role in Play Console → API Access
  3. Download JSON key, store securely (gitignored, referenced from `local.properties` or env var)
- [ ] Configure the plugin in `app/build.gradle.kts`:
  ```kotlin
  play {
      serviceAccountCredentials.set(file(localProperties["PLAY_SERVICE_ACCOUNT_KEY"] as String))
      track.set("alpha")
      defaultToAppBundles.set(true)
  }
  ```
- [ ] Test upload to internal/alpha track: `./gradlew publishReleaseBundle`
- [ ] Document the publishing commands in this file (see Phase 7)

## Phase 5: Alpha Channel Setup

- [ ] Create an "Alpha" (closed testing) track in Google Play Console if it doesn't exist
- [ ] Add alpha testers: create a Google Group or use email list for testers
- [ ] Configure gradle-play-publisher to target `alpha` track by default
- [ ] Publish first alpha build (`10.9.0`) to the alpha track
- [ ] Verify alpha testers can install from Play Store
- [ ] Set up release notes template (stored in `app/src/main/play/release-notes/en-US/alpha.txt`)

## Phase 6: Screenshots

### 6.1: Generate New Screenshots

- [ ] Define the set of screens to capture:
  - Home screen (events list)
  - Event detail (with matches, rankings, teams tabs)
  - Match detail (with score breakdown)
  - Team detail
  - District rankings
  - Settings / My TBA
- [ ] Capture screenshots on at least two device profiles:
  - Phone (Pixel 8 or similar, 1080x2400)
  - 7" tablet (if supporting tablets)
  - 10" tablet (if supporting tablets)
- [ ] Use `2026test` (North Pole Regional) for consistent test data

### 6.2: Screenshot Playbook

- [ ] Create `tasks/screenshot-playbook.md` documenting the full screenshot capture process:
  1. **Prerequisites**: emulator setup, test data event (`2026test`), app installed
  2. **Device profiles**: which AVDs to use, resolution settings
  3. **Data setup**: sign in, navigate to `2026test`, ensure data is loaded
  4. **Capture sequence**: exact steps for each screenshot using `emu` tool
     ```bash
     # Example capture sequence
     emu launch com.thebluealliance.androidclient.development/com.thebluealliance.android.MainActivity
     # Navigate to events list
     emu screenshot screenshots/play-store/phone/01-home.png
     # Navigate to 2026test
     emu tap "North Pole Regional"
     emu screenshot screenshots/play-store/phone/02-event-detail.png
     # ... etc
     ```
  5. **Post-processing**: any trimming, framing, or annotation steps
  6. **Upload**: how to update Play Store listing (via gradle-play-publisher or manually)
- [ ] Store Play Store screenshots in `app/src/main/play/listings/en-US/graphics/`
  - `phone-screenshots/` (min 2, max 8)
  - `tablet-screenshots/` (optional)
- [ ] Add `screenshots/play-store/` to `.gitignore` for raw captures (keep processed ones in `play/`)

## Phase 7: Production Release (`11.0.0`)

_These are future steps, not needed for alpha._

- [ ] Promote alpha to beta, then beta to production — OR publish directly to production
- [ ] Bump version to `11.0.0` with appropriate `versionCode`
- [ ] Write production release notes
- [ ] Update Play Store listing metadata (description, feature graphic, etc.)
- [ ] Configure `play { track.set("production") }` or use `./gradlew promoteArtifact`
- [ ] Monitor Crashlytics after production rollout
- [ ] Consider staged rollout (e.g., 10% → 50% → 100%)

---

## Quick Reference: Publishing Commands

```bash
# Build release AAB
./gradlew :app:bundleRelease

# Publish to alpha track
./gradlew publishReleaseBundle

# Promote from alpha to beta
./gradlew promoteArtifact --from-track alpha --to-track beta

# Promote from beta to production
./gradlew promoteArtifact --from-track beta --to-track production

# Publish with specific release notes
./gradlew publishReleaseBundle --release-name "10.9.0-alpha01"
```

## Open Questions

- [ ] What `versionCode` is the legacy app currently at? Need to check Play Console.
- [x] ~~Do we want tag-based versioning or manual version bumps?~~ → Tag-based versioning implemented
- [ ] Should alpha testers be a Google Group or individual emails?
- [ ] Are we keeping the existing Play Store listing text or rewriting it for the new app?
- [ ] Do we need a new feature graphic / icon, or keep the existing ones?
