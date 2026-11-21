plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.thebluealliance.android.core.theme"
    compileSdk = 37

    defaultConfig {
        // The floor across consumers (:tv minSdk = 23). Plain Color constants, no platform APIs.
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Color appears in every public token, so it's api. No Material library here on purpose: each
    // app maps these tokens into its own (phone material3, tv-material, wear-compose-material3).
    api(platform(libs.compose.bom))
    api(libs.compose.ui.graphics)
}
