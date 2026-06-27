# Local Development Notes

## Local Backend

The TBA web server (backend + frontend) is checked out at `~/codez/the-blue-alliance-2026` with its Docker container running. When the app targets the local dev server (e.g. `http://10.0.2.2:8080` in the emulator), requests go to that container.

## Build Commands

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug   # always install to emulator after building
./gradlew :app:testDebugUnitTest
```

After installing a debug build, always relaunch the app on the emulator with `scripts/emu launch`
(it also grants Android 17's `ACCESS_LOCAL_NETWORK` so the `10.0.2.2` local backend stays reachable):
```bash
scripts/emu launch com.thebluealliance.androidclient.development/com.thebluealliance.android.MainActivity
```

## Emulator Interaction

Use `scripts/emu` (a Python CLI included in the repo) instead of raw `adb` commands. It provides text-based UI element matching which is far more reliable than guessing pixel coordinates.

```bash
scripts/emu screenshot screenshots/<name>.png   # capture screenshot
scripts/emu find "text"                          # find UI elements by text
scripts/emu tap "text"                           # tap element by text (must match exactly one)
scripts/emu tap-xy <x> <y>                       # tap at exact device pixel coordinates
scripts/emu back                                 # press BACK key
scripts/emu list                                 # dump full UI hierarchy as readable tree
scripts/emu logcat --tag <tag> --grep <pattern> -n <count>  # filtered logcat
scripts/emu launch <package/activity>            # force-stop and start activity
```

Use `find` before `tap` to verify unique matching; use `list` to explore the UI hierarchy.

## PR Screenshots (before/after in PR descriptions)

This is the standard way to put before/after screenshots in a PR — WITHOUT committing
image files. Images live on an orphan `screenshot-assets` branch (its own history, never
merged) and serve from `raw.githubusercontent.com`, so they render inline in the PR body.
Release assets do NOT work here — the org has immutable releases enabled.

```bash
# Uploads images, then rewrites the PR body's screenshot block in place.
scripts/pr-screenshots.sh --pr <N> \
  --row "Label" before.png after.png \   # a before/after table row (repeatable)
  --shot "Label" single.png              # a single image (repeatable)
```

Omit `--pr` to just print the markdown. Re-runs cleanly replace the
`<!-- screenshots:start/end -->` block. Works on fork PRs too (run as a maintainer).
The script never touches your working tree, index, or current branch (pure git plumbing
into the asset branch). Each filename is content-hashed so GitHub's image proxy can't
serve a stale cached copy.

**Getting real data for the shots.** The local backend has no competition data, so point
the *debug* build at prod with a personal read key (thebluealliance.com/account) in
`local.properties`, then rebuild:
```
tba.url.debug=https://www.thebluealliance.com/
tba.api.key.debug=<your-read-key>
```
Drive navigation with deep links — more reliable than tapping, and in-app search only
matches teams/events you've already browsed:
```bash
adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.MainActivity \
  -a android.intent.action.VIEW -d "https://www.thebluealliance.com/event/2024micmp4"
```

**Flushing the asset branch when it gets too full.** Assets accumulate on the branch
forever. To reset it (the script recreates it on the next run):
```bash
git push origin --delete screenshot-assets
```
This breaks image links in any *already-merged/closed* PRs that referenced old assets, so
only flush when those are done. Open PRs you still care about should be re-screenshotted
after a flush.

**Heads-up:** the Docker backend + a Gradle build + the emulator running together can OOM
the machine (containers exit 137). Build with Docker stopped, then start it (or hit prod)
only when capturing.

## Android Documentation

When researching Android APIs, libraries, or best practices, use `android docs search "<query>"` and `android docs fetch "<url>"` instead of web search. This is an offline curated knowledge base that has up-to-date, authoritative Android documentation.

## Architecture

- **UI:** Jetpack Compose with Material 3, MVVM via `@HiltViewModel`
- **DI:** Hilt — modules in `di/`, `@Singleton` repositories
- **Data layer:** Room DB (`TBADatabase`) ← DAOs ← Entities; Retrofit API (`TbaApi`) ← DTOs
- **Repositories** bridge API ↔ Room: fetch from API, store entities, expose `Flow`s of domain models
- **Navigation:** Type-safe Compose navigation with `Screen` sealed class

### Adding a new data type (pattern to follow)

1. Entity in `data/local/entity/` — `@Entity` data class
2. DAO in `data/local/dao/` — `@Dao` interface with observe/insert/delete
3. Register entity + DAO in `TBADatabase`, bump version
4. Provide DAO in `di/DatabaseModule`
5. Add repository methods (or new repository) in `data/repository/`
6. Wire into ViewModel
