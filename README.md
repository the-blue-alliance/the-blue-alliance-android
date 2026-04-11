# The Blue Alliance - Android App

An Android app for accessing information about the [FIRST Robotics Competition](https://www.firstinspires.org/robotics/frc). This is the native Android client for [The Blue Alliance](https://www.thebluealliance.com).

Available on the [Google Play Store](https://play.google.com/store/apps/details?id=com.thebluealliance.androidclient&hl=en).

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

The steps below get you from a fresh clone to a running app on an emulator. No Firebase project, API key, or local backend is required to see the UI — add those later when you need signed-in features or real data.

### 1. Prerequisites

- **[Android Studio](https://developer.android.com/studio)** (any recent version). On first open of the project, Gradle sync will install the required SDK components for you.
- **JDK 17**. Android Studio bundles one (`jbr/`); if you build from the CLI without Android Studio, install a JDK 17 separately (`brew install openjdk@17` on macOS).

### 2. Clone and configure

```bash
git clone https://github.com/the-blue-alliance/the-blue-alliance-android.git
cd the-blue-alliance-android
cp local.properties.example local.properties
```

`local.properties` is gitignored and holds your SDK path, API keys, and signing config. The example file has sane defaults for a first build.

### 3. Add a stub `google-services.json`

The Firebase plugin refuses to configure without a `google-services.json`, but you don't need a real Firebase project for the first build. Copy the example stubs into place:

```bash
cp app/src/debug/google-services.json.example  app/src/debug/google-services.json
cp wear/src/debug/google-services.json.example wear/src/debug/google-services.json
```

Google Sign-In and push notifications will not work with the stub — see [Firebase project (optional)](#firebase-project-optional) below when you need them.

### 4. Open in Android Studio and sync

Open the cloned directory in Android Studio. It will download Gradle, the required Android SDK platform, and build tools on first sync. This takes a few minutes.

### 5. Create an emulator

In Android Studio: **Tools → Device Manager → Create Device**. Any Phone definition with a recent Google Play system image (API 34+) works.

### 6. Build and run

Either click the green ▶ button in Android Studio with your emulator selected, or from the CLI:

```bash
./gradlew :app:installDebug
adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.MainActivity
```

You should see the app launch to an empty Events list. The UI works; you just haven't connected it to any data yet.

## Connecting to data

After the first build succeeds, pick one of these to see real content:

### Option A: Point at the production API (simplest)

Add to `local.properties`:

```properties
tba.url.debug=https://www.thebluealliance.com/
tba.api.key.debug=YOUR_KEY_HERE
```

Get an API key from the [TBA Account page](https://www.thebluealliance.com/account). Rebuild and you'll see real events, teams, and matches.

### Option B: Run the local backend (for backend development)

The app's debug build defaults to `http://10.0.2.2:8080/` (the emulator's alias for the host's `localhost`). Use Docker Compose to run the backend locally:

```bash
# In a separate directory:
git clone https://github.com/the-blue-alliance/the-blue-alliance.git
cd the-blue-alliance
docker compose up
```

This starts:
- **TBA server** at `http://localhost:8080` — the API the Android app talks to
- **Datastore emulator** at `http://localhost:8089`
- **Firebase emulator** at `http://localhost:4000` (admin UI)

To import data, visit `http://localhost:8080/local/bootstrap` in a browser.

The Docker Compose setup includes a Firebase Auth emulator, so Google Sign-In works against the local backend without any real Firebase configuration.

## Firebase project (optional)

A real Firebase project is **only** needed if you want to test Google Sign-In or push notifications against the production API. For everything else (including signing in via the local Docker backend) the stub `google-services.json` from step 3 is enough.

To set up a real project:

1. Create a project in the [Firebase console](https://console.firebase.google.com/) (e.g. `yourname-tba-dev`).
2. Add an Android app with package name `com.thebluealliance.androidclient.development`.
3. Add your debug signing SHA-1 fingerprint ([instructions](https://developers.google.com/android/guides/client-auth)).
4. Download the real `google-services.json` and overwrite `app/src/debug/google-services.json` with it.

## Code quality

The project enforces formatting and Android Lint in CI. Both run against every PR via [`ci-lint.yaml`](.github/workflows/ci-lint.yaml).

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

### git blame

A `.git-blame-ignore-revs` file tracks bulk-formatting commits (e.g. the initial ktlint sweep) so they don't pollute `git blame`. Configure your local git to respect it:

```bash
git config blame.ignoreRevsFile .git-blame-ignore-revs
```

GitHub's blame UI respects this file automatically; this command is only needed for CLI `git blame` and IDE integrations.

## Testing

```bash
./gradlew :app:testDebugUnitTest
```

Unit tests use JUnit 5 (Jupiter), MockK, Turbine for Flow testing, and Coroutines Test utilities.

## Alpha Testing

We publish pre-release builds to a closed alpha track on Google Play. To join, request access to the [thebluealliance-android-alpha](https://groups.google.com/g/thebluealliance-android-alpha) Google Group, then opt in to the alpha on the [Play Store listing](https://play.google.com/store/apps/details?id=com.thebluealliance.androidclient).
