package com.thebluealliance.android.widget

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WidgetAnalyticsTest {
    @Test
    fun `tier classify matches the renderer breakpoints`() {
        assertEquals("tiny", WidgetSizeTier.classify(60, 60))
        assertEquals("minimal", WidgetSizeTier.classify(110, 60))
        assertEquals("square", WidgetSizeTier.classify(110, 110))
        assertEquals("compact", WidgetSizeTier.classify(250, 60))
        assertEquals("full", WidgetSizeTier.classify(250, 110))
        // Just below each boundary
        assertEquals("tiny", WidgetSizeTier.classify(109, 109))
        assertEquals("compact", WidgetSizeTier.classify(250, 109))
        assertEquals("minimal", WidgetSizeTier.classify(249, 109))
    }

    @Test
    fun `fold groups by state x tier and sums each metric`() {
        val snapshots =
            listOf(
                WidgetEventSnapshot(appWidgetId = 1, visibleMillis = 1_000),
                WidgetEventSnapshot(appWidgetId = 1, visibleMillis = 500),
                WidgetEventSnapshot(appWidgetId = 2, visibleMillis = 250),
            )
        val meta =
            mapOf(
                1 to ("current_event" to "full"),
                2 to ("upcoming" to "minimal"),
            )

        val groups = WidgetAnalytics.foldEngagement(snapshots) { meta[it] }

        assertEquals(2, groups.size)
        val full = groups.first { it.tier == "full" }
        assertEquals("current_event", full.widgetState)
        assertEquals(2, full.visibleWindowCount)
        assertEquals(1_500L, full.impressionMillis)
        val minimal = groups.first { it.tier == "minimal" }
        assertEquals("upcoming", minimal.widgetState)
        assertEquals(1, minimal.visibleWindowCount)
        assertEquals(250L, minimal.impressionMillis)
    }

    @Test
    fun `fold drops snapshots whose widget is gone`() {
        val snapshots = listOf(WidgetEventSnapshot(appWidgetId = 99, visibleMillis = 9_999))
        val groups = WidgetAnalytics.foldEngagement(snapshots) { null }
        assertEquals(0, groups.size)
    }

    @Test
    fun `same state different tiers stay separate groups`() {
        val snapshots =
            listOf(
                WidgetEventSnapshot(appWidgetId = 1, visibleMillis = 100),
                WidgetEventSnapshot(appWidgetId = 2, visibleMillis = 200),
            )
        val meta = mapOf(1 to ("current_event" to "full"), 2 to ("current_event" to "square"))
        val groups = WidgetAnalytics.foldEngagement(snapshots) { meta[it] }
        assertEquals(2, groups.size)
    }
}
