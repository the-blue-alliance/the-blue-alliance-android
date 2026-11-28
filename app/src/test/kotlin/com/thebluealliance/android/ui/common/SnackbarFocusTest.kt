package com.thebluealliance.android.ui.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.ui.focus.FocusDirection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SnackbarFocusTest {
    private fun visuals(
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
    ) = object : SnackbarVisuals {
        override val message = "Couldn't refresh events"
        override val actionLabel = actionLabel
        override val withDismissAction = withDismissAction
        override val duration =
            if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
    }

    private fun focusEnteredBy(direction: FocusDirection) =
        SnackbarFocus().apply {
            onEnter(direction)
            onFocusChanged(true)
        }

    // Room at the bottom

    @Test
    fun `a Retry snackbar takes room so rows can scroll above it`() {
        assertTrue(reservesSpace(visuals(actionLabel = "Retry", withDismissAction = true)))
    }

    @Test
    fun `an Undo snackbar takes room`() {
        assertTrue(reservesSpace(visuals(actionLabel = "Undo", withDismissAction = true)))
    }

    @Test
    fun `a snackbar with only a close button takes room`() {
        assertTrue(reservesSpace(visuals(withDismissAction = true)))
    }

    @Test
    fun `a short message takes no room so nothing shifts`() {
        assertFalse(reservesSpace(visuals()))
    }

    @Test
    fun `no snackbar takes no room`() {
        assertFalse(reservesSpace(null))
    }

    // Where Tab and Shift+Tab land

    @Test
    fun `Shift+Tab into the snackbar lands on its action`() {
        val focus = SnackbarFocus()
        focus.onEnter(FocusDirection.Previous)
        assertTrue(focus.entersAtAction(hasAction = true))
    }

    @Test
    fun `a snackbar without an action keeps the default entry`() {
        val focus = SnackbarFocus()
        focus.onEnter(FocusDirection.Previous)
        assertFalse(focus.entersAtAction(hasAction = false))
    }

    // Where focus goes when it closes

    @Test
    fun `entered with Tab, focus goes back to the row before it`() {
        assertEquals(
            SnackbarExit(SnackbarEdge.First, FocusDirection.Previous),
            focusEnteredBy(FocusDirection.Next).exit(),
        )
    }

    @Test
    fun `entered with Shift+Tab, focus goes back to the element after it`() {
        assertEquals(
            SnackbarExit(SnackbarEdge.Last, FocusDirection.Next),
            focusEnteredBy(FocusDirection.Previous).exit(),
        )
    }

    @Test
    fun `entered with Down, focus goes back up`() {
        assertEquals(
            SnackbarExit(null, FocusDirection.Up),
            focusEnteredBy(FocusDirection.Down).exit(),
        )
    }

    @Test
    fun `entered with Up, focus goes back down`() {
        assertEquals(
            SnackbarExit(null, FocusDirection.Down),
            focusEnteredBy(FocusDirection.Up).exit(),
        )
    }

    @Test
    fun `entered with Right, focus goes back left from the first button`() {
        assertEquals(
            SnackbarExit(SnackbarEdge.First, FocusDirection.Left),
            focusEnteredBy(FocusDirection.Right).exit(),
        )
    }

    @Test
    fun `entered with Left, focus goes back right from the last button`() {
        assertEquals(
            SnackbarExit(SnackbarEdge.Last, FocusDirection.Right),
            focusEnteredBy(FocusDirection.Left).exit(),
        )
    }

    @Test
    fun `focus placed in the snackbar goes to the content before it`() {
        val focus = SnackbarFocus().apply { onFocusChanged(true) }
        assertEquals(SnackbarExit(SnackbarEdge.First, FocusDirection.Previous), focus.exit())
    }

    @Test
    fun `focus elsewhere stays put when the snackbar closes`() {
        assertNull(SnackbarFocus().exit())
    }

    @Test
    fun `focus that left the snackbar stays put when it closes`() {
        val focus = focusEnteredBy(FocusDirection.Next)
        focus.onFocusChanged(false)
        assertNull(focus.exit())
    }

    @Test
    fun `a later entry replaces the earlier direction`() {
        val focus = focusEnteredBy(FocusDirection.Next)
        focus.onFocusChanged(false)
        focus.onEnter(FocusDirection.Previous)
        focus.onFocusChanged(true)
        assertEquals(SnackbarExit(SnackbarEdge.Last, FocusDirection.Next), focus.exit())
    }
}
