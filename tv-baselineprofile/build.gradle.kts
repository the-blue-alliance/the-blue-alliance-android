// Generates the :tv Baseline Profile and benchmarks its effect on cold start. Never part of a
// release build: run `./gradlew :tv:generateBaselineProfile` by hand with a TV device attached and
// commit the result (see tv/build.gradle.kts).
plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.thebluealliance.android.tv.baselineprofile"
    compileSdk {
        version = release(37) { minorApiLevel = 1 }
    }

    defaultConfig {
        // Baseline Profile capture without root needs API 33+; Macrobenchmark itself needs 28+.
        minSdk = 28
        targetSdk = 37
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":tv"
    // Macrobenchmark drives the target app from its own process instead of instrumenting it.
    experimentalProperties["android.experimental.self-instrumenting"] = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

baselineProfile {
    // Use whatever TV emulator or device is attached (ANDROID_SERIAL picks one).
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.runner)
    implementation(libs.uiautomator)
    implementation(libs.benchmark.macro.junit4)
}
