import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(FileInputStream(file))
}

android {
    namespace = "com.thebluealliance.android.wear"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.thebluealliance.androidclient"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

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
