import java.io.FileInputStream
import java.time.Instant
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.play.publisher)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(FileInputStream(file))
}

// --- Git tag-based versioning ---
// Tags must be in the format "vMAJOR.MINOR.PATCH" (e.g., v10.9.0)
// versionCode formula: MAJOR * 1_000_000 + MINOR * 10_000 + PATCH * 100
// This matches the legacy app's formula and leaves room for hotfix candidates.
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

val computedVersionCode = vMajor * 1_000_000 + vMinor * 10_000 + vPatch * 100 + commitDistance
val computedVersionName = if (commitDistance == 0) {
    "$vMajor.$vMinor.$vPatch"
} else {
    "$vMajor.$vMinor.$vPatch-dev.$commitDistance"
}

android {
    namespace = "com.thebluealliance.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.thebluealliance.androidclient"
        minSdk = 26
        targetSdk = 36
        versionCode = computedVersionCode
        versionName = computedVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TBA_BASE_URL", "\"https://www.thebluealliance.com/\"")
        buildConfigField("String", "TBA_API_KEY", "\"\"")
        buildConfigField("String", "BUILD_TIME", "\"${Instant.now()}\"")
        buildConfigField("String", "GIT_HASH", "\"${providers.exec { commandLine("git", "rev-parse", "--short", "HEAD") }.standardOutput.asText.get().trim()}\"")

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

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

play {
    serviceAccountCredentials.set(rootProject.file(localProperties.getProperty("play.service.account.key", "play-service-account.json")))
    track.set("alpha")
    defaultToAppBundles.set(true)
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    debugImplementation(libs.compose.ui.tooling)

    // AndroidX
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.navigation3)
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.androidx.shortcuts)
    implementation(libs.androidx.lifecycle.process)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.lifecycle.viewmodel.compose)

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

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.datastore.preferences)

    // Coil
    implementation(libs.coil.compose)

    // AboutLibraries
    implementation(libs.aboutlibraries.compose.m3)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.config)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // Credentials (Google Sign-In)
    implementation(libs.credentials)
    implementation(libs.credentials.play)
    implementation(libs.googleid)

    // Testing
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    debugImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test)
}
