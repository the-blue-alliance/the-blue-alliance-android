# Agent Guidelines

## Commits

When asked to commit a checkpoint, write a short summary line and a brief description of what changed and why. Keep it concise.

## UI conventions

Highlight shapes (the hover, press and focus highlight of anything clickable):

1. **Full width → square.** A highlight that spans its container edge to edge stays square: list
   rows, match-list rows, table rows, blue bands. Material list items work this way.
2. **Inset → rounded.** A highlight that stops short of its container's edges gets rounded
   corners, sized to the element:
   - one line of text or a number (links, score cells, footer links): `MaterialTheme.shapes.small`, 8dp
   - a card-sized, multi-line block (the inset Last/Next match row, media tiles): `MaterialTheme.shapes.medium`, 12dp

`paddedClickable` clips to the shape you pass; pick it by these rules.

## Verifying changes on an emulator

After a substantive UI change, build and run the app to confirm it. Run all
commands **from the worktree root**. A base AVD named `Medium_Phone` must exist
(`"$ANDROID_HOME/emulator/emulator" -list-avds`) or override it with `TBA_BASE_AVD`.

Single emulator:

```bash
scripts/run-emulator.sh Medium_Phone        # boot (idempotent — no-op if already running)
scripts/install-and-launch.sh               # build :app debug, install, AND launch
mkdir -p artifacts
scripts/emu screenshot artifacts/<name>.png
```

`scripts/emu -h` lists the rest: `find`/`tap`/`long-press` by text or content-desc,
`top` to confirm which activity is in front, `qs` to open Quick Settings (it waits
until the shade has focus, so a following `find` sees the tiles).

Mouse and keyboard (`scripts/emu -h` for every flag):

- `emu hover <text>` parks a stylus: enough for hover highlights (what a Quest laser looks like).
- `emu mouse …` drives a real virtual USB mouse. Use it for pointer icons, right-click and the wheel:
  `mouse move <text> --shot a.png` (pointer and icon stay in the shot), `mouse click <text> --right`,
  `mouse scroll down 3 --at <text>` (`left`/`right` use the horizontal wheel), `mouse drag x1 y1 x2 y2`,
  `mouse cursor` (prints the icon: `HAND`, `TEXT`, `ARROW`), `mouse stop`. The device starts on first
  use. `hover` and `mouse` release each other.
- `emu key tab`, `emu key ctrl+f`, `emu key shift+tab esc`, `emu key meta+slash`.
- Coordinates are screenshot pixels, including under a `wm size` override.

### Getting to a screen fast

Open screens with a deep link, not taps. It takes about a second, and it doesn't flake the way UI-dump-then-tap does:

```bash
scripts/navigate.sh /event/2025hop          # Event detail
scripts/navigate.sh /team/frc254            # Team detail
scripts/navigate.sh /match/2025hop_qm5      # Match detail (event underneath)
scripts/navigate.sh /events/2024            # Events list for a year
scripts/navigate.sh /teams                  # also /regional_advancement and /event/<key>/pitmap
```

- `navigate.sh` pins the intent to the installed TBA app (debug first; `TBA_PACKAGE` overrides), so links never open in Chrome.
- Only use `scripts/emu find`/`tap` for states a URL can't express, such as a tab, an open menu or a selection.
- Capture with `scripts/emu screenshot`.
- For window sizes, use `adb shell wm size 2400x1080 && adb shell wm density 420` for phone landscape. Reset with `wm size reset; wm density reset`.

### Signed-in screens

myTBA (Favorites, Notifications, a team's or event's notification sheet) needs a signed-in
user with data, so capture it against a local TBA server, not production:

```bash
scripts/local-backend.sh up       # server docker compose + Firebase emulators (or reuses one on :8080)
scripts/local-backend.sh seed     # 3 real 2026 events + uid 2's favorites and subscriptions
./gradlew :app:assembleDebug -Ptba.url.debug=http://10.0.2.2:8080/ -Ptba.api.key.debug=tba-dev-key
adb -s "$ANDROID_SERIAL" install -r app/build/outputs/apk/debug/app-debug.apk
scripts/emu launch com.thebluealliance.androidclient.development/com.thebluealliance.android.MainActivity
scripts/emu tap More && scripts/emu tap myTBA && scripts/emu tap "Sign in with Google"
scripts/local-backend.sh down     # when done
```

