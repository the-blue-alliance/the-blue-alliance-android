plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.thebluealliance.android.core.network"
    compileSdk = 37

    defaultConfig {
        // 23 is the floor across consumers (:tv minSdk = 23, :app = 26, :wear = 30). This module
        // is pure kotlinx-serialization data classes with no version-dependent Android APIs.
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(libs.serialization.json)
}
