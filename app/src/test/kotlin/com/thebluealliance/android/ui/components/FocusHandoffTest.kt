package com.thebluealliance.android.ui.components

import androidx.compose.ui.input.InputMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class FocusHandoffTest {
    @Test
    fun `a keyboard press arms the hand-off`() {
        val handoff = FocusHandoff()
        handoff.arm(2025, InputMode.Keyboard)
        assertEquals(2025, handoff.armedFor)
        assertFalse(handoff.reached)
    }

    @Test
    fun `a touch press leaves it unarmed`() {
        val handoff = FocusHandoff()
        handoff.arm(2025, InputMode.Keyboard)
        handoff.arm(2024, InputMode.Touch)
        assertNull(handoff.armedFor)
    }

    @Test
    fun `unarmed never moves focus`() {
        assertEquals(FocusHandoffStep.WAIT, step(armedFor = null, hasContent = true))
    }

    @Test
    fun `waits while the selection hasn't reached the screen`() {
        assertEquals(FocusHandoffStep.WAIT, step(current = 2026, hasContent = true))
    }

    @Test
    fun `focuses the new content once it is shown`() {
        assertEquals(FocusHandoffStep.FOCUS_CONTENT, step(hasContent = true))
    }

    @Test
    fun `waits while the new selection loads`() {
        assertEquals(FocusHandoffStep.WAIT, step())
    }

    @Test
    fun `keeps focus on the relabeled control when the selection is empty too`() {
        assertEquals(FocusHandoffStep.FOCUS_CONTROL, step(controlShown = true))
    }

    @Test
    fun `stops once the screen moves to another selection`() {
        assertEquals(
            FocusHandoffStep.DISARM,
            step(reached = true, current = 2023, hasContent = true),
        )
    }

    private fun step(
        armedFor: Any? = 2025,
        reached: Boolean = false,
        current: Any = 2025,
        hasContent: Boolean = false,
        controlShown: Boolean = false,
    ) = focusHandoffStep(armedFor, reached, current, hasContent, controlShown)
}
