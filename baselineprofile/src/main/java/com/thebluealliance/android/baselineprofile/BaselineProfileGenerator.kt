package com.thebluealliance.android.baseline

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.textAsString
import androidx.test.uiautomator.uiAutomator
import androidx.test.uiautomator.waitForStable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their performance.
 * Refer to the [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You can run the generator with the "Generate Baseline Profile" run configuration in Android Studio or
 * the equivalent `generateBaselineProfile` gradle task:
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [com.example.baselineprofile.StartupBenchmarks] benchmark.
 *
 * When using this class to generate a baseline profile, only API 33+ or rooted API 28+ are supported.
 *
 * The minimum required version of androidx.benchmark to generate a baseline profile is 1.2.0.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

  @get:Rule
  val rule = BaselineProfileRule()

  @Test
  fun generate() {
    // The application id for the running build variant is read from the instrumentation arguments.
    rule.collect(
      packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
        ?: throw Exception("targetAppId not passed as instrumentation runner arg"),

      // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
      includeInStartupProfile = true
    ) {
      // This block defines the app's critical user journey. Here we are interested in
      // optimizing for app startup. But you can also navigate and scroll through your most important UI.

      // Start default activity for your app
      pressHome()
      startActivityAndWait()

      uiAutomator {
          // Go to teams tab, wait for team 1 to be displayed
          onElement { textAsString() == "Teams" }.click()
          onElement { textAsString()?.contains("1") == true }

          // Go to Districts tab, wait for California to be displayed
          onElement { textAsString() == "Districts" }.click()
          onElement { textAsString()?.contains("California") == true }

          // Click search icon, type "10" (which will have both teams and events), wait for results
          onElement { contentDescription == "Search" }.click()
          onElement { isFocused }.setText("10")
          // Match on team 100, because "10" will also be in the search box. This waits for actual results
          onElement { textAsString()?.contains("100") == true }.waitForStable()
      }
    }
  }
}
