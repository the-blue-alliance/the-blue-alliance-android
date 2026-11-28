package com.thebluealliance.android.ui.common

import androidx.compose.material3.SnackbarVisuals
import androidx.compose.ui.focus.FocusDirection

/**
 * Whether the snackbar showing [visuals] takes room at the bottom of the window, so the screen
 * above it ends where it starts. One with buttons stays until it's used, so lists must be able to
 * scroll their rows, and Tab must be able to bring a focused row, out from under it. A short
 * message goes away on its own and draws over the content, so nothing shifts for it.
 */
internal fun reservesSpace(visuals: SnackbarVisuals?): Boolean =
    visuals != null && (visuals.actionLabel != null || visuals.withDismissAction)

/** One end of the snackbar's row of buttons. */
internal enum class SnackbarEdge { First, Last }

/**
 * How keyboard focus leaves a snackbar that is closing: [from] the button to focus first (null for
 * the one focused now), then one [direction] move out of it.
 */
internal data class SnackbarExit(
    val from: SnackbarEdge?,
    val direction: FocusDirection,
)

/**
 * Keyboard focus around a snackbar with buttons: which one Tab or Shift+Tab lands on, and where
 * focus goes when the snackbar closes while it has focus. Told when focus enters the snackbar and
 * whether it is still inside.
 */
internal class SnackbarFocus {
    /** The direction focus moved in to enter the snackbar, while it is inside. */
    private var enteredBy: FocusDirection? = null

    /** Whether keyboard focus is on one of the snackbar's buttons. */
    private var inside = false

    /** Called when a focus search enters the snackbar moving in [direction]. */
    fun onEnter(direction: FocusDirection) {
        enteredBy = direction
    }

    /** Called with whether the snackbar holds focus whenever that changes. */
    fun onFocusChanged(hasFocus: Boolean) {
        inside = hasFocus
        if (!hasFocus) enteredBy = null
    }

    /**
     * Whether focus entering the snackbar goes to its action button. It does from either side, so
     * one Tab or one Shift+Tab reaches Retry; the close button is one more Tab, or Esc.
     */
    fun entersAtAction(hasAction: Boolean): Boolean = hasAction

    /**
     * How to move focus out as the snackbar closes, or null to leave focus alone: back the way it
     * came in, past the snackbar's other button, so it lands on the element it left. Focus that
     * got in without a focus search goes to the content before the snackbar.
     */
    fun exit(): SnackbarExit? {
        if (!inside) return null
        return when (enteredBy) {
            FocusDirection.Previous -> SnackbarExit(SnackbarEdge.Last, FocusDirection.Next)
            FocusDirection.Down -> SnackbarExit(null, FocusDirection.Up)
            FocusDirection.Up -> SnackbarExit(null, FocusDirection.Down)
            FocusDirection.Right -> SnackbarExit(SnackbarEdge.First, FocusDirection.Left)
            FocusDirection.Left -> SnackbarExit(SnackbarEdge.Last, FocusDirection.Right)
            else -> SnackbarExit(SnackbarEdge.First, FocusDirection.Previous)
        }
    }
}
