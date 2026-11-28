package com.thebluealliance.android.ui.components

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipState
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
class FocusTooltipStateTest {
    private fun newState() =
        FocusTooltipState(TooltipState(isPersistent = false, mutatorMutex = MutatorMutex()))

    // What TooltipBox does when its anchor gains focus.
    private fun TestScope.boxShowsForFocus(state: FocusTooltipState) {
        backgroundScope.launch { state.show(MutatePriority.PreventUserInput) }
        runCurrent()
    }

    private fun TestScope.focusBy(
        state: FocusTooltipState,
        key: Key,
        withAltCtrlOrMeta: Boolean = false,
    ): Boolean {
        state.onFocusChanged(true)
        boxShowsForFocus(state)
        val shows = state.onKey(KeyEventType.KeyUp, key, withAltCtrlOrMeta)
        if (shows) backgroundScope.launch { state.showForFocus() }
        runCurrent()
        return shows
    }

    @Test
    fun `focus placed on screen open shows no tooltip`() =
        runTest {
            val state = newState()
            state.onFocusChanged(true)
            boxShowsForFocus(state)
            assertFalse(state.isVisible)
        }

    @Test
    fun `Enter that opened the screen shows no tooltip`() =
        runTest {
            val state = newState()
            assertFalse(focusBy(state, Key.Enter))
            assertFalse(state.isVisible)
        }

    @Test
    fun `Alt+Left back to the screen shows no tooltip`() =
        runTest {
            val state = newState()
            assertFalse(focusBy(state, Key.DirectionLeft, withAltCtrlOrMeta = true))
            assertFalse(state.isVisible)
        }

    @Test
    fun `Tab onto the button shows the tooltip`() =
        runTest {
            val state = newState()
            assertTrue(focusBy(state, Key.Tab))
            assertTrue(state.isVisible)
        }

    @Test
    fun `arrow keys onto the button show the tooltip`() =
        runTest {
            for (key in listOf(
                Key.DirectionUp,
                Key.DirectionDown,
                Key.DirectionLeft,
                Key.DirectionRight,
            )) {
                val state = newState()
                assertTrue(focusBy(state, key), "$key")
                assertTrue(state.isVisible, "$key")
            }
        }

    @Test
    fun `Shift released before Tab still shows the tooltip`() =
        runTest {
            val state = newState()
            state.onFocusChanged(true)
            boxShowsForFocus(state)
            assertFalse(state.onKey(KeyEventType.KeyUp, Key.ShiftLeft, false))
            assertTrue(state.onKey(KeyEventType.KeyUp, Key.Tab, false))
        }

    @Test
    fun `Alt released before Left shows no tooltip`() =
        runTest {
            val state = newState()
            state.onFocusChanged(true)
            boxShowsForFocus(state)
            assertFalse(state.onKey(KeyEventType.KeyUp, Key.AltLeft, false))
            assertFalse(state.onKey(KeyEventType.KeyUp, Key.DirectionLeft, false))
            assertFalse(state.isVisible)
        }

    @Test
    fun `focus gone before the tooltip shows shows nothing`() =
        runTest {
            val state = newState()
            state.onFocusChanged(true)
            boxShowsForFocus(state)
            assertTrue(state.onKey(KeyEventType.KeyUp, Key.Tab, false))
            state.onFocusChanged(false)
            backgroundScope.launch { state.showForFocus() }
            runCurrent()
            assertFalse(state.isVisible)
        }

    @Test
    fun `Tab released before the keyboard app gets it shows the tooltip`() =
        runTest {
            val state = newState()
            state.onFocusChanged(true)
            boxShowsForFocus(state)
            assertTrue(state.onKeyBeforeSoftKeyboard(KeyEventType.KeyUp, Key.Tab, false))
            backgroundScope.launch { state.showForFocus() }
            runCurrent()
            assertTrue(state.isVisible)
        }

    @Test
    fun `Shift+Tab released before the keyboard app gets it shows the tooltip`() =
        runTest {
            val state = newState()
            state.onFocusChanged(true)
            boxShowsForFocus(state)
            assertFalse(state.onKeyBeforeSoftKeyboard(KeyEventType.KeyUp, Key.ShiftLeft, false))
            assertTrue(state.onKeyBeforeSoftKeyboard(KeyEventType.KeyUp, Key.Tab, false))
        }

    @Test
    fun `a release seen before and after the keyboard app counts once`() =
        runTest {
            val state = newState()
            state.onFocusChanged(true)
            assertTrue(state.onKeyBeforeSoftKeyboard(KeyEventType.KeyUp, Key.Tab, false))
            assertFalse(state.onKey(KeyEventType.KeyUp, Key.Tab, false))
        }

    @Test
    fun `Enter released before the keyboard app gets it shows no tooltip`() =
        runTest {
            val state = newState()
            state.onFocusChanged(true)
            boxShowsForFocus(state)
            assertFalse(state.onKeyBeforeSoftKeyboard(KeyEventType.KeyUp, Key.Enter, false))
            assertFalse(state.onKey(KeyEventType.KeyUp, Key.Enter, false))
            assertFalse(state.isVisible)
        }

    @Test
    fun `a focus tooltip hides on its own`() =
        runTest {
            val state = newState()
            focusBy(state, Key.Tab)
            // M3's plain tooltip duration is 1.5 s.
            advanceTimeBy(1_501)
            assertFalse(state.isVisible)
        }

    @Test
    fun `only the first key after focus counts`() =
        runTest {
            val state = newState()
            focusBy(state, Key.Enter)
            assertFalse(state.onKey(KeyEventType.KeyUp, Key.Tab, false))
        }

    @Test
    fun `Tab pressed down on the button is focus leaving`() =
        runTest {
            val state = newState()
            state.onFocusChanged(true)
            assertFalse(state.onKey(KeyEventType.KeyDown, Key.Tab, false))
        }

    @Test
    fun `hover still shows the tooltip and keeps it while hovered`() =
        runTest {
            val state = newState()
            backgroundScope.launch { state.show(MutatePriority.UserInput) }
            advanceTimeBy(5_000)
            assertTrue(state.isVisible)
        }

    @Test
    fun `long press still shows the tooltip`() =
        runTest {
            val state = newState()
            backgroundScope.launch { state.show(MutatePriority.PreventUserInput) }
            runCurrent()
            assertTrue(state.isVisible)
        }

    @Test
    fun `long press after focus left still shows the tooltip`() =
        runTest {
            val state = newState()
            state.onFocusChanged(true)
            state.onFocusChanged(false)
            backgroundScope.launch { state.show(MutatePriority.PreventUserInput) }
            runCurrent()
            assertTrue(state.isVisible)
        }
}
