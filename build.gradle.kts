plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.aboutlibraries) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.play.publisher) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
        outputToConsole.set(true)
        ignoreFailures.set(false)
    }
}

// TBA API config for DEBUG builds (release builds take their key from Remote Config at runtime).
// local.properties: `tba.url.debug` picks the server and `tba.api.key.debug` is its key, as
// always. Optionally also set `tba.api.key.prod`: it's used instead whenever tba.url.debug points
// at thebluealliance.com, so switching servers means editing only the URL line. Files without
// tba.api.key.prod build exactly as before; with nothing set, each module keeps its own default.
val tbaLocalProperties =
    java.util.Properties().apply {
        rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use(::load)
    }

// The existing properties are read raw, exactly as the modules always did (an empty
// tba.api.key.debug stays empty, e.g. the TV app's mock-data mode).
val tbaDebugUrl: String? = tbaLocalProperties.getProperty("tba.url.debug")
val tbaDebugHost: String? = tbaDebugUrl?.let { runCatching { java.net.URI(it).host }.getOrNull() }
val tbaUrlIsProd =
    tbaDebugHost == "thebluealliance.com" || tbaDebugHost?.endsWith(".thebluealliance.com") == true
extra["tbaDebugUrl"] = tbaDebugUrl
extra["tbaDebugKey"] =
    tbaLocalProperties.getProperty("tba.api.key.prod")?.takeIf { tbaUrlIsProd && it.isNotBlank() }
        ?: tbaLocalProperties.getProperty("tba.api.key.debug")
