pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "the-blue-alliance-android"
include(":app")
include(":wear")
include(":tv")
include(":core-network")
include(":baselineprofile")
