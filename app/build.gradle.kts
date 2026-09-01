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

val localProperties =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) load(FileInputStream(file))
    }

// --- Git tag-based versioning ---
// Tags must be in the format "vMAJOR.MINOR.PATCH" (e.g., v10.9.0)
// versionCode formula: MAJOR * 1_000_000 + MINOR * 10_000 + PATCH * 100
// This matches the legacy app's formula and leaves room for hotfix candidates.
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
    maxOf(
        1,
        vMajor * 1_000_000 + vMinor * 10_000 + vPatch * 100 + commitDistance,
    )
val computedVersionName =
    if (commitDistance == 0) {
        "$vMajor.$vMinor.$vPatch"
    } else {
        "$vMajor.$vMinor.$vPatch-dev.$commitDistance"
    }

android {
    namespace = "com.thebluealliance.android"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.thebluealliance.androidclient"
        minSdk = 26
        targetSdk = 37
        versionCode = computedVersionCode
        versionName = computedVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TBA_BASE_URL", "\"https://www.thebluealliance.com/\"")
        buildConfigField("String", "TBA_API_KEY", "\"\"")
        buildConfigField("String", "BUILD_TIME", "\"${Instant.now()}\"")
        buildConfigField(
            "String",
            "GIT_HASH",
            "\"${providers.exec {
                commandLine(
                    "git",
                    "rev-parse",
                    "--short",
                    "HEAD",
                )
            }.standardOutput.asText.get().trim()}\"",
        )
    }

    // "gms" = Google Play (Google Mobile Services present). "metavr" = Meta Horizon Store,
    // which is AOSP with no GMS, so sign-in and push are swapped out per source set.
    flavorDimensions += "distribution"
    productFlavors {
        create("gms") {
            dimension = "distribution"
        }
        create("metavr") {
            dimension = "distribution"
            // Horizon OS is Android 14; Meta requires targetSdk 34 for new store apps.
            targetSdk = 34
        }
    }

    // MetaVR ships through the Meta Horizon Store, not Play — keep gradle-play-publisher
    // from generating publish tasks for its variants.
    playConfigs {
        register("metavr") {
            enabled.set(false)
        }
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
            buildConfigField(
                "String",
                "TBA_BASE_URL",
                "\"${
                    localProperties.getProperty(
                        "tba.url.debug",
                        "http://10.0.2.2:8080/",
                    )
                }\"",
            )
            buildConfigField(
                "String",
                "TBA_API_KEY",
                "\"${localProperties.getProperty("tba.api.key.debug", "tba-dev-key")}\"",
            )
            // Debug builds normally point Firebase Auth at the local emulator and sign in a
            // fake user instead of running the real flow. Set tba.auth.emulator=false to
            // exercise the real sign-in flow.
            buildConfigField(
                "boolean",
                "AUTH_EMULATOR",
                localProperties.getProperty("tba.auth.emulator", "true"),
            )
        }
        release {
            buildConfigField("boolean", "AUTH_EMULATOR", "false")
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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

    lint {
        warningsAsErrors = true
        abortOnError = true
        checkDependencies = true
        // Advisory rule that fires whenever a newer (beta) SDK exists. We
        // bump targetSdk deliberately, not on every API release.
        disable += "OldTargetApi"
        // Advisory rules that fire whenever a newer dependency version exists.
        // Dependabot already handles upgrades; otherwise every release of any
        // dep would turn every open PR red. (AGP exposes this as two issue IDs
        // depending on version — disable both.)
        disable += "GradleDependency"
        disable += "NewerVersionAvailable"
        // False positive for adaptive-icon XML in mipmap-anydpi alongside
        // legacy PNG fallbacks in density buckets — this is intentional.
        disable += "IconXmlAndPng"
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
    track.set("alpha")
    defaultToAppBundles.set(true)
    // :app is the COMMITTER of the shared alpha Play edit (default commit=true).
    // :tv sets commit.set(false) so its publish task only stages — :app's commit
    // marker is what actually flushes the edit. Order is enforced below: :app's
    // publishReleaseBundle must run AFTER :tv's, else :tv's getOrCreateEditId
    // discards :app's already-uploaded edit and starts a fresh one.
}

afterEvaluate {
    tasks.named("publishGmsReleaseBundle").configure {
        mustRunAfter(":tv:publishReleaseBundle")
    }
    tasks.named("promoteGmsReleaseArtifact").configure {
        mustRunAfter(":tv:promoteReleaseArtifact")
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

// TODO(kotlin-2.4): Hilt 2.59.2's annotation-processor classpath bundles
// kotlin-metadata-jvm 2.2.20, which can't parse Kotlin 2.4.0 class metadata
// (`hiltJavaCompile*` fails with "maximum supported version is 2.3.0"). Force the
// matching kotlin-metadata-jvm onto every configuration until Hilt releases a
// version that bundles >= 2.4.0. See https://github.com/google/dagger/issues/5001.
configurations.configureEach {
    resolutionStrategy {
        force(libs.kotlin.metadata.jvm)
    }
}

dependencies {
    // Modules
    implementation(project(":core-network"))

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

    // Glance (App Widgets) — Horizon OS has no widget host, so gms-only.
    "gmsImplementation"(libs.glance.appwidget)
    "gmsImplementation"(libs.glance.material3)
    "gmsImplementation"(libs.glance.preview)
    "gmsImplementation"(libs.glance.appwidget.preview)

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
    implementation(libs.coil.network.okhttp)

    // AboutLibraries
    implementation(libs.aboutlibraries.compose.m3)

    // Firebase. Auth, Remote Config, Crashlytics, and Analytics all work without Google
    // Play services, so they stay common; Cloud Messaging does not and is gms-only.
    // https://firebase.google.com/docs/android/android-play-services
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.config)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    "gmsImplementation"(libs.firebase.messaging)

    // Google Sign-In via Credential Manager — Play services only, so gms-only.
    "gmsImplementation"(libs.credentials)
    "gmsImplementation"(libs.credentials.play)
    "gmsImplementation"(libs.googleid)

    // Testing
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    debugImplementation(libs.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test)
}
