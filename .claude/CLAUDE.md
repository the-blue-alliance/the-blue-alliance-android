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

```bash
# Take a screenshot
adb shell screencap -p /sdcard/screen.png && adb pull /sdcard/screen.png screenshot.png

# Tap (uses device pixel coordinates, e.g. 1080x2400)
adb shell input tap <x> <y>

# Press back
adb shell input keyevent KEYCODE_BACK
```

### Tap coordinate tips

The emulator is 1080x2400 device pixels. Screenshots displayed in conversation may show a
smaller size (e.g. 900x2000) with a note like "Multiply coordinates by 1.20 to map to
original image." `adb shell input tap` uses the real 1080x2400 device pixel coordinates.

- The status bar is ~50px, the Material 3 top app bar is ~140px, so the first content row
  starts around **y=300-400** in device pixels.
- Bottom nav bar centers around **y=2300**.
- For list rows below the top app bar, first row center ≈ y=350, second ≈ y=450, etc.
- When taps don't seem to register, the content is probably lower than expected — try
  increasing y by 100-150px.

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
