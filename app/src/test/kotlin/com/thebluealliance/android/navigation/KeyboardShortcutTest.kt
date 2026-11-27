package com.thebluealliance.android.navigation

import android.view.KeyEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class KeyboardShortcutTest {
    private val leftCtrl = KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
    private val rightCtrl = KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_RIGHT_ON

    @Test
    fun `ctrl F is search`() {
        assertEquals(KeyboardShortcut.Search, KeyboardShortcut.match(KeyEvent.KEYCODE_F, leftCtrl))
        assertEquals(KeyboardShortcut.Search, KeyboardShortcut.match(KeyEvent.KEYCODE_F, rightCtrl))
    }

    @Test
    fun `lock keys do not block a match`() {
        val locks = KeyEvent.META_CAPS_LOCK_ON or KeyEvent.META_NUM_LOCK_ON
        assertEquals(
            KeyboardShortcut.Search,
            KeyboardShortcut.match(
                KeyEvent.KEYCODE_F,
                leftCtrl or locks,
            ),
        )
    }

    @Test
    fun `F without ctrl is not a shortcut`() {
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_F, 0))
        assertNull(
            KeyboardShortcut.match(
                KeyEvent.KEYCODE_F,
                KeyEvent.META_META_ON or KeyEvent.META_META_LEFT_ON,
            ),
        )
    }

    @Test
    fun `extra modifiers are a different chord`() {
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_F, leftCtrl or KeyEvent.META_SHIFT_ON))
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_F, leftCtrl or KeyEvent.META_ALT_ON))
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_F, leftCtrl or KeyEvent.META_META_ON))
    }

    @Test
    fun `other ctrl keys are left to the system`() {
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_C, leftCtrl))
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_CTRL_LEFT, leftCtrl))
    }
}
