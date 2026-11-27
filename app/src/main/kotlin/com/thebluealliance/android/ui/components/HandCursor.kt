package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection

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
 * Shows the hand while the pointer is over an item of the lazy list this modifies whose key
 * [isClickable] accepts, and the arrow over everything else: other items, the [contentPadding]
 * beside and below the items, and blank space past the last one. For a third-party list, such as
 * AboutLibraries', whose rows take no modifier of their own.
 */
@Composable
fun Modifier.handOverListItems(
    state: LazyListState,
    contentPadding: PaddingValues,
    isClickable: (key: Any) -> Boolean,
): Modifier {
    var pointer by remember { mutableStateOf<Offset?>(null) }
    var width by remember { mutableIntStateOf(0) }
    val direction = LocalLayoutDirection.current
    val density = LocalDensity.current
    val left = with(density) { contentPadding.calculateLeftPadding(direction).roundToPx() }
    val right = with(density) { contentPadding.calculateRightPadding(direction).roundToPx() }
    // Derived, so scrolling under a still pointer updates it too.
    val hand by remember(state, left, right) {
        derivedStateOf {
            val at = pointer ?: return@derivedStateOf false
            val info = state.layoutInfo
            isOverListItem(
                x = at.x,
                y = at.y,
                left = left,
                right = width - right,
                items =
                    info.visibleItemsInfo
                        .filter { isClickable(it.key) }
                        .map {
                            (it.offset - info.viewportStartOffset).let { top ->
                                top until
                                    top + it.size
                            }
                        },
            )
        }
    }
    return this
        .onSizeChanged { width = it.width }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    pointer =
                        if (event.type == PointerEventType.Exit) {
                            null
                        } else {
                            event.changes.firstOrNull()?.position
                        }
                }
            }
        }.handCursor(enabled = hand)
}

/** Whether [x], [y] lies between [left] and [right] and inside one of the items' vertical [items] spans. */
internal fun isOverListItem(
    x: Float,
    y: Float,
    left: Int,
    right: Int,
    items: List<IntRange>,
): Boolean = x >= left && x < right && items.any { y.toInt() in it }
