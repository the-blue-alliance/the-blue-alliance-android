import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
  alias(libs.plugins.android.test)
  alias(libs.plugins.baselineprofile)
}

android {
  namespace = "com.thebluealliance.android.baselineprofile"
  compileSdk {
    version = release(36) {
      minorApiLevel = 1
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  defaultConfig {
    minSdk = 28
    targetSdk = 36

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  targetProjectPath = ":app"

  // This code creates the gradle managed device used to generate baseline profiles.
  // To use GMD please invoke generation through the command line:
  // ./gradlew :app:generateBaselineProfile
  testOptions.managedDevices.allDevices {
    create<ManagedVirtualDevice>("pixel6Api34") {
      device = "Pixel 6"
      apiLevel = 34
      systemImageSource = "google-atd"
    }
  }
}

// This is the configuration block for the Baseline Profile plugin.
// You can specify to run the generators on a managed devices or connected devices.
baselineProfile {
  managedDevices += "pixel6Api34"
  useConnectedDevices = false
}

dependencies {
  implementation(libs.androidx.junit)
  implementation(libs.androidx.espresso.core)
  implementation(libs.androidx.uiautomator)
  implementation(libs.androidx.benchmark.macro.junit4)
}

androidComponents {
  onVariants { v ->
    val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
    v.instrumentationRunnerArguments.put(
      "targetAppId",
      v.testedApks.map { artifactsLoader.load(it)?.applicationId }
    )
  }
}
