package com.thebluealliance.android.ui.components

import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.LayoutDirection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StickyHeaderFocusTest {
    @Test
    fun `tab into the list stops at the header first`() {
        assertTrue(headerTakesFocus(FocusDirection.Next, fromRows = false))
    }

    @Test
    fun `tab from a row skips the header`() {
        assertFalse(headerTakesFocus(FocusDirection.Next, fromRows = true))
    }

    @Test
    fun `shift tab from the first row goes to the header`() {
        assertTrue(headerTakesFocus(FocusDirection.Previous, fromRows = true))
    }

    @Test
    fun `shift tab into the list skips the header for the last row`() {
        assertFalse(headerTakesFocus(FocusDirection.Previous, fromRows = false))
    }

    @Test
    fun `arrow keys reach the header as before`() {
        for (direction in listOf(FocusDirection.Up, FocusDirection.Down, FocusDirection.Enter)) {
            assertTrue(headerTakesFocus(direction, fromRows = true))
            assertTrue(headerTakesFocus(direction, fromRows = false))
        }
    }

    @Test
    fun `shift tab enters the header at its end side`() {
        assertEquals(FocusDirection.Left, headerEndEntry(LayoutDirection.Ltr))
        assertEquals(FocusDirection.Right, headerEndEntry(LayoutDirection.Rtl))
    }

    @Test
    fun `a list without a composed header leaves focus to the rows`() {
        val order = StickyHeaderFocusOrder(NoFocusManager, LayoutDirection.Ltr)
        assertFalse(order.focusHeaderStart())
        assertFalse(order.focusHeaderEnd())
    }

    private object NoFocusManager : FocusManager {
        override fun clearFocus(force: Boolean) = Unit

        override fun moveFocus(focusDirection: FocusDirection): Boolean = false
    }
}
