package com.thebluealliance.android.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.thebluealliance.android.core.theme.TbaBrandColors
import com.thebluealliance.android.core.theme.TbaPaletteTokens
import com.thebluealliance.android.ui.components.pageBackdrop
import com.thebluealliance.android.ui.components.pageSheet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RailChromeTest {
    private val schemes = mapOf("light" to TbaLightColorScheme, "dark" to TbaDarkColorScheme)

    private fun assertContrast(
        min: Float,
        name: String,
        foreground: Color,
        background: Color,
    ) {
        val fg = foreground.luminance()
        val bg = background.luminance()
        val ratio = (maxOf(fg, bg) + 0.05f) / (minOf(fg, bg) + 0.05f)
        assertTrue(ratio >= min, "$name is $ratio:1")
    }

    @Test
    fun `the status strip is Neutral10 with white icons above 7 to 1`() {
        assertEquals(TbaPaletteTokens.Neutral10, StatusStripGray)
        assertContrast(7f, "status icons on strip", Color.White, StatusStripGray)
    }

    @Test
    fun `rail items meet AA in both modes`() {
        schemes.forEach { (mode, scheme) ->
            val rail = scheme.surfaceContainerHighest
            assertContrast(4.5f, "$mode selected rail label", scheme.primary, rail)
            assertContrast(4.5f, "$mode unselected rail label", scheme.onSurfaceVariant, rail)
            assertContrast(3f, "$mode selected rail icon", OnBrand, scheme.primaryContainer)
        }
    }

    @Test
    fun `the rail lamp meets non-text contrast`() {
        assertContrast(3f, "lamp on circle", TbaBrandColors.OnBlue, TbaBrandColors.Blue)
    }

    @Test
    fun `the sheet is the lighter tone in both modes`() {
        assertEquals(TbaLightColorScheme.background, TbaLightColorScheme.pageSheet)
        assertEquals(TbaLightColorScheme.surfaceContainer, TbaLightColorScheme.pageBackdrop)
        assertEquals(TbaDarkColorScheme.surfaceContainer, TbaDarkColorScheme.pageSheet)
        assertEquals(TbaDarkColorScheme.background, TbaDarkColorScheme.pageBackdrop)
        schemes.values.forEach {
            assertTrue(it.pageSheet.luminance() > it.pageBackdrop.luminance())
        }
    }
}
