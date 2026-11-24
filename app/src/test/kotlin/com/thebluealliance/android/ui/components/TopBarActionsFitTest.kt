package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TopBarActionsFitTest {
    // Edge padding and Back.
    private val detailFixed = 8.dp + 48.dp

    private fun collapses(
        barWidth: Dp,
        secondaryCount: Int = 2,
        hasOverflowMenu: Boolean = true,
    ) = topBarCollapsesActions(barWidth, detailFixed, secondaryCount, hasOverflowMenu)

    @Test
    fun `phones keep bell and star down to 320dp`() {
        assertFalse(collapses(320.dp))
        assertFalse(collapses(411.dp))
    }

    @Test
    fun `title exactly at the minimum keeps the icons`() {
        // 56 fixed + 3 * 48 actions + 120 title.
        assertFalse(collapses(320.dp))
        assertTrue(collapses(319.dp))
    }

    @Test
    fun `narrow split-screen windows collapse bell and star`() {
        assertTrue(collapses(280.dp))
    }

    @Test
    fun `collapsing leaves the title at least the minimum at 280dp`() {
        assertTrue(280.dp - detailFixed - TopBarActionWidth >= TopBarMinTitleWidth)
    }

    @Test
    fun `two actions without a menu collapse into a new menu`() {
        assertTrue(collapses(260.dp, hasOverflowMenu = false))
    }

    @Test
    fun `a lone action without a menu never collapses`() {
        assertFalse(collapses(200.dp, secondaryCount = 1, hasOverflowMenu = false))
    }

    @Test
    fun `nothing to collapse`() {
        assertFalse(collapses(100.dp, secondaryCount = 0))
    }
}
