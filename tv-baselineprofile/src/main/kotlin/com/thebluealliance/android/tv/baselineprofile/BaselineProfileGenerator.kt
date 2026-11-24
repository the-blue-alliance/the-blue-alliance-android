package com.thebluealliance.android.tv.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Records the :tv Baseline Profile. Run `./gradlew :tv:generateBaselineProfile` with a TV emulator or
 * device (API 33+, or rooted) attached; the plugin writes the rules to
 * tv/src/main/generated/baselineProfiles/baseline-prof.txt, which is committed.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() =
        rule.collect(packageName = TV_PACKAGE) {
            startAndWaitForFeed()
            browseFeedAndAbout()
        }
}
