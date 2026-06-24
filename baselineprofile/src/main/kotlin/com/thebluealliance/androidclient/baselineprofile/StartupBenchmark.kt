package com.thebluealliance.androidclient.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebluealliance.androidclient.baselineprofile.TbaJourney.scrollEventsList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Before/after measurement that proves (or disproves) the cold-start win and guards against
 * regressions. Run BOTH variants and compare the reported `timeToInitialDisplayMs` medians
 * (use a PHYSICAL device for a trustworthy number — see baselineprofile/README.md):
 *
 *   ./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest -PuseConnectedDevices=true
 *
 *  - [startupNoCompilation] is the floor: JIT-only, the experience of a fresh install with
 *    no baseline profile and no Play cloud profile.
 *  - [startupBaselineProfile] requires the committed Baseline Profile to be installed and
 *    AOT-compiled, i.e. the experience this change ships to users on first launch.
 *
 * The delta between the two medians is the actual user-facing win. Rule of thumb: ship if
 * the median TTID improves by ~15-20%+; treat a smaller delta as noise. Results print to the
 * test output and to build/outputs/connected_android_test_additional_output as JSON, so a CI
 * job can track the number over time rather than relying on a one-off manual run.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())

    @Test
    fun startupBaselineProfile() =
        startup(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    private fun startup(compilationMode: CompilationMode) =
        rule.measureRepeated(
            packageName = BaselineProfileGenerator.TARGET_PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 10,
            setupBlock = {
                pressHome()
            },
        ) {
            startActivityAndWait()
            // Touch the events list so TTID reflects real first-screen work, not a bare
            // Activity. Best-effort: a slow/empty backend must not fail the measurement.
            runCatching { scrollEventsList() }
        }
}
