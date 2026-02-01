# Local Development Notes

## Local Backend

The TBA web server (backend + frontend) is checked out at `~/codez/the-blue-alliance-2026` with its Docker container running. When the app targets the local dev server (e.g. `http://10.0.2.2:8080` in the emulator), requests go to that container.

## Build Commands

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug   # always install to emulator after building
./gradlew :app:testDebugUnitTest
```

## Emulator Interaction

```bash
# Take a screenshot
adb shell screencap -p /sdcard/screen.png && adb pull /sdcard/screen.png screenshot.png

# Tap (uses device pixel coordinates, e.g. 1080x2400)
adb shell input tap <x> <y>

# Press back
adb shell input keyevent KEYCODE_BACK
```

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
