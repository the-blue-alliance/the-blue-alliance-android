package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FastScrollbarTest {
    // A 1080px list with a 126px strip.
    private fun onStrip(
        x: Float,
        ltr: Boolean = true,
    ) = onFastScrollbarStrip(x = x, width = 1080f, stripWidth = 126f, ltr = ltr)

    @Test
    fun `press at the end of a row is on the strip`() {
        assertTrue(onStrip(x = 1030f))
    }

    @Test
    fun `press at the start of a right-to-left row is on the strip`() {
        assertTrue(onStrip(x = 50f, ltr = false))
    }

    @Test
    fun `press mid-row is off the strip`() {
        assertFalse(onStrip(x = 500f))
    }

    @Test
    fun `press at the end of a right-to-left row is off the strip`() {
        assertFalse(onStrip(x = 1030f, ltr = false))
    }

    @Test
    fun `tap on the strip stays with the row`() {
        assertEquals(
            StripDrag.UNDECIDED,
            fastScrollbarClaimsDrag(dx = 0f, dy = 0f, touchSlop = 21f),
        )
    }

    @Test
    fun `vertical drag past the touch slop becomes a fast scroll`() {
        assertEquals(
            StripDrag.SCROLLBAR,
            fastScrollbarClaimsDrag(dx = 2f, dy = 30f, touchSlop = 21f),
        )
    }

    @Test
    fun `small wobble during a tap stays with the row`() {
        assertEquals(
            StripDrag.UNDECIDED,
            fastScrollbarClaimsDrag(dx = 3f, dy = 10f, touchSlop = 21f),
        )
    }

    @Test
    fun `sideways drag stays with the row`() {
        assertEquals(StripDrag.ROW, fastScrollbarClaimsDrag(dx = 60f, dy = 30f, touchSlop = 21f))
    }

    @Test
    fun `diagonal drag past the slop is left to the list for the whole gesture`() {
        assertEquals(StripDrag.ROW, fastScrollbarClaimsDrag(dx = 20f, dy = 20f, touchSlop = 21f))
    }

    @Test
    fun `move past the slop on neither axis alone is still decided`() {
        assertEquals(StripDrag.ROW, fastScrollbarClaimsDrag(dx = 16f, dy = 15f, touchSlop = 21f))
    }
}
