package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MediaTileMinSizeTest {
    private fun sizeClass(widthDp: Float) =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp, 800f)

    @Test
    fun `compact widths keep phone-sized tiles`() {
        assertEquals(160.dp, mediaTileMinSize(sizeClass(360f)))
        assertEquals(160.dp, mediaTileMinSize(sizeClass(599f)))
    }

    @Test
    fun `medium and wider windows use larger tiles`() {
        assertEquals(240.dp, mediaTileMinSize(sizeClass(600f)))
        assertEquals(240.dp, mediaTileMinSize(sizeClass(840f)))
        assertEquals(240.dp, mediaTileMinSize(sizeClass(1280f)))
    }
}
