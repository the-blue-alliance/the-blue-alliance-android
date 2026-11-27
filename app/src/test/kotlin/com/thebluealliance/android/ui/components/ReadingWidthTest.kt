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
    fun `bands reach the window edges while margins are hidden`() {
        assertEquals(10f, bandBleed(860f, 840f, 24f))
        assertEquals(23f, bandBleed(886f, 840f, 24f))
    }

    @Test
    fun `bands end at the column beside drawn margins and fill narrow windows unaided`() {
        assertEquals(0f, bandBleed(888f, 840f, 24f))
        assertEquals(0f, bandBleed(1280f, 840f, 24f))
        assertEquals(0f, bandBleed(840f, 840f, 24f))
        assertEquals(0f, bandBleed(412f, 840f, 24f))
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

    @Test
    fun `controls fill every column narrower than the full reading column`() {
        val full = ReadingMaxWidth - 32.dp
        listOf(328.dp, 379.dp, 588.dp, 728.dp, full - 1.dp).forEach {
            assertEquals(it, controlWidth(it, full))
        }
    }

    @Test
    fun `controls cap once the reading column is full`() {
        assertEquals(
            ControlMaxWidth,
            controlWidth(ReadingMaxWidth - 32.dp, ReadingMaxWidth - 32.dp),
        )
    }

    @Test
    fun `a field in a phone bar keeps its slot`() {
        // 411dp phone at density 1: slot starts after the 56dp back button.
        val fit = readingColumnFit(56f, 0f, 411f, 840f, rtl = false)
        assertEquals(0f, fit.start)
        assertTrue(fit.maxWidth >= 411f - 56f)
    }

    @Test
    fun `a field beside the rail below the reading width keeps its slot`() {
        // 900dp window, 80dp rail: an 820dp content area, narrower than the column.
        val fit = readingColumnFit(136f, 80f, 900f, 840f, rtl = false)
        assertEquals(0f, fit.start)
        assertEquals(900f - 136f, fit.maxWidth)
    }

    @Test
    fun `a field on a 1280dp tablet shares the column's edges`() {
        // 1200dp content area beside the rail; the column runs from 260 to 1100.
        val fit = readingColumnFit(136f, 80f, 1280f, 840f, rtl = false)
        assertEquals(260f - 136f, fit.start)
        assertEquals(840f, fit.maxWidth)
    }

    @Test
    fun `a field just past the reading width ends at the column's end`() {
        // 920dp content area: the column runs from 120 to 1000, left of the field's slot.
        val fit = readingColumnFit(136f, 80f, 1000f, 840f, rtl = false)
        assertEquals(0f, fit.start)
        assertEquals(960f - 136f, fit.maxWidth)
    }

    @Test
    fun `a field in a right-to-left bar shares the column's edges`() {
        // Mirror of the 1280dp tablet: rail on the right, slot starting at 1144.
        val fit = readingColumnFit(1144f, 0f, 1200f, 840f, rtl = true)
        assertEquals(1144f - 1020f, fit.start)
        assertEquals(840f, fit.maxWidth)
    }
}
