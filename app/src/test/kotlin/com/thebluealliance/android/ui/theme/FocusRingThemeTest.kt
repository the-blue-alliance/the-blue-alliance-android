package com.thebluealliance.android.ui.theme

import androidx.compose.material3.RippleThemeConfiguration
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FocusRingThemeTest {
    @Test
    fun `keyboard focus draws Material's inset focus ring`() {
        assertEquals(
            RippleThemeConfiguration.Focus.InsetRing(
                outerStrokeInset = 0.dp,
                outerStrokeWidth = 2.dp,
                innerStrokeInset = 1.dp,
                innerStrokeWidth = 3.dp,
            ),
            TbaRippleThemeConfiguration.focus,
        )
    }
}
