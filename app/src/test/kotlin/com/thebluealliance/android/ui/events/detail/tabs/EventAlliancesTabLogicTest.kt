package com.thebluealliance.android.ui.events.detail.tabs

import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.displayTitle
import com.thebluealliance.android.domain.model.displayTitleShort
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
    fun `Alliance displayTitle and displayTitleShort uses API name when present`() {
        assertEquals("Curie", alliance("Curie").displayTitle)
        assertEquals("Curie", alliance("Curie").displayTitleShort)
    }

    @Test
    fun `Alliance displayTitle and displayTitleShort falls back when name is null`() {
        assertEquals("Alliance 6", alliance(null).displayTitle)
        assertEquals("A6", alliance(null).displayTitleShort)
    }

    @Test
    fun `Alliance displayTitle and displayTitleShort falls back when name is blank`() {
        assertEquals("Alliance 6", alliance("   ").displayTitle)
        assertEquals("A6", alliance("   ").displayTitleShort)
    }

    @Test
    fun `Alliance displayShortTitle returns 'A#' even when name is 'Alliance #'`() {
        assertEquals("A6", alliance("Alliance 6").displayTitleShort)
    }
}

