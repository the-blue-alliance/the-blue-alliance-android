package com.thebluealliance.android.ui.components

import android.view.MotionEvent
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerEventType
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SecondaryClickTest {
    @Test
    fun `right button press opens the menu`() {
        assertTrue(
            isSecondaryPress(PointerEventType.Press, PointerButtons(MotionEvent.BUTTON_SECONDARY)),
        )
    }

    @Test
    fun `left button press does not`() {
        assertFalse(
            isSecondaryPress(PointerEventType.Press, PointerButtons(MotionEvent.BUTTON_PRIMARY)),
        )
    }

    @Test
    fun `touch press has no buttons and does not`() {
        assertFalse(isSecondaryPress(PointerEventType.Press, PointerButtons(0)))
    }

    @Test
    fun `right button held while moving does not reopen it`() {
        assertFalse(
            isSecondaryPress(PointerEventType.Move, PointerButtons(MotionEvent.BUTTON_SECONDARY)),
        )
    }

    @Test
    fun `right button release does not`() {
        assertFalse(isSecondaryPress(PointerEventType.Release, PointerButtons(0)))
    }
}