- The `-P` flags beat `local.properties`, so leave that file alone even when it points debug builds at production.
- Debug builds always use the Firebase Auth emulator at `10.0.2.2:9099`. There, "Sign in with Google" signs in as the seeded user (uid 2) with no Google account.
- `seed` reads a thebluealliance.com key from `TBA_APIV3_KEY`, or from `tba auth login` if that is unset. `TBA_SEED_EVENTS` changes the events, and `TBA_SERVER_DIR` picks the server checkout.
- `seed` writes favorites through the app's own `setPreferences` call, so rerunning it is safe. `status` prints what uid 2 has.

### Keeping the loop short

- **Several agents capturing at once:** use the read-only fleet below, one emulator per agent. Don't use a single shared emulator behind a lock.
- **Stacked branches:**
  - `git config rerere.enabled true` replays conflict resolutions you've already made.
  - `git rebase --update-refs` moves a whole linear stack in one command. It skips branches checked out in another worktree, so when each branch has its own worktree, add `exec` lines to the rebase that record each new tip, then `git reset --hard <tip>` in each clean worktree.
  - Keep each branch on its real parent, not on merge-only integration branches.
- **Gates on a stack:** run ktlint plus the tests you touched on each branch. Run the full gate (`:app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:ktlintCheck`) on the stack's tip. CI runs everything on every PR.
- **Screenshots:**
  - Capture only states whose pixels the change can affect.
  - For a semantics-only change, write "no visible change" instead of attaching 0.00% pairs.
  - Don't recapture when only the base branch moved.
- **Review before pushing:** run a code review on the branch before the first push. That's cheaper than force-pushing a stack after review comments.

### Parallel worktrees (fan-out)

When several worktree agents verify at once, each drives its **own** read-only
emulator off a shared base AVD, so they don't contend for one device. Read-only
instances write to a throwaway overlay and can never modify the base AVD.

**One-time, by the orchestrator** (not each agent):

```bash
scripts/run-emulator.sh Medium_Phone        # 1. boot the base; get it golden (sign-in, etc.)
scripts/worktree-emu.sh bless               # 2. save its state as the 'verify-base' snapshot
# 3. close the base AVD — a read-only fleet can't share an AVD with a read-write base
```

**Per worktree, given a slot `0..2`:**

```bash
mkdir -p artifacts
export ANDROID_SERIAL="$(scripts/worktree-emu.sh up "$SLOT")"   # boot read-only instance; prints its serial
./gradlew :app:installDebug                                     # installs only — adb + Gradle read $ANDROID_SERIAL
# MUST launch — installDebug does NOT start the app; screenshotting without this captures the launcher.
# (scripts/emu launch also grants Android 17's ACCESS_LOCAL_NETWORK so the 10.0.2.2 backend is reachable.)
scripts/emu launch \
  com.thebluealliance.androidclient.development/com.thebluealliance.android.MainActivity
scripts/emu screenshot artifacts/<name>.png
scripts/worktree-emu.sh down "$SLOT"                            # tear down when done
```

If `up` errors with `snapshot 'verify-base' not found` or `base AVD … running
read-write`, the orchestrator hasn't finished the one-time setup (bless + close
the base) — stop and surface it; don't try to work around it.

**Tear it all down** (the fleet is headless — `-no-window` — so nothing shows on screen):

```bash
scripts/worktree-emu.sh list        # see what's running
scripts/worktree-emu.sh down-all    # kill the ENTIRE fleet at once (ports 5580+ only; never the base/interactive emulators)
adb devices                         # confirm only your base/interactive emulators remain
```

Notes:

- `scripts/emu`, `adb`, and Gradle all honor `$ANDROID_SERIAL`, so exporting it once points
  every command at the right device. Pass `-s <serial>` to `scripts/emu` to override it.
- Debug package is `com.thebluealliance.androidclient.development`, activity
  `com.thebluealliance.android.MainActivity`.
- Max **3** concurrent instances on a 32 GB host (`TBA_WT_CAP`). Slots use console
  ports `5580+`, leaving `5554–5578` free for interactive use.
- Gitignored configs (`google-services.json`, `local.properties`) must be present in each
  worktree for `:app:installDebug` to build — the orchestrator copies them in.
- **Emulators parallelize cheaply; builds don't.** Idle read-only instances are light, but
  three simultaneous `:app:installDebug` builds will thrash a 32 GB host (especially under
  other load) — serialize or throttle them (`nice -n 10`, `--max-workers`) even while the
  emulators run in parallel.
