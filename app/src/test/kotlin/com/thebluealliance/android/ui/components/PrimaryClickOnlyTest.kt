package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PrimaryClickOnlyTest {
    @Test
    fun `a right-click release is held back`() {
        val tracker = SecondaryPressTracker()
        tracker.onPress(id = 1, isMouse = true, secondaryOnly = true)
        assertTrue(tracker.onRelease(1))
    }

    @Test
    fun `a left click goes through`() {
        val tracker = SecondaryPressTracker()
        tracker.onPress(id = 1, isMouse = true, secondaryOnly = false)
        assertFalse(tracker.onRelease(1))
    }

    @Test
    fun `a touch goes through`() {
        val tracker = SecondaryPressTracker()
        tracker.onPress(id = 1, isMouse = false, secondaryOnly = true)
        assertFalse(tracker.onRelease(1))
    }

    @Test
    fun `only the right-click's own release is held back, once`() {
        val tracker = SecondaryPressTracker()
        tracker.onPress(id = 1, isMouse = true, secondaryOnly = true)
        assertFalse(tracker.onRelease(2))
        assertTrue(tracker.onRelease(1))
        assertFalse(tracker.onRelease(1))
    }

    @Test
    fun `a left click after a right-click goes through`() {
        val tracker = SecondaryPressTracker()
        tracker.onPress(id = 1, isMouse = true, secondaryOnly = true)
        tracker.onRelease(1)
        tracker.onPress(id = 1, isMouse = true, secondaryOnly = false)
        assertFalse(tracker.onRelease(1))
    }
}
