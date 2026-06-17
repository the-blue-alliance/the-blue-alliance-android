# Agent Guidelines

## Commits

When asked to commit a checkpoint, write a short summary line and a brief description of what changed and why. Keep it concise.

## Verifying changes on an emulator

After a substantive UI change, build and run the app to confirm it.

Single emulator:

```bash
scripts/run-emulator.sh Medium_Phone        # boot (idempotent)
scripts/install-and-launch.sh               # build :app debug, install, launch
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
export ANDROID_SERIAL="$(scripts/worktree-emu.sh up "$SLOT")"   # boot read-only instance
./gradlew :app:installDebug                                     # adb + Gradle honor ANDROID_SERIAL
scripts/emu screenshot artifacts/<name>.png                     # scripts/emu honors it too
scripts/worktree-emu.sh down "$SLOT"                            # tear down when done
```

**Tear it all down** (the fleet is headless — `-no-window` — so nothing shows on screen):

```bash
scripts/worktree-emu.sh list        # see what's running
scripts/worktree-emu.sh down-all    # kill the ENTIRE fleet at once (ports 5580+ only; never the base/interactive emulators)
adb devices                         # confirm only your base/interactive emulators remain
```

Notes:

- Max **3** concurrent instances on a 32 GB host (`TBA_WT_CAP`). Slots use console
  ports `5580+`, leaving `5554–5578` free for interactive use.
- `scripts/worktree-emu.sh list` shows slot status; see the script header for env overrides.
- Gitignored configs (`google-services.json`, `local.properties`) must be present in each
  worktree for `:app:installDebug` to build — the orchestrator copies them in.
- **Emulators parallelize cheaply; builds don't.** Idle read-only instances are light, but
  three simultaneous `:app:installDebug` builds will thrash a 32 GB host (especially under
  other load) — serialize or throttle them (`nice -n 10`, `--max-workers`) even while the
  emulators run in parallel.
