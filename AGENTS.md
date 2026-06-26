# Agent Guidelines

## Commits

When asked to commit a checkpoint, write a short summary line and a brief description of what changed and why. Keep it concise.

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
