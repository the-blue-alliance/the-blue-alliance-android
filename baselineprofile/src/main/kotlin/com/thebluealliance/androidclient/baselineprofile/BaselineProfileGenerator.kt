package com.thebluealliance.androidclient.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebluealliance.androidclient.baselineprofile.TbaJourney.runEventsToDetailJourney
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates the app's Baseline Profile by driving the critical user journey on a
 * non-minified release build and recording the classes/methods touched. AGP compiles the
 * captured rules into the binary `baseline.prof` shipped in the release AAB, so the next
 * install AOT-compiles the hot startup + events-list + event-detail paths instead of
 * relying on JIT (or waiting days/weeks for a Play cloud profile).
 *
 * Generate / regenerate with (see baselineprofile/README.md for the key/emulator setup):
 *   ./gradlew :app:generateBaselineProfile -PuseConnectedDevices=true
 * which writes the rules to app/src/main/generated/baselineProfiles/{baseline-prof.txt,
 * startup-prof.txt} (committed to git).
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() =
        rule.collect(
            // The release variant under test (no .development suffix; that is debug only).
            packageName = TARGET_PACKAGE_NAME,
            // Also emit a startup profile so DEX layout is ordered for fast cold start.
            includeInStartupProfile = true,
        ) {
            pressHome()
            startActivityAndWait()
            // Cold start -> events list scroll -> event detail tab journey. Each leg
            // hard-fails if its content never renders, so we never commit a thin profile.
            runEventsToDetailJourney()
        }

    companion object {
        /**
         * applicationId of the :app *release* variant the profile is generated against.
         * Debug builds add a `.development` suffix, but Baseline Profiles only apply to the
         * non-debuggable release variant, so we target the unsuffixed id here.
         */
        const val TARGET_PACKAGE_NAME = "com.thebluealliance.androidclient"
    }
}
