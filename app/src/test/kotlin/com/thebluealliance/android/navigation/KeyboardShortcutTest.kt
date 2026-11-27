package com.thebluealliance.android.navigation

import android.view.KeyEvent
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class KeyboardShortcutTest {
    private val leftCtrl = KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
    private val rightCtrl = KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_RIGHT_ON
    private val leftAlt = KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON

    @Test
    fun `ctrl F is search`() {
        assertEquals(ShortcutAction.Search, KeyboardShortcut.match(KeyEvent.KEYCODE_F, leftCtrl))
        assertEquals(ShortcutAction.Search, KeyboardShortcut.match(KeyEvent.KEYCODE_F, rightCtrl))
    }

    @Test
    fun `lock keys do not block a match`() {
        val locks = KeyEvent.META_CAPS_LOCK_ON or KeyEvent.META_NUM_LOCK_ON
        assertEquals(
            ShortcutAction.Search,
            KeyboardShortcut.match(
                KeyEvent.KEYCODE_F,
                leftCtrl or locks,
            ),
        )
        assertEquals(ShortcutAction.Refresh, KeyboardShortcut.match(KeyEvent.KEYCODE_F5, locks))
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
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_F5, KeyEvent.META_SHIFT_ON))
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_R, leftCtrl or KeyEvent.META_SHIFT_ON))
    }

    @Test
    fun `other ctrl keys are left to the system`() {
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_C, leftCtrl))
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_CTRL_LEFT, leftCtrl))
    }

    @Test
    fun `F5 and ctrl R refresh`() {
        assertEquals(ShortcutAction.Refresh, KeyboardShortcut.match(KeyEvent.KEYCODE_F5, 0))
        assertEquals(ShortcutAction.Refresh, KeyboardShortcut.match(KeyEvent.KEYCODE_R, leftCtrl))
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_R, 0))
    }

    @Test
    fun `alt left is back`() {
        assertEquals(
            ShortcutAction.Back,
            KeyboardShortcut.match(KeyEvent.KEYCODE_DPAD_LEFT, leftAlt),
        )
        assertEquals(
            ShortcutAction.Back,
            KeyboardShortcut.match(
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.META_ALT_ON or KeyEvent.META_ALT_RIGHT_ON,
            ),
        )
    }

    @Test
    fun `plain and ctrl arrows stay with focus and text`() {
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_DPAD_LEFT, 0))
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_DPAD_LEFT, leftCtrl))
        assertNull(
            KeyboardShortcut.match(
                KeyEvent.KEYCODE_DPAD_LEFT,
                leftAlt or KeyEvent.META_SHIFT_ON,
            ),
        )
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_DPAD_RIGHT, leftAlt))
    }

    @Test
    fun `ctrl digits open top-level destinations in bar order`() {
        val digits =
            listOf(KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4)
        digits.forEachIndexed { index, key ->
            assertEquals(ShortcutAction.SwitchTab(index), KeyboardShortcut.match(key, leftCtrl))
        }
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_1, 0))
    }

    @Test
    fun `ctrl digits past the last destination do nothing`() {
        assertNull(KeyboardShortcut.match(KeyEvent.KEYCODE_5, leftCtrl))
    }

    @Test
    fun `tab shortcuts are labelled with their destination`() {
        val tabLabels =
            KeyboardShortcut.navigation
                .filter { it.action is ShortcutAction.SwitchTab }
                .map { it.label }
        assertEquals(TOP_LEVEL_DESTINATIONS.map { it.label }, tabLabels)
        assertEquals(listOf("Events", "Teams", "Districts", "More"), tabLabels)
    }

    @Test
    fun `every chord is unique`() {
        val chords = KeyboardShortcut.all.map { it.keyCode to it.modifiers }
        assertEquals(chords.size, chords.toSet().size)
    }
}
