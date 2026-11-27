package com.thebluealliance.android.ui.components

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
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

    @Test
    fun `the sheet has no margins until the window outgrows the column`() {
        assertTrue(sheetInset(800f, 840f) <= 0f)
        assertEquals(0f, sheetInset(840f, 840f))
    }

    @Test
    fun `sheet margins are even whole pixels`() {
        assertEquals(92f, sheetInset(1024f, 840f))
        assertEquals(2f, sheetInset(843f, 840f))
    }

    @Test
    fun `margins narrower than the minimum are not drawn`() {
        val min = MinSheetMargin.value
        // 850-888dp windows, and 930-968dp beside the 80dp rail, leave 5-23dp margins.
        listOf(850f, 886f, 930f - 80f, 966f - 80f).forEach {
            assertFalse(drawsSheetMargins(sheetInset(it, 840f), min), "width $it")
        }
    }

    @Test
    fun `no margins at or below the column width`() {
        val min = MinSheetMargin.value
        assertFalse(drawsSheetMargins(sheetInset(840f, 840f), min))
        assertFalse(drawsSheetMargins(sheetInset(800f, 840f), min))
    }

    @Test
    fun `margins at least the minimum are drawn`() {
        val min = MinSheetMargin.value
        assertTrue(drawsSheetMargins(sheetInset(888f, 840f), min))
        assertTrue(drawsSheetMargins(sheetInset(1024f - 80f, 840f), min))
        assertTrue(drawsSheetMargins(sheetInset(1280f, 840f), min))
    }

    @Test
    fun `centred messages sit on the plain page colour in both themes`() {
        val light = lightColorScheme()
        val dark = darkColorScheme()
        assertEquals(light.background, light.messageBackground)
        assertEquals(dark.background, dark.messageBackground)
    }

    @Test
    fun `the message colour is one of the two wide-window page colours`() {
        val light = lightColorScheme()
        val dark = darkColorScheme()
        assertEquals(light.pageSheet, light.messageBackground)
        assertEquals(dark.pageBackdrop, dark.messageBackground)
    }
}
