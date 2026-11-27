package com.thebluealliance.android.ui.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TouchPullGateTest {
    private val gate = TouchPullGate()

    private fun overscroll(available: Offset) =
        gate.onPostScroll(Offset.Zero, available, NestedScrollSource.UserInput)

    @Test
    fun `fingers and styluses pull`() {
        assertTrue(isPullPointer(listOf(PointerType.Touch)))
        assertTrue(isPullPointer(listOf(PointerType.Stylus)))
        assertTrue(isPullPointer(listOf(PointerType.Touch, PointerType.Touch)))
    }

    @Test
    fun `mice do not pull`() {
        assertFalse(isPullPointer(listOf(PointerType.Mouse)))
        assertFalse(isPullPointer(listOf(PointerType.Touch, PointerType.Mouse)))
        assertFalse(isPullPointer(emptyList()))
    }

    @Test
    fun `touch overscroll reaches pull-to-refresh`() {
        gate.observe(pulls = true)
        assertEquals(Offset.Zero, overscroll(Offset(0f, 40f)))
    }

    @Test
    fun `wheel overscroll past the top is held back`() {
        gate.observe(pulls = false)
        assertEquals(Offset(0f, 40f), overscroll(Offset(0f, 40f)))
    }

    @Test
    fun `wheel overscroll past the bottom is left alone`() {
        gate.observe(pulls = false)
        assertEquals(Offset.Zero, overscroll(Offset(0f, -40f)))
    }

    @Test
    fun `a touch after the wheel pulls again`() {
        gate.observe(pulls = false)
        gate.observe(pulls = true)
        assertEquals(Offset.Zero, overscroll(Offset(0f, 40f)))
    }

    @Test
    fun `a mouse entering or leaving doesn't change the last input`() {
        assertFalse(changesLastInput(PointerEventType.Enter))
        assertFalse(changesLastInput(PointerEventType.Exit))
        assertTrue(changesLastInput(PointerEventType.Move))
        assertTrue(changesLastInput(PointerEventType.Press))
        assertTrue(changesLastInput(PointerEventType.Scroll))
    }
}
