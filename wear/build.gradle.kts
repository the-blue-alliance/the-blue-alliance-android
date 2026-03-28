import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.play.publisher)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(FileInputStream(file))
}

// --- Git tag-based versioning (shared with :app, offset for multi-APK) ---
// Wear version codes add 100_000_000 to avoid collisions with the phone app.
val gitDescribeResult = providers.exec {
    commandLine("git", "describe", "--tags", "--long", "--match", "v[0-9]*")
    isIgnoreExitValue = true
}
val gitDescribe = gitDescribeResult.result.get().exitValue.let { exitCode ->
    if (exitCode == 0) gitDescribeResult.standardOutput.asText.get().trim() else ""
}

val versionPattern = Regex("""^v(\d+)\.(\d+)\.(\d+)-(\d+)-g[0-9a-f]+$""")
val versionMatch = versionPattern.matchEntire(gitDescribe)

val vMajor = versionMatch?.groupValues?.get(1)?.toInt() ?: 0
val vMinor = versionMatch?.groupValues?.get(2)?.toInt() ?: 0
val vPatch = versionMatch?.groupValues?.get(3)?.toInt() ?: 0
val commitDistance = versionMatch?.groupValues?.get(4)?.toInt() ?: 0

val computedVersionCode = 100_000_000 + vMajor * 1_000_000 + vMinor * 10_000 + vPatch * 100 + commitDistance
val computedVersionName = if (commitDistance == 0) {
    "$vMajor.$vMinor.$vPatch"
} else {
    "$vMajor.$vMinor.$vPatch-dev.$commitDistance"
}

android {
    namespace = "com.thebluealliance.android.wear"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.thebluealliance.androidclient"
        minSdk = 30
        targetSdk = 34
        versionCode = computedVersionCode
        versionName = computedVersionName

        buildConfigField("String", "TBA_BASE_URL", "\"https://www.thebluealliance.com/\"")
        buildConfigField("String", "TBA_API_KEY", "\"\"")
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file(localProperties.getProperty("release.store.file", "release.keystore"))
            storePassword = localProperties.getProperty("release.store.password", "")
            keyAlias = localProperties.getProperty("release.key.alias", "")
            keyPassword = localProperties.getProperty("release.key.password", "")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".development"
            buildConfigField("String", "TBA_BASE_URL", "\"${
                localProperties.getProperty(
                    "tba.url.debug",
                    "http://10.0.2.2:8080/"
                )
            }\"")
            buildConfigField("String", "TBA_API_KEY", "\"${localProperties.getProperty("tba.api.key.debug", "tba-dev-key")}\"")
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

play {
    serviceAccountCredentials.set(rootProject.file(localProperties.getProperty("play.service.account.key", "play-service-account.json")))
    track.set("wear:alpha")
    defaultToAppBundles.set(true)
}

dependencies {
    // Wear OS Compose
    implementation(libs.wear.compose.material)
    implementation(libs.wear.compose.foundation)
    implementation(libs.activity.compose)

    // Complications
    implementation(libs.wear.complications.data.source.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // WorkManager
    implementation(libs.work.runtime)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Serialization
    implementation(libs.serialization.json)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.serialization)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)

    // AndroidX
    implementation(libs.core.ktx)
}
