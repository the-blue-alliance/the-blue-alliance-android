package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.roundToInt

private val TouchTargetWidth = 48.dp

/** Whether [x] is on the [stripWidth]-wide strip at the end edge of a [width]-wide list. */
internal fun onFastScrollbarStrip(
    x: Float,
    width: Float,
    stripWidth: Float,
    ltr: Boolean,
): Boolean = if (ltr) x >= width - stripWidth else x <= stripWidth

/** Who owns a press on the strip once it starts to move. */
internal enum class StripDrag {
    /** Still inside the touch slop: the row keeps the press for now. */
    UNDECIDED,

    /** A mostly vertical move past the slop: the fast scrollbar takes it. */
    SCROLLBAR,

    /** Any other move past the slop: the row and the list keep it for the rest of the gesture. */
    ROW,
}

/**
 * Who owns a press on the strip that has moved by ([dx], [dy]). It is decided once, as soon as the
 * move passes [touchSlop], so the list's own scroll can't be taken over later in the same gesture.
 */
internal fun fastScrollbarClaimsDrag(
    dx: Float,
    dy: Float,
    touchSlop: Float,
): StripDrag =
    when {
        hypot(dx, dy) <= touchSlop -> StripDrag.UNDECIDED
        abs(dy) > abs(dx) -> StripDrag.SCROLLBAR
        else -> StripDrag.ROW
    }

@Composable
fun FastScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable BoxScope.() -> Unit,
) {
    val totalItems by remember { derivedStateOf { listState.layoutInfo.totalItemsCount } }
    var isDragging by remember { mutableStateOf(false) }

    val ltr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val currentTotal by rememberUpdatedState(totalItems)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScrollbar(
                    listState,
                    contentPadding,
                    keepVisible = isDragging,
                    // Only draws the thumb: the handler below is the one drag handler on the edge.
                    draggable = false,
                )
                // Watches presses on the Initial pass instead of covering the rows with a strip, so
                // hover, the hand pointer, and taps on the strip all reach the row underneath. A
                // press becomes a fast scroll only once it moves past the slop mostly vertically.
                .pointerInput(listState, ltr) {
                    awaitPointerEventScope {
                        while (true) {
                            val press = awaitPointerEvent(PointerEventPass.Initial)
                            if (press.type != PointerEventType.Press) continue
                            val down = press.changes.firstOrNull { it.changedToDown() } ?: continue
                            if (currentTotal <= 0) continue
                            if (down.type == PointerType.Mouse &&
                                !press.buttons.isPrimaryPressed
                            ) {
                                continue
                            }
                            val width = size.width.toFloat()
                            val height = size.height.toFloat()
                            val strip = TouchTargetWidth.toPx()
                            if (!onFastScrollbarStrip(down.position.x, width, strip, ltr)) continue
                            var claimed = false
                            try {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    val change =
                                        event.changes.firstOrNull { it.id == down.id } ?: break
                                    if (!change.pressed) {
                                        if (claimed) change.consume()
                                        break
                                    }
                                    if (!claimed) {
                                        // Someone else (the list's own scroll) already took it.
                                        if (change.isConsumed) break
                                        val moved = change.position - down.position
                                        when (
                                            fastScrollbarClaimsDrag(
                                                moved.x,
                                                moved.y,
                                                viewConfiguration.touchSlop,
                                            )
                                        ) {
                                            StripDrag.UNDECIDED -> continue
                                            // Left to the row and the list for the whole gesture.
                                            StripDrag.ROW -> break
                                            StripDrag.SCROLLBAR -> claimed = true
                                        }
                                    }
                                    isDragging = true
                                    change.consume()
                                    val fraction = (change.position.y / height).coerceIn(0f, 1f)
                                    val total = currentTotal
                                    val targetIndex =
                                        (fraction * total)
                                            .roundToInt()
                                            .coerceIn(0, (total - 1).coerceAtLeast(0))
                                    listState.requestScrollToItem(targetIndex)
                                }
                            } finally {
                                isDragging = false
                            }
                        }
                    }
                },
    ) {
        content()
    }
}
