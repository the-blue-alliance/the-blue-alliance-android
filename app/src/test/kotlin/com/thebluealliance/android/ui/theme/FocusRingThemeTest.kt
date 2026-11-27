package com.thebluealliance.android.ui.theme

import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.RippleThemeConfiguration
import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FocusRingThemeTest {
    @Test
    fun `keyboard focus draws Material's inset focus ring, not the opacity layer`() {
        assertInstanceOf(
            RippleThemeConfiguration.Focus.InsetRing::class.java,
            TbaRippleThemeConfiguration.focus,
        )
    }

    @Test
    fun `focus ring depth is the deeper of the ring's two strokes`() {
        val ring = TbaRippleThemeConfiguration.focus as RippleThemeConfiguration.Focus.InsetRing
        assertEquals(
            maxOf(
                ring.outerStrokeInset + ring.outerStrokeWidth,
                ring.innerStrokeInset + ring.innerStrokeWidth,
            ),
            FocusRingDepth,
        )
        assertTrue(FocusRingDepth.value > 0f)
    }

    @Test
    fun `brand ring draws OnBrand outside and the surface color inside`() {
        val container = Color(0xFF3F51B5)
        val ring = brandFocusRing(container).focus as RippleConfiguration.Focus.InsetRing
        assertEquals(OnBrand, ring.outerStrokeColor)
        assertEquals(container, ring.innerStrokeColor)
    }
}
