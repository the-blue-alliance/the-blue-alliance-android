package com.thebluealliance.android.ui.theme

import androidx.compose.material3.RippleThemeConfiguration
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class FocusRingThemeTest {
    @Test
    fun `keyboard focus draws Material's inset focus ring, not the opacity layer`() {
        assertInstanceOf(
            RippleThemeConfiguration.Focus.InsetRing::class.java,
            TbaRippleThemeConfiguration.focus,
        )
    }
}
