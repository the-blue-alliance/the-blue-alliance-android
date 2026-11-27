package com.thebluealliance.android.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipState
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

@OptIn(ExperimentalMaterial3Api::class)
class TooltipIconButtonTest {
    @Test
    fun `a click hides a showing tooltip and still acts`() =
        runTest {
            val state = TooltipState(initialIsVisible = true)
            var clicks = 0
            dismissingTooltip(state, this) { clicks++ }()
            testScheduler.advanceUntilIdle()
            assertFalse(state.transition.targetState)
            assertEquals(1, clicks)
        }

    @Test
    fun `a click with no tooltip showing just acts`() =
        runTest {
            val state = TooltipState()
            var clicks = 0
            dismissingTooltip(state, this) { clicks++ }()
            testScheduler.advanceUntilIdle()
            assertFalse(state.transition.targetState)
            assertEquals(1, clicks)
        }
}
