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
 */
fun Modifier.handCursor(): Modifier = pointerHoverIcon(PointerIcon.Hand)
