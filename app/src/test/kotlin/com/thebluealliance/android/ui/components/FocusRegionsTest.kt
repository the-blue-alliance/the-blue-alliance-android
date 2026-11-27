package com.thebluealliance.android.ui.components

import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FocusRegionsTest {
    @Test
    fun `the downward entry from touch mode is the first keyboard focus`() {
        assertTrue(isFirstKeyboardFocus(FocusDirection.Down))
    }

    @Test
    fun `Tab wrapping around and Shift+Tab keep placement order`() {
        listOf(FocusDirection.Next, FocusDirection.Previous, FocusDirection.Enter)
            .forEach { assertFalse(isFirstKeyboardFocus(it), "$it") }
    }

    @Test
    fun `Tab and arrows are focus keys, others are not`() {
        listOf(Key.Tab, Key.DirectionUp, Key.DirectionDown, Key.DirectionLeft, Key.DirectionRight)
            .forEach { assertTrue(isFocusKey(it), "$it") }
        assertFalse(isFocusKey(Key.Enter))
        assertFalse(isFocusKey(Key.A))
    }

    @Test
    fun `only the key pressed before the entry is swallowed`() {
        assertTrue(causedEntry(keyTime = 1_000, enteredAt = 1_005))
        assertTrue(causedEntry(keyTime = 1_005, enteredAt = 1_005))
        assertFalse(causedEntry(keyTime = 1_200, enteredAt = 1_005))
        assertFalse(causedEntry(keyTime = 1_000, enteredAt = -1))
    }
}
