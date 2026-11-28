package com.thebluealliance.android.ui.components

import androidx.compose.ui.input.InputMode
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SelectableMenuItemTest {
    @Test
    fun `current entry takes focus when the menu opens from the keyboard`() {
        assertTrue(focusesOnOpen(selected = true, inputMode = InputMode.Keyboard))
    }

    @Test
    fun `current entry leaves focus alone when the menu opens by touch`() {
        assertFalse(focusesOnOpen(selected = true, inputMode = InputMode.Touch))
    }

    @Test
    fun `other entries never take focus on open`() {
        assertFalse(focusesOnOpen(selected = false, inputMode = InputMode.Keyboard))
    }
}
