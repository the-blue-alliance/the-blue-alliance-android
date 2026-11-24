package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TableWidthTest {
    @Test
    fun `rows that fit don't scroll`() {
        assertNull(sideScrollRowWidth(360f, null, 12f, frozenCount = 0))
        assertNull(sideScrollRowWidth(360f, listOf(100f, 236f), 12f, frozenCount = 0))
        assertNull(sideScrollRowWidth(360f, listOf(40f, 60f, 100f, 136f), 12f, frozenCount = 2))
    }

    @Test
    fun `overflowing rows scroll their columns and end padding`() {
        assertEquals(372f, sideScrollRowWidth(360f, listOf(100f, 260f), 12f, frozenCount = 0))
    }

    @Test
    fun `frozen columns stay out of the scrolling width`() {
        assertEquals(
            272f,
            sideScrollRowWidth(360f, listOf(40f, 60f, 100f, 160f), 12f, frozenCount = 2),
        )
    }
}
