package com.thebluealliance.android.ui.components

import androidx.compose.ui.geometry.Offset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class SidewaysWheelDeltaTest {
    private fun delta(
        scroll: Offset,
        forward: Boolean = true,
        backward: Boolean = true,
    ) = sidewaysWheelDelta(
        scroll,
        pixelsPerStep = 64f,
        canScrollForward = forward,
        canScrollBackward = backward,
    )

    @Test
    fun `wheel down scrolls toward the end`() {
        assertEquals(64f, delta(Offset(0f, 1f)))
        assertEquals(128f, delta(Offset(0f, 2f)))
    }

    @Test
    fun `wheel up scrolls toward the start`() {
        assertEquals(-64f, delta(Offset(0f, -1f)))
    }

    @Test
    fun `horizontal and diagonal wheels are left to the row`() {
        assertNull(delta(Offset(1f, 0f)))
        assertNull(delta(Offset(1f, 1f)))
        assertNull(delta(Offset.Zero))
    }

    @Test
    fun `nothing is taken past either end`() {
        assertNull(delta(Offset(0f, 1f), forward = false))
        assertNull(delta(Offset(0f, -1f), backward = false))
        assertEquals(64f, delta(Offset(0f, 1f), backward = false))
    }
}
