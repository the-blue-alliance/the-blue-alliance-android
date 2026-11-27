package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FastScrollbarTest {
    // A 1080px list with a 126px strip and a 126px thumb at 1500..1626.
    private fun claims(
        x: Float,
        y: Float,
        thumbShown: Boolean = true,
        ltr: Boolean = true,
    ) = fastScrollbarClaimsPress(
        x = x,
        y = y,
        width = 1080f,
        stripWidth = 126f,
        ltr = ltr,
        thumbTop = 1500f,
        thumbHeight = 126f,
        thumbShown = thumbShown,
    )

    @Test
    fun `tap at the end of a row while the scrollbar is hidden reaches the row`() {
        assertFalse(claims(x = 1030f, y = 860f, thumbShown = false))
    }

    @Test
    fun `tap at the end of a row away from a shown thumb reaches the row`() {
        assertFalse(claims(x = 1030f, y = 860f))
    }

    @Test
    fun `tap where a hidden thumb would be reaches the row`() {
        assertFalse(claims(x = 1030f, y = 1550f, thumbShown = false))
    }

    @Test
    fun `tap at the start of a right-to-left row away from the thumb reaches the row`() {
        assertFalse(claims(x = 50f, y = 860f, ltr = false))
    }

    @Test
    fun `press on a shown thumb grabs the scrollbar`() {
        assertTrue(claims(x = 1030f, y = 1550f))
    }

    @Test
    fun `press on a shown right-to-left thumb grabs the scrollbar`() {
        assertTrue(claims(x = 50f, y = 1550f, ltr = false))
    }

    @Test
    fun `tap mid-row never grabs the scrollbar`() {
        assertFalse(claims(x = 500f, y = 1550f))
    }
}
