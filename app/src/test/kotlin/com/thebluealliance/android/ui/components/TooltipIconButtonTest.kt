package com.thebluealliance.android.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

@OptIn(ExperimentalMaterial3Api::class)
class TooltipIconButtonTest {
    @Test
    fun `a click hides a showing tooltip before it acts`() {
        val state = TooltipState(initialIsVisible = true)
        var visibleAtClick: Boolean? = null
        dismissingTooltip(state) { visibleAtClick = state.transition.targetState }()
        assertEquals(false, visibleAtClick)
        assertFalse(state.transition.targetState)
    }

    @Test
    fun `a click with no tooltip showing just acts`() {
        val state = TooltipState()
        var clicks = 0
        dismissingTooltip(state) { clicks++ }()
        assertFalse(state.transition.targetState)
        assertEquals(1, clicks)
    }
}
