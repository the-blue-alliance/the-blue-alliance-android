package com.thebluealliance.android.ui.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DefaultSeasonTest {
    @Test
    fun `skips seasons past the current one`() {
        assertEquals(2026, defaultSeason(listOf(2027, 2026, 2025), currentSeason = 2026))
    }

    @Test
    fun `picks the newest season up to the current one`() {
        assertEquals(2024, defaultSeason(listOf(2027, 2024, 2019), currentSeason = 2026))
    }

    @Test
    fun `falls back to the earliest season when all are later`() {
        assertEquals(2027, defaultSeason(listOf(2028, 2027), currentSeason = 2026))
    }

    @Test
    fun `is null with no seasons`() {
        assertNull(defaultSeason(emptyList(), currentSeason = 2026))
    }
}
