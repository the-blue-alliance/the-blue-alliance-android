package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BottomInsetPaddingTest {
    private val padding = PaddingValues(start = 1.dp, top = 2.dp, end = 3.dp, bottom = 4.dp)

    @Test
    fun `withoutBottom keeps the sides and top`() {
        val result = padding.withoutBottom()
        assertEquals(1.dp, result.calculateLeftPadding(LayoutDirection.Ltr))
        assertEquals(3.dp, result.calculateRightPadding(LayoutDirection.Ltr))
        assertEquals(3.dp, result.calculateLeftPadding(LayoutDirection.Rtl))
        assertEquals(1.dp, result.calculateRightPadding(LayoutDirection.Rtl))
        assertEquals(2.dp, result.calculateTopPadding())
        assertEquals(0.dp, result.calculateBottomPadding())
    }

    @Test
    fun `onlyBottom keeps just the bottom`() {
        assertEquals(PaddingValues(bottom = 4.dp), padding.onlyBottom())
    }

    @Test
    fun `the halves add back up to the whole`() {
        val top = padding.withoutBottom()
        val bottom = padding.onlyBottom()
        LayoutDirection.entries.forEach { dir ->
            assertEquals(
                padding.calculateLeftPadding(dir),
                top.calculateLeftPadding(dir) + bottom.calculateLeftPadding(dir),
            )
            assertEquals(
                padding.calculateRightPadding(dir),
                top.calculateRightPadding(dir) + bottom.calculateRightPadding(dir),
            )
        }
        assertEquals(
            padding.calculateBottomPadding(),
            top.calculateBottomPadding() + bottom.calculateBottomPadding(),
        )
    }
}
