# The Blue Alliance - Android App

An Android app for accessing information about the [FIRST Robotics Competition](https://www.firstinspires.org/robotics/frc). This is the native Android client for [The Blue Alliance](https://www.thebluealliance.com).

Available on the [Google Play Store](https://play.google.com/store/apps/details?id=com.thebluealliance.androidclient&hl=en).

## Alpha Testing

We publish pre-release builds to a closed alpha track on Google Play. To join, request access to the [thebluealliance-android-alpha](https://groups.google.com/g/thebluealliance-android-alpha) Google Group, then opt in to the alpha on the [Play Store listing](https://play.google.com/store/apps/details?id=com.thebluealliance.androidclient).

## Get Involved

The Blue Alliance is built by volunteers. We'd love your help!

- **Chat with us** on [Slack](https://the-blue-alliance.slack.com/) (request an invite on the [mailing list](https://groups.google.com/forum/#!forum/thebluealliance-developers))
- **Report bugs or request features** on the [issue tracker](https://github.com/the-blue-alliance/the-blue-alliance-android/issues)
- **Contribute code** by forking the repo, making changes on a branch, and opening a pull request

## Features

- **Events** -- Browse competitions by year and week, view event details including teams, match results, rankings, alliances, and awards
- **Teams** -- Browse teams, view team details with year-by-year event participation and media
- **Districts** -- Browse district listings and rankings
- **Search** -- Find teams and events across all of TBA
- **myTBA** -- Sign in with Google to save favorite teams and events, and set up push notifications for match scores, upcoming matches, schedule changes, and more
- **Deep linking** -- Open thebluealliance.com links directly in the app
- **Push notifications** -- Receive alerts for match scores, upcoming matches, event updates, and more via Firebase Cloud Messaging

## Tech Stack

- **UI:** Jetpack Compose with Material 3
- **Architecture:** MVVM with Hilt dependency injection
- **Data:** Room database with Retrofit networking, repository pattern bridging API and local storage
- **Auth:** Firebase Auth with Google Sign-In
- **Messaging:** Firebase Cloud Messaging with WorkManager retry
- **Navigation:** Type-safe Compose Navigation
- **Language:** Kotlin

## First-time setup

Fresh clone to running app in three steps.

### 1. Prerequisites

- **[Android Studio](https://developer.android.com/studio)** — on first open of the project, Gradle sync installs the required SDK components for you.
- **JDK 17** — bundled with Android Studio (`jbr/`). If you build from the CLI without Android Studio, install one separately (`brew install openjdk@17` on macOS).

### 2. Clone and stub `google-services.json`

```bash
git clone https://github.com/the-blue-alliance/the-blue-alliance-android.git
cd the-blue-alliance-android
cp  app/src/debug/google-services.json.example  app/src/debug/google-services.json
cp wear/src/debug/google-services.json.example wear/src/debug/google-services.json
```

The Firebase plugin refuses to configure without a `google-services.json`, but you don't need a real Firebase project for the first build — the stubs are enough. You also don't need a `local.properties` file; the Gradle scripts fall back to sane defaults, and Android Studio will create one with your SDK path on first sync.

### 3. Open in Android Studio and run

Open the cloned directory in Android Studio. It will download Gradle, the Android SDK platform, and build tools on first sync (takes a few minutes). Then **Tools → Device Manager → Create Device** to make an emulator (any recent Phone AVD), select it in the run target dropdown, and hit the green ▶ button.

The app will launch with empty lists because no data source is connected yet — see [Connecting to data](#connecting-to-data) below.

<details>
<summary>CLI alternative</summary>

```bash
./gradlew :app:installDebug
adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.MainActivity
```

</details>

## Connecting to data

After the first build succeeds, pick one of these to see real content.

### Option A: Point at the production API (simplest)

Copy the template and edit:

```bash
cp local.properties.example local.properties
```

Then uncomment and fill in:

```properties
tba.url.debug=https://www.thebluealliance.com/
tba.api.key.debug=YOUR_KEY_HERE
```

Get an API key from the [TBA Account page](https://www.thebluealliance.com/account). Rebuild and you'll see real events, teams, and matches.

### Option B: Run the local backend (for backend development)

The Android debug build defaults to `http://10.0.2.2:8080/` (the emulator's alias for the host's `localhost`), which is where the TBA server runs inside its Docker Compose stack. Follow the setup instructions in the [TBA backend repository](https://github.com/the-blue-alliance/the-blue-alliance) to start it; once Docker Compose is running, rebuild the Android app and you'll see local data.

The Docker Compose stack includes a Firebase Auth emulator, and debug builds of the Android app are wired to it in `AuthModule` — so Google Sign-In works against the local backend with the stub `google-services.json` alone. No real Firebase project needed.

## Firebase project (optional)

Only needed if you want to test Google Sign-In or push notifications **against production**. For local development the stub `google-services.json` is enough.

To set up a real project:

1. Create a project in the [Firebase console](https://console.firebase.google.com/).
2. Add an Android app with package name `com.thebluealliance.androidclient.development`.
3. Add your debug signing SHA-1 fingerprint ([instructions](https://developers.google.com/android/guides/client-auth)).
4. Download the real `google-services.json` and overwrite `app/src/debug/google-services.json`.

## Code quality

The project enforces formatting and Android Lint in CI via [`ci-lint.yaml`](.github/workflows/ci-lint.yaml).

### Formatting (ktlint)

```bash
./gradlew ktlintCheck    # fail on any violation
./gradlew ktlintFormat   # auto-fix what it can
```

Install the pre-commit hook once to catch formatting issues before they reach CI:

```bash
./gradlew addKtlintCheckGitPreCommitHook
```

### Android Lint

```bash
./gradlew :app:lintDebug
./gradlew :wear:lintDebug
```

Both modules use `warningsAsErrors = true`, so any new lint warning fails the build. If you need to suppress a check, prefer targeted `tools:ignore` / `@Suppress` annotations over disabling the rule project-wide.

## Testing

```bash
./gradlew :app:testDebugUnitTest
```

Unit tests use JUnit 5 (Jupiter), MockK, Turbine for Flow testing, and Coroutines Test utilities.
