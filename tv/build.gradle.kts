import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.play.publisher)
}

val localProperties =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) load(FileInputStream(file))
    }

// --- Git tag-based versioning (shared with :app, offset for multi-APK) ---
// TV version codes add 200_000_000 so they never collide with the phone app (base
// band) or the Wear app (+100_000_000) within the shared applicationId.
val gitDescribeResult =
    providers.exec {
        commandLine("git", "describe", "--tags", "--long", "--match", "v[0-9]*")
        isIgnoreExitValue = true
    }
val gitDescribe =
    gitDescribeResult.result.get().exitValue.let { exitCode ->
        if (exitCode == 0) {
            gitDescribeResult.standardOutput.asText
                .get()
                .trim()
        } else {
            ""
        }
    }

val versionPattern = Regex("""^v(\d+)\.(\d+)\.(\d+)-(\d+)-g[0-9a-f]+$""")
val versionMatch = versionPattern.matchEntire(gitDescribe)

val vMajor = versionMatch?.groupValues?.get(1)?.toInt() ?: 0
val vMinor = versionMatch?.groupValues?.get(2)?.toInt() ?: 0
val vPatch = versionMatch?.groupValues?.get(3)?.toInt() ?: 0
val commitDistance = versionMatch?.groupValues?.get(4)?.toInt() ?: 0

val computedVersionCode =
    maxOf(1, 200_000_000 + vMajor * 1_000_000 + vMinor * 10_000 + vPatch * 100 + commitDistance)
val computedVersionName =
    if (commitDistance == 0) {
        "$vMajor.$vMinor.$vPatch"
    } else {
        "$vMajor.$vMinor.$vPatch-dev.$commitDistance"
    }

// TBA debug config mirrors the other modules. A blank debug key falls back to the
// bundled sample events (USE_MOCK_DATA), so a fresh clone builds a working TV app
// with no secrets configured.
val tbaDebugKey: String = localProperties.getProperty("tba.api.key.debug", "")
val tbaDebugUrl: String = localProperties.getProperty("tba.url.debug", "http://10.0.2.2:8080/")
val tbaProdUrl = "https://www.thebluealliance.com/"

android {
    namespace = "com.thebluealliance.android.tv"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.thebluealliance.androidclient"
        minSdk = 23
        targetSdk = 36
        versionCode = computedVersionCode
        versionName = computedVersionName

        // Production defaults; the debug buildType overrides these below. Release builds
        // ship with no embedded TBA key — ApiKeyProvider fetches the live key from
        // Firebase Remote Config (apiv3_auth_key), mirroring :app and :wear.
        buildConfigField("String", "TBA_API_KEY", "\"\"")
        buildConfigField("String", "TBA_BASE_URL", "\"$tbaProdUrl\"")
        buildConfigField("boolean", "USE_MOCK_DATA", "false")
    }

    signingConfigs {
        create("release") {
            storeFile =
                rootProject.file(
                    localProperties.getProperty("release.store.file", "release.keystore"),
                )
            storePassword = localProperties.getProperty("release.store.password", "")
            keyAlias = localProperties.getProperty("release.key.alias", "")
            keyPassword = localProperties.getProperty("release.key.password", "")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".development"
            buildConfigField("String", "TBA_API_KEY", "\"$tbaDebugKey\"")
            buildConfigField("String", "TBA_BASE_URL", "\"$tbaDebugUrl\"")
            buildConfigField("boolean", "USE_MOCK_DATA", "${tbaDebugKey.isBlank()}")
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            // R8 + resource shrinking stay off for the first TV release until a minified
            // build is verified on a TV device; the unminified APK is already small.
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
        // Advisory rule that fires whenever a newer (beta) SDK exists. We bump
        // targetSdk deliberately, not on every API release.
        disable += "OldTargetApi"
        // Advisory rules that fire whenever a newer dependency version exists.
        // Dependabot already handles upgrades; otherwise every release of any
        // dep would turn every open PR red. (AGP exposes this as two issue IDs
        // depending on version — disable both.)
        disable += "GradleDependency"
        disable += "NewerVersionAvailable"
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

play {
    serviceAccountCredentials.set(
        rootProject.file(
            localProperties.getProperty("play.service.account.key", "play-service-account.json"),
        ),
    )
    // Piggyback on :app's "alpha" track. Play routes the AAB to TV devices via the
    // <uses-feature android:name="android.software.leanback" android:required="true"/>
    // declaration + LEANBACK_LAUNCHER intent, so phone testers get :app and TV testers
    // get :tv from a single release.
    track.set("alpha")
    defaultToAppBundles.set(true)
    // Phone + TV publish into one shared Play edit on alpha so the release
    // contains both AABs (Play routes by leanback uses-feature). Neither module
    // commits its own edit; release.sh invokes :commitEditFor… last to flush it.
    commit.set(false)
}

dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    // Compose for TV (focus-aware Material components for the 10-foot UI)
    implementation(libs.androidx.tv.material)
    debugImplementation(libs.compose.ui.tooling)

    // AndroidX
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    // Coroutines
    implementation(libs.coroutines.android)

    // Serialization
    implementation(libs.serialization.json)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Firebase — Crashlytics + Analytics auto-initialize via the google-services
    // plugin and the bundled google-services.json. Remote Config fetches the TBA
    // API key from apiv3_auth_key on release builds. All matching :app and :wear.
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)

    // Core library desugaring (java.time on minSdk 23)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testing (JUnit 5 / Jupiter, matching :app)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}
