# Local Development Notes

## Local Backend

The TBA web server (backend + frontend) is checked out at `~/codez/the-blue-alliance-2026` with its Docker container running. When the app targets the local dev server (e.g. `http://10.0.2.2:8080` in the emulator), requests go to that container.

## Build Commands

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug   # always install to emulator after building
./gradlew :app:testDebugUnitTest
```

After installing a debug build, always relaunch the app on the emulator:
```bash
adb shell am force-stop com.thebluealliance.android.dev && adb shell am start -n com.thebluealliance.android.dev/com.thebluealliance.android.MainActivity
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
