package com.thebluealliance.android.ui.components

import android.view.KeyEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EscapeHandlerTest {
    @Test
    fun `Esc acts on release`() {
        assertEquals(EscapeKeyStep.Act, escapeKeyStep(KeyEvent.KEYCODE_ESCAPE, KeyEvent.ACTION_UP))
    }

    @Test
    fun `Esc press is claimed without acting`() {
        assertEquals(
            EscapeKeyStep.Claim,
            escapeKeyStep(KeyEvent.KEYCODE_ESCAPE, KeyEvent.ACTION_DOWN),
        )
    }

    @Test
    fun `other keys pass through`() {
        assertEquals(EscapeKeyStep.Ignore, escapeKeyStep(KeyEvent.KEYCODE_BACK, KeyEvent.ACTION_UP))
        assertEquals(
            EscapeKeyStep.Ignore,
            escapeKeyStep(KeyEvent.KEYCODE_ENTER, KeyEvent.ACTION_DOWN),
        )
    }
}
