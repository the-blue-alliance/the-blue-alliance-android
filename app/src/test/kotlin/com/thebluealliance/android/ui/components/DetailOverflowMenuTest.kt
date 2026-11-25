package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DetailOverflowMenuTest {
    @Test
    fun `menu is hidden while loading without pinned shortcuts`() {
        assertFalse(hasDetailOverflowItems(entityLoaded = false, canPinShortcuts = false))
    }

    @Test
    fun `pinning alone fills the menu while loading`() {
        assertTrue(hasDetailOverflowItems(entityLoaded = false, canPinShortcuts = true))
    }

    @Test
    fun `a loaded entity can always be shared`() {
        assertTrue(hasDetailOverflowItems(entityLoaded = true, canPinShortcuts = false))
        assertTrue(hasDetailOverflowItems(entityLoaded = true, canPinShortcuts = true))
    }
}
