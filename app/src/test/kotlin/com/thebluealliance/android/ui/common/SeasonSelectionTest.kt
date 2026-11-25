package com.thebluealliance.android.ui.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SeasonSelectionTest {
    @Test
    fun `takes a default before the user picks`() {
        val selection = SeasonSelection(2026)
        assertTrue(selection.offerDefault(2025))
        assertEquals(2025, selection.year.value)
    }

    @Test
    fun `a default matching the selection doesn't move it`() {
        assertFalse(SeasonSelection(2026).offerDefault(2026))
    }

    @Test
    fun `ignores defaults once the user picks`() {
        val selection = SeasonSelection(2026)
        selection.pick(2019)
        assertFalse(selection.wouldTakeDefault(2025))
        assertFalse(selection.offerDefault(2025))
        assertEquals(2019, selection.year.value)
    }

    @Test
    fun `remembers whether the user picked`() {
        val selection = SeasonSelection(2026)
        selection.offerDefault(2025)
        assertFalse(selection.hasUserPick)
        selection.pick(2019)
        assertTrue(selection.hasUserPick)
    }
}
