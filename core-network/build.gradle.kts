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

    // ApiKeyProvider + TbaClientFactory live here (RFC 0004 network unification, #1393).
    // FirebaseRemoteConfig, Retrofit and OkHttpClient appear in their public signatures, so
    // they are `api`; the converter, logging interceptor and core-ktx are internal.
    api(platform(libs.firebase.bom))
    api(libs.firebase.config)
    api(libs.retrofit)
    api(libs.okhttp)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.core.ktx)
}
