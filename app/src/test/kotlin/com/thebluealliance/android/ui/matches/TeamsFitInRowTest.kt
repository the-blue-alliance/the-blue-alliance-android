package com.thebluealliance.android.ui.matches

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TeamsFitInRowTest {
    // Target widths at bodyLarge plus 12dp side padding, in dp at density 1.
    private val fourDigits = listOf(62, 62, 62)
    private val fiveDigits = listOf(71, 71, 71)
    private val spacing = 0

    @Test
    fun `three teams fit in a wide alliance column`() {
        assertTrue(teamsFitInRow(fiveDigits, spacing, availableWidth = 359))
    }

    @Test
    fun `three teams stack in a phone alliance column`() {
        assertFalse(teamsFitInRow(fourDigits, spacing, availableWidth = 139))
    }

    @Test
    fun `five digit teams need more room than four digit teams`() {
        assertTrue(teamsFitInRow(fourDigits, spacing, availableWidth = 200))
        assertFalse(teamsFitInRow(fiveDigits, spacing, availableWidth = 200))
    }

    @Test
    fun `a row exactly as wide as the column fits`() {
        assertTrue(teamsFitInRow(fiveDigits, spacing, availableWidth = 213))
        assertFalse(teamsFitInRow(fiveDigits, spacing, availableWidth = 212))
    }

    @Test
    fun `spacing between targets counts against the column`() {
        assertTrue(teamsFitInRow(fourDigits, spacing = 16, availableWidth = 218))
        assertFalse(teamsFitInRow(fourDigits, spacing = 16, availableWidth = 217))
    }

    @Test
    fun `a lone team needs no spacing`() {
        assertTrue(teamsFitInRow(listOf(71), spacing = 16, availableWidth = 71))
    }
}
