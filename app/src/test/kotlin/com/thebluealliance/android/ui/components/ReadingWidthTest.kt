package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReadingWidthTest {
    @Test
    fun `no inset at or below the reading width`() {
        assertEquals(0.dp, readingInset(412.dp))
        assertEquals(0.dp, readingInset(840.dp))
    }

    @Test
    fun `wider windows center the column`() {
        assertEquals(92.dp, readingInset(1024.dp))
        assertEquals(220.dp, readingInset(1280.dp))
    }

    @Test
    fun `phone and rail-narrowed widths keep list padding unchanged`() {
        listOf(
            320.dp,
            360.dp,
            599.dp,
            600.dp - 80.dp,
        ).forEach { assertEquals(0.dp, readingInset(it)) }
    }

    @Test
    fun `a 1024dp panel beside the rail centers the column`() {
        assertEquals(52.dp, readingInset(1024.dp - 80.dp))
    }

    @Test
    fun `prose cap sits inside the column and above compact widths`() {
        assertTrue(ProseMaxWidth < ReadingMaxWidth)
        assertTrue(ProseMaxWidth >= 600.dp)
    }

    @Test
    fun `no inset just below the reading width`() {
        assertEquals(0.dp, readingInset(839.dp))
    }

    @Test
    fun `stat rows fill every phone width`() {
        listOf(320.dp, 328.dp, 372.dp, 380.dp, 440.dp, 600.dp).forEach {
            assertEquals(it, statRowWidth(it))
        }
    }

    @Test
    fun `stat rows cap on wide panels`() {
        assertEquals(StatRowMaxWidth, statRowWidth(601.dp))
        assertEquals(StatRowMaxWidth, statRowWidth(984.dp))
    }
}
