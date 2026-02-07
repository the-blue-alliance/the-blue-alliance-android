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
- **myTBA** -- Sign in with Google to save favorite teams and events, and subscribe to push notifications for match scores, upcoming matches, schedule changes, and more
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

## Development Setup

1. Install [Android Studio](https://developer.android.com/studio) and a JDK (17+).
2. Fork and clone this repository.
3. Set up a Firebase project for development:
   1. Create a project in the [Firebase console](https://console.firebase.google.com/) (e.g. `yourname-tba-dev`).
   2. Add an Android app with package name `com.thebluealliance.androidclient.development`.
   3. Add your debug signing SHA-1 fingerprint ([instructions](https://developers.google.com/android/guides/client-auth)).
   4. Download `google-services.json` and place it in `app/src/debug/`.
4. Get a TBA API key from the [TBA Account page](https://www.thebluealliance.com/account) and add it to `local.properties`:
   ```
   tba.api.key.debug=YOUR_KEY_HERE
   ```
5. Build and run:
   ```bash
   ./gradlew :app:installDebug
   ```

## Testing

```bash
./gradlew :app:testDebugUnitTest
```
