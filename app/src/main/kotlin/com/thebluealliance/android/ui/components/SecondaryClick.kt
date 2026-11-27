package com.thebluealliance.android.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput

/** A press of a mouse's secondary (right) button, or a trackpad's secondary tap. */
internal fun isSecondaryPress(
    type: PointerEventType,
    buttons: PointerButtons,
): Boolean = type == PointerEventType.Press && buttons.isSecondaryPressed

/**
 * Calls [onSecondaryClick] with the pointer's position in this element when a right-click presses
 * it, to open the element's context menu there. The press is consumed before any `clickable`
 * inside sees it, so a right-click never also counts as a click. Touch has no secondary button.
 */
fun Modifier.onSecondaryClick(onSecondaryClick: (Offset) -> Unit): Modifier =
    pointerInput(onSecondaryClick) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (isSecondaryPress(event.type, event.buttons)) {
                    val change = event.changes.first()
                    change.consume()
                    onSecondaryClick(change.position)
                }
            }
        }
    }
