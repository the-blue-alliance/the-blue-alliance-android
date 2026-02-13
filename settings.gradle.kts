pluginManagement {
    repositories {
        maven { url = uri("https://maven.google.com") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://maven.google.com") }
        google()
        mavenCentral()
    }
}

rootProject.name = "the-blue-alliance-android"
include(":app")
