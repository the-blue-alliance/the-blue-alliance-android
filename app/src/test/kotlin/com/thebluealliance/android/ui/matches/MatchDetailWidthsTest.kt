package com.thebluealliance.android.ui.matches

import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatchDetailWidthsTest {
    private val expandedWindow = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(1024f, 640f)

    @Test
    fun `full screen goes by the window`() {
        assertEquals(
            MatchDetailWidths(wide = true, expanded = true),
            matchDetailWidths(expandedWindow, null),
        )
        assertEquals(
            MatchDetailWidths(wide = false, expanded = false),
            matchDetailWidths(
                WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(360f, 640f),
                null,
            ),
        )
    }

    @Test
    fun `a pane goes by its own width`() {
        assertEquals(
            MatchDetailWidths(wide = false, expanded = false),
            matchDetailWidths(expandedWindow, 566.dp),
        )
        assertEquals(
            MatchDetailWidths(wide = true, expanded = false),
            matchDetailWidths(expandedWindow, 800.dp),
        )
    }
}
