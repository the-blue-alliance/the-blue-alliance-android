plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.thebluealliance.android.core.theme"
    compileSdk {
        version = release(37) { minorApiLevel = 1 }
    }

    defaultConfig {
        // The floor across consumers (:tv minSdk = 24). Plain Color constants, no platform APIs.
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    // Color appears in every public token, so it's api. No Material library here on purpose: each
    // app maps these tokens into its own (phone material3, tv-material, wear-compose-material3).
    api(platform(libs.compose.bom))
    api(libs.compose.ui.graphics)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}
