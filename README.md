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

### Prerequisites

1. Install [Android Studio](https://developer.android.com/studio) and a JDK (17+).
2. Install [Docker](https://www.docker.com/products/docker-desktop/) (includes Docker Compose).
3. Fork and clone this repository.

### Local backend server

The app's debug build connects to a local TBA server at `http://10.0.2.2:8080/` (the emulator's alias for `localhost`). Use Docker Compose to run the backend locally:

```bash
# Clone the backend repo (if you haven't already)
git clone https://github.com/the-blue-alliance/the-blue-alliance.git

# Start all services (TBA server, Datastore emulator, Firebase emulator)
cd the-blue-alliance
docker compose up
```

This starts:
- **TBA server** at `http://localhost:8080` -- the API the Android app talks to
- **Datastore emulator** at `http://localhost:8089`
- **Firebase emulator** at `http://localhost:4000` (admin UI)

To import data into your local server, visit `http://localhost:8080/local/bootstrap` in a browser.

Running the local backend is the easiest way to test logged-in features like myTBA favorites and subscriptions, since the Docker Compose setup includes a Firebase Auth emulator that handles sign-in without any additional configuration.

> **Tip:** If you'd rather skip running the backend locally, you can point the app at the production API by adding this to `local.properties`:
> ```
> tba.url.debug=https://www.thebluealliance.com/
> tba.api.key.debug=YOUR_KEY_HERE
> ```
> Get an API key from the [TBA Account page](https://www.thebluealliance.com/account).

### Firebase project (optional)

A Firebase project is **not required** if you're using the local Docker Compose backend -- the Firebase Auth emulator handles authentication automatically.

If you want to test against the production API with real Google Sign-In or push notifications, set up a Firebase project:

1. Create a project in the [Firebase console](https://console.firebase.google.com/) (e.g. `yourname-tba-dev`).
2. Add an Android app with package name `com.thebluealliance.androidclient.development`.
3. Add your debug signing SHA-1 fingerprint ([instructions](https://developers.google.com/android/guides/client-auth)).
4. Download `google-services.json` and place it in `app/src/debug/`.

### Build and run

```bash
./gradlew :app:installDebug
```

## Alpha Testing

We publish pre-release builds to a closed alpha track on Google Play. To join, request access to the [thebluealliance-android-alpha](https://groups.google.com/g/thebluealliance-android-alpha) Google Group, then opt in to the alpha on the [Play Store listing](https://play.google.com/store/apps/details?id=com.thebluealliance.androidclient).

## Testing

```bash
./gradlew :app:testDebugUnitTest
```
