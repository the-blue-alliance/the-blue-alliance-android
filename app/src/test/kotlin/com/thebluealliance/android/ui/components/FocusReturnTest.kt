package com.thebluealliance.android.ui.components

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.InputMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FocusReturnTest {
    private val focusReturn = FocusReturn()
    private val list = focusReturn.entry("Events")
    private val detail = focusReturn.entry("EventDetail")

    @Test
    fun `the focused launcher gets focus back on return`() {
        list.focusChanged(ROW, focused = true)
        focusReturn.onNavigate()
        assertEquals(ROW, list.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `a return is taken once`() {
        list.focusChanged(ROW, focused = true)
        focusReturn.onNavigate()
        list.takeReturn(InputMode.Keyboard)
        assertNull(list.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `touch mode drops the return`() {
        list.focusChanged(ROW, focused = true)
        focusReturn.onNavigate()
        assertNull(list.takeReturn(InputMode.Touch))
        assertNull(list.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `navigating with focus off every launcher saves nothing`() {
        list.focusChanged(ROW, focused = true)
        list.focusChanged(ROW, focused = false)
        focusReturn.onNavigate()
        assertNull(list.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `focus moving to another launcher returns there`() {
        list.focusChanged(ROW, focused = true)
        list.focusChanged(OTHER_ROW, focused = true)
        list.focusChanged(ROW, focused = false)
        focusReturn.onNavigate()
        assertEquals(OTHER_ROW, list.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `the return goes to the entry that held focus`() {
        list.focusChanged(ROW, focused = true)
        focusReturn.onNavigate()
        assertNull(detail.takeReturn(InputMode.Keyboard))
        assertEquals(ROW, list.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `focus back in the entry spends an older return`() {
        // A pane beside the list closes and focus goes back to the list by other means.
        list.focusChanged(ROW, focused = true)
        focusReturn.onNavigate()
        list.focusChanged(ROW, focused = false)
        list.focusChanged(OTHER_ROW, focused = true)
        assertNull(list.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `a popped entry is forgotten`() {
        list.focusChanged(ROW, focused = true)
        focusReturn.forget("Events")
        focusReturn.onNavigate()
        assertNull(focusReturn.entry("Events").takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `a pending return survives recreation`() {
        list.focusChanged(ROW, focused = true)
        focusReturn.onNavigate()
        val saved = list.savedReturn()
        val recreated = FocusReturn().entry("Events")
        recreated.restoreReturn(saved!!)
        assertEquals(ROW, recreated.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `a spent return is not saved`() {
        list.focusChanged(ROW, focused = true)
        focusReturn.onNavigate()
        list.takeReturn(InputMode.Keyboard)
        assertNull(list.savedReturn())
    }

    @Test
    fun `restoring keeps a newer return`() {
        list.focusChanged(OTHER_ROW, focused = true)
        focusReturn.onNavigate()
        list.restoreReturn(ROW)
        assertEquals(OTHER_ROW, list.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `a composed launcher holds its key`() {
        val requester = FocusRequester()
        list.requesters[ROW] = requester
        assertTrue(list.holds(ROW, requester))
    }

    @Test
    fun `a launcher removed before focus moves holds nothing`() {
        val requester = FocusRequester()
        list.requesters[ROW] = requester
        list.requesters.remove(ROW)
        assertFalse(list.holds(ROW, requester))
    }

    @Test
    fun `a recomposed launcher replaces the old requester`() {
        val old = FocusRequester()
        list.requesters[ROW] = old
        list.requesters[ROW] = FocusRequester()
        assertFalse(list.holds(ROW, old))
    }

    @Test
    fun `a menu item hands its return to the button that opened the menu`() {
        list.standIns[MENU_ITEM] = MORE_BUTTON
        list.focusChanged(MENU_ITEM, focused = true)
        focusReturn.onNavigate()
        assertEquals(MORE_BUTTON, list.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `moving between menu items keeps the return on the button`() {
        list.standIns[MENU_ITEM] = MORE_BUTTON
        list.standIns[OTHER_MENU_ITEM] = MORE_BUTTON
        list.focusChanged(OTHER_MENU_ITEM, focused = true)
        list.focusChanged(MENU_ITEM, focused = true)
        list.focusChanged(OTHER_MENU_ITEM, focused = false)
        focusReturn.onNavigate()
        assertEquals(MORE_BUTTON, list.takeReturn(InputMode.Keyboard))
    }

    @Test
    fun `focus returns only in keyboard mode`() {
        assertTrue(returnsFocusIn(InputMode.Keyboard))
        assertFalse(returnsFocusIn(InputMode.Touch))
    }

    private companion object {
        const val ROW = 1L
        const val OTHER_ROW = 2L
        const val MORE_BUTTON = 3L
        const val MENU_ITEM = 4L
        const val OTHER_MENU_ITEM = 5L
    }
}
