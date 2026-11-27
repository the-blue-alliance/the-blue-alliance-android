package com.thebluealliance.android.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Remembers which mouse presses were secondary-only (a right-click, or a trackpad's secondary
 * tap) and picks out their releases. A click acts on release, so a consumed release never counts
 * as one.
 */
internal class SecondaryPressTracker {
    private val secondaryPointers = mutableSetOf<Long>()

    /** Records a press of pointer [id]; [secondaryOnly] is true when only the secondary button is down. */
    fun onPress(
        id: Long,
        isMouse: Boolean,
        secondaryOnly: Boolean,
    ) {
        if (isMouse && secondaryOnly) secondaryPointers += id else secondaryPointers -= id
    }

    /** Whether the release of pointer [id] ends a secondary-only press, and so must not click. */
    fun onRelease(id: Long): Boolean = secondaryPointers.remove(id)
}

/**
 * Stops a right-click from acting as a click anywhere inside this element. Compose's `clickable`,
 * and so every Material button, tab and list row, clicks on any mouse button. This consumes the
 * release of a secondary-only mouse press before anything inside sees it, which cancels the
 * click. The press itself is left alone, so right-click menus that open on the press, such as a
 * text field's, still work. Touch and the primary button are unchanged.
 */
fun Modifier.primaryClicksOnly(): Modifier =
    pointerInput(Unit) {
        val tracker = SecondaryPressTracker()
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                for (change in event.changes) {
                    if (change.changedToDownIgnoreConsumed()) {
                        tracker.onPress(
                            id = change.id.value,
                            isMouse = change.type == PointerType.Mouse,
                            secondaryOnly =
                                event.buttons.isSecondaryPressed && !event.buttons.isPrimaryPressed,
                        )
                    } else if (change.changedToUpIgnoreConsumed() &&
                        tracker.onRelease(change.id.value)
                    ) {
                        change.consume()
                    }
                }
            }
        }
    }
