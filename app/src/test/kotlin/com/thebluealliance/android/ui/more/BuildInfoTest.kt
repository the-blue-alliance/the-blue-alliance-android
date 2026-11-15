package com.thebluealliance.android.ui.more

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BuildInfoTest {
    @Test
    fun versionLabelIsVersionOnly() {
        assertEquals("v7.4.0", versionLabel(versionName = "7.4.0"))
    }

    @Test
    fun versionLabelDropsPrereleaseSuffix() {
        assertEquals("v11.11.2", versionLabel(versionName = "11.11.2-dev.86"))
    }

    @Test
    fun buildTimeLabelFormatsIsoInstantInUtc() {
        assertEquals("Nov 12, 2026, 03:05 UTC", buildTimeLabel("2026-11-12T03:05:48.121542Z"))
    }

    @Test
    fun buildTimeLabelPassesThroughUnparseableInput() {
        assertEquals("unknown", buildTimeLabel("unknown"))
    }
}
