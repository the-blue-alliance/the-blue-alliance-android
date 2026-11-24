package com.thebluealliance.android.core.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TbaBrandColorsContrastTest {
    private fun contrast(
        foreground: Color,
        background: Color,
    ): Float {
        val fg = foreground.compositeOver(background).luminance()
        val bg = background.luminance()
        return (maxOf(fg, bg) + 0.05f) / (minOf(fg, bg) + 0.05f)
    }

    @Test
    fun `OnBlue meets AA on Blue and HeaderBlue`() {
        assertTrue(contrast(TbaBrandColors.OnBlue, TbaBrandColors.Blue) >= 4.5f)
        assertTrue(contrast(TbaBrandColors.OnBlue, TbaBrandColors.HeaderBlue) >= 4.5f)
    }

    @Test
    fun `OnBlueMuted meets AA text contrast on Blue`() {
        val ratio = contrast(TbaBrandColors.OnBlueMuted, TbaBrandColors.Blue)
        assertTrue(ratio >= 4.5f, "OnBlueMuted on Blue is $ratio:1")
    }

    @Test
    fun `OnBlueOutline meets non-text contrast on Blue`() {
        val ratio = contrast(TbaBrandColors.OnBlueOutline, TbaBrandColors.Blue)
        assertTrue(ratio >= 3f, "OnBlueOutline on Blue is $ratio:1")
    }
}
