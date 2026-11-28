package com.thebluealliance.android.ui.components

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DetailOverflowActionsTest {
    private val icon = ImageVector.Builder("icon", 24.dp, 24.dp, 24f, 24f).build()
    private val search = TopBarAction("Search", icon, {})
    private val share = TopBarAction("Share", icon, {})
    private val pin = TopBarAction("Add to home screen", icon, {})

    private fun labels(
        share: TopBarAction?,
        pinShortcut: TopBarAction?,
        notFound: Boolean,
    ) = detailOverflowActions(share, search, pinShortcut, notFound).map { it.label }

    @Test
    fun `a loaded page leads its menu with Search`() {
        assertEquals(
            listOf("Search", "Share", "Add to home screen"),
            labels(share, pin, notFound = false),
        )
    }

    @Test
    fun `a launcher that cannot pin keeps Search and Share`() {
        assertEquals(listOf("Search", "Share"), labels(share, pinShortcut = null, notFound = false))
    }

    @Test
    fun `a page still loading keeps Search first`() {
        assertEquals(
            listOf("Search", "Add to home screen"),
            labels(share = null, pin, notFound = false),
        )
    }

    @Test
    fun `a missing page keeps only Search`() {
        assertEquals(listOf("Search"), labels(share = null, pin, notFound = true))
    }

    @Test
    fun `a missing page shows Search as an icon, not a one-item menu`() {
        val overflow = detailOverflowActions(null, search, pin, notFound = true)
        assertEquals(
            listOf(TopBarSlot.MenuItem),
            topBarSlots(0, overflow.size, hasClose = false, collapse = false),
        )
    }

    @Test
    fun `a loaded page keeps bell, star and More on a phone`() {
        val overflow = detailOverflowActions(share, search, pin, notFound = false)
        assertEquals(
            listOf(TopBarSlot.Secondary, TopBarSlot.Secondary, TopBarSlot.Overflow),
            topBarSlots(2, overflow.size, hasClose = false, collapse = false),
        )
    }
}
