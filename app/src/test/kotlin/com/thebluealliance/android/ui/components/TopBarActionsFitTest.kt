package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
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

    @Test
    fun `Close reserves its width`() {
        // Pane without Back: 8 fixed + Share + menu + Close + 120 title = 272.
        assertFalse(topBarCollapsesActions(272.dp, 8.dp, 1, true, pinnedCount = 1))
        assertTrue(topBarCollapsesActions(271.dp, 8.dp, 1, true, pinnedCount = 1))
    }

    @Test
    fun `Close alone never triggers a collapse`() {
        assertFalse(topBarCollapsesActions(100.dp, 8.dp, 1, false, pinnedCount = 1))
        assertFalse(topBarCollapsesActions(100.dp, 8.dp, 0, true, pinnedCount = 1))
    }

    @Test
    fun `Close follows secondary actions and More options`() {
        assertEquals(
            listOf(TopBarSlot.Secondary, TopBarSlot.Overflow, TopBarSlot.Close),
            topBarSlots(
                secondaryCount = 1,
                overflowCount = 2,
                hasClose = true,
                collapse = false,
            ),
        )
    }

    @Test
    fun `Close stays last when secondary actions collapse`() {
        assertEquals(
            listOf(TopBarSlot.Overflow, TopBarSlot.Close),
            topBarSlots(
                secondaryCount = 2,
                overflowCount = 0,
                hasClose = true,
                collapse = true,
            ),
        )
    }

    @Test
    fun `Close follows secondary actions without a menu`() {
        assertEquals(
            listOf(TopBarSlot.Secondary, TopBarSlot.Close),
            topBarSlots(
                secondaryCount = 1,
                overflowCount = 0,
                hasClose = true,
                collapse = false,
            ),
        )
    }

    @Test
    fun `no menu button with nothing to put in it`() {
        assertEquals(
            listOf(TopBarSlot.Secondary, TopBarSlot.Secondary),
            topBarSlots(
                secondaryCount = 2,
                overflowCount = 0,
                hasClose = false,
                collapse = false,
            ),
        )
    }

    @Test
    fun `no menu items, no menu button`() {
        assertEquals(
            listOf(TopBarSlot.Secondary),
            topBarSlots(secondaryCount = 1, overflowCount = 0, hasClose = false, collapse = false),
        )
        assertEquals(
            listOf(TopBarSlot.Close),
            topBarSlots(secondaryCount = 0, overflowCount = 0, hasClose = true, collapse = false),
        )
    }

    @Test
    fun `a lone menu item shows as an icon instead of a menu`() {
        assertEquals(
            listOf(TopBarSlot.Secondary, TopBarSlot.Secondary, TopBarSlot.MenuItem),
            topBarSlots(secondaryCount = 2, overflowCount = 1, hasClose = false, collapse = false),
        )
        assertEquals(
            listOf(TopBarSlot.MenuItem),
            topBarSlots(secondaryCount = 0, overflowCount = 1, hasClose = false, collapse = false),
        )
    }

    @Test
    fun `a lone menu item stays before Close`() {
        assertEquals(
            listOf(TopBarSlot.Secondary, TopBarSlot.MenuItem, TopBarSlot.Close),
            topBarSlots(secondaryCount = 1, overflowCount = 1, hasClose = true, collapse = false),
        )
    }

    @Test
    fun `two or more menu items keep the menu`() {
        assertEquals(
            listOf(TopBarSlot.Secondary, TopBarSlot.Secondary, TopBarSlot.Overflow),
            topBarSlots(secondaryCount = 2, overflowCount = 2, hasClose = false, collapse = false),
        )
        assertEquals(
            listOf(TopBarSlot.Overflow, TopBarSlot.Close),
            topBarSlots(secondaryCount = 0, overflowCount = 3, hasClose = true, collapse = false),
        )
    }

    @Test
    fun `collapsed actions join the menu items`() {
        assertEquals(
            listOf(TopBarSlot.Overflow, TopBarSlot.Close),
            topBarSlots(secondaryCount = 2, overflowCount = 1, hasClose = true, collapse = true),
        )
        assertEquals(
            listOf(TopBarSlot.Overflow),
            topBarSlots(secondaryCount = 1, overflowCount = 1, hasClose = false, collapse = true),
        )
    }

    @Test
    fun `a lone item takes the width More options would`() {
        // Back + bell + star + one item fit exactly as bell + star + More options did.
        assertFalse(collapses(320.dp, hasOverflowMenu = true))
        assertTrue(collapses(319.dp, hasOverflowMenu = true))
    }
}
