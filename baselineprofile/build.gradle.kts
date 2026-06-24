import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.baselineprofile)
    // AGP 9 enables built-in Kotlin by default, so we do NOT apply
    // org.jetbrains.kotlin.android here. The module's Kotlin compiles via AGP.
}

android {
    namespace = "com.thebluealliance.androidclient.baselineprofile"
    compileSdk = 37

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        // Baseline Profile generation requires API 28+ to run; root-free capture
        // (no rooted/userdebug image) needs API 33+.
        minSdk = 28
        // AGP 9 changed targetSdk defaulting for test modules; set it explicitly.
        targetSdk = 37
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // The module under test whose profile we are generating.
    targetProjectPath = ":app"

    // Gradle Managed Device for deterministic, hands-off capture (no externally-attached
    // emulator needed). AOSP (userdebug) images produce cleaner profiles than google_apis
    // images. The on-demand CI job (.github/workflows/baseline-profile.yaml) instead boots a
    // connected emulator via android-emulator-runner and passes -PuseConnectedDevices=true.
    @Suppress("UnstableApiUsage")
    testOptions {
        managedDevices {
            allDevices {
                create<ManagedVirtualDevice>("pixel6Api34") {
                    device = "Pixel 6"
                    apiLevel = 34
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

// Which variant of :app to generate the profile against. The release variant is
// non-debuggable and R8-optimized, which is what ships to users, so we benchmark
// and profile that one (AGP creates a nonMinifiedRelease variant for capture).
baselineProfile {
    // Capture against the Gradle Managed Device by default (deterministic). Pass
    // -PuseConnectedDevices=true to instead capture against an already-running emulator
    // (the on-demand CI job boots one via android-emulator-runner; locally point it at a
    // running emulator + a read key — see baselineprofile/README.md). The property avoids
    // editing this file per-environment.
    val useConnected = (providers.gradleProperty("useConnectedDevices").orNull == "true")
    managedDevices += "pixel6Api34"
    useConnectedDevices = useConnected
}

dependencies {
    // In a com.android.test module the module *is* the instrumentation test, so the
    // benchmark/test deps go on the default (implementation) configuration.
    implementation(libs.benchmark.macro.junit4)
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.junit4)
    implementation(libs.espresso.core)
    implementation(libs.uiautomator)
}
