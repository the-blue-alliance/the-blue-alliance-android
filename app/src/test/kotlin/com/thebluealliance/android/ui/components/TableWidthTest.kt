package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TableWidthTest {
    @Test
    fun `no inset at or below the table width`() {
        assertEquals(0.dp, tableInset(412.dp))
        assertEquals(0.dp, tableInset(720.dp))
    }

    @Test
    fun `wider windows center the table`() {
        assertEquals(60.dp, tableInset(840.dp))
        assertEquals(152.dp, tableInset(1024.dp))
    }

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
