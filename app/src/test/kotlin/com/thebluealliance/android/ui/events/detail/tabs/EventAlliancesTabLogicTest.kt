package com.thebluealliance.android.ui.events.detail.tabs

import com.thebluealliance.android.domain.model.Alliance
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventAlliancesTabLogicTest {

    private fun alliance(name: String?) = Alliance(
        eventKey = "2025cmptx",
        number = 6,
        name = name,
        picks = listOf("frc1", "frc2", "frc3"),
        declines = emptyList(),
        backupIn = null,
        backupOut = null,
    )

    @Test
    fun `allianceTitle uses API name when present`() {
        assertEquals("Curie", allianceTitle(alliance("Curie")))
    }

    @Test
    fun `allianceTitle falls back when name is null`() {
        assertEquals("Alliance 6", allianceTitle(alliance(null)))
    }

    @Test
    fun `allianceTitle falls back when name is blank`() {
        assertEquals("Alliance 6", allianceTitle(alliance("   ")))
    }
}

