package com.thebluealliance.android.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

/**
 * Shows the hand pointer while a mouse or trackpad hovers this element, marking it clickable.
 *
 * Neither `clickable` nor the Material 3 components set a pointer icon, so every clickable in the
 * app chains this: the shared helpers ([paddedClickable], [sortableHeader], [expandableRow]) build
 * it in, and M3 components take it as their `modifier`. Touch has no pointer, so phones are
 * unchanged. Text fields keep the I-beam because they set their own icon.
 *
 * Pass the control's [enabled] state: a disabled control can't be clicked, so it keeps the arrow.
 */
fun Modifier.handCursor(enabled: Boolean = true): Modifier =
    if (enabled) pointerHoverIcon(PointerIcon.Hand) else this

/**
 * Keeps the arrow over non-clickable content inside a container that shows [handCursor] for all
 * its items, such as a third-party list whose rows take no modifier of their own. An element's own
 * pointer icon wins over its ancestors'.
 */
fun Modifier.arrowCursor(): Modifier = pointerHoverIcon(PointerIcon.Default)
