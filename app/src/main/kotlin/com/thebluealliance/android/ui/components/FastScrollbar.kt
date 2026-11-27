package com.thebluealliance.android.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.theme.TBAMotionTokens
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

private val ThumbWidth = 4.dp
private val ThumbHeight = 48.dp
private val TouchTargetWidth = 48.dp

/** Whether [x] is on the [stripWidth]-wide strip at the end edge of a [width]-wide list. */
private fun onStrip(
    x: Float,
    width: Float,
    stripWidth: Float,
    ltr: Boolean,
): Boolean = if (ltr) x >= width - stripWidth else x <= stripWidth

/**
 * Whether a press at ([x], [y]) in a [width]-wide list belongs to the fast scrollbar rather than
 * the row under it. The strip is [stripWidth] wide at the end edge ([ltr] picks the side), and the
 * thumb spans [thumbTop] to [thumbTop] + [thumbHeight] while [thumbShown].
 *
 * Only a press on the shown thumb is claimed; any other press on the strip reaches the row.
 */
internal fun fastScrollbarClaimsPress(
    x: Float,
    y: Float,
    width: Float,
    stripWidth: Float,
    ltr: Boolean,
    thumbTop: Float,
    thumbHeight: Float,
    thumbShown: Boolean,
): Boolean =
    thumbShown && onStrip(x, width, stripWidth, ltr) && y in thumbTop..thumbTop + thumbHeight

/**
 * Whether a pointer pressed on the strip and moved by ([dx], [dy]) is now a fast-scroll drag:
 * mostly vertical and past [touchSlop]. Until then the press stays with the row.
 */
internal fun fastScrollbarClaimsDrag(
    dx: Float,
    dy: Float,
    touchSlop: Float,
): Boolean = abs(dy) > touchSlop && abs(dy) > abs(dx)

@Composable
fun FastScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val totalItems by remember { derivedStateOf { listState.layoutInfo.totalItemsCount } }
    val thumbFraction by remember {
        derivedStateOf {
            if (totalItems == 0) {
                0f
            } else {
                listState.firstVisibleItemIndex.toFloat() / totalItems
            }
        }
    }
    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

    var isDragging by remember { mutableStateOf(false) }

    val alpha = remember { Animatable(0f) }
    LaunchedEffect(isScrolling, isDragging) {
        if (isScrolling || isDragging) {
            alpha.animateTo(1f, TBAMotionTokens.fastEffectsSpec())
        } else {
            delay(1500)
            alpha.animateTo(0f, TBAMotionTokens.defaultEffectsSpec())
        }
    }

    var trackHeightPx by remember { mutableFloatStateOf(0f) }

    val ltr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val currentFraction by rememberUpdatedState(thumbFraction)
    val currentTotal by rememberUpdatedState(totalItems)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .onSizeChanged { trackHeightPx = it.height.toFloat() }
                // Watches presses on the Initial pass instead of covering the rows with a strip, so
                // hover, the hand pointer, and taps off the thumb all reach the row underneath.
                .pointerInput(listState, ltr) {
                    awaitPointerEventScope {
                        while (true) {
                            val press = awaitPointerEvent(PointerEventPass.Initial)
                            if (press.type != PointerEventType.Press) continue
                            val down = press.changes.firstOrNull() ?: continue
                            if (currentTotal <= 0) continue
                            if (down.type == PointerType.Mouse &&
                                !press.buttons.isPrimaryPressed
                            ) {
                                continue
                            }
                            val width = size.width.toFloat()
                            val height = size.height.toFloat()
                            val strip = TouchTargetWidth.toPx()
                            if (!onStrip(down.position.x, width, strip, ltr)) continue
                            val thumbHeight = ThumbHeight.toPx()
                            val thumbTop =
                                currentFraction * (height - thumbHeight).coerceAtLeast(0f)
                            var claimed =
                                fastScrollbarClaimsPress(
                                    x = down.position.x,
                                    y = down.position.y,
                                    width = width,
                                    stripWidth = strip,
                                    ltr = ltr,
                                    thumbTop = thumbTop,
                                    thumbHeight = thumbHeight,
                                    thumbShown = alpha.value > 0f,
                                )
                            if (claimed) down.consume()
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
                                        claimed =
                                            fastScrollbarClaimsDrag(
                                                moved.x,
                                                moved.y,
                                                viewConfiguration.touchSlop,
                                            )
                                        if (!claimed) continue
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

        if (totalItems > 0) {
            // Visible thumb indicator
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 4.dp)
                        .alpha(alpha.value)
                        .offset {
                            val thumbHeightPx = ThumbHeight.toPx()
                            val scrollableTrack = (trackHeightPx - thumbHeightPx).coerceAtLeast(0f)
                            val offsetY =
                                (thumbFraction * scrollableTrack).coerceIn(
                                    0f,
                                    scrollableTrack,
                                )
                            IntOffset(0, offsetY.roundToInt())
                        }.width(ThumbWidth)
                        .height(ThumbHeight)
                        .clip(RoundedCornerShape(ThumbWidth / 2))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant),
            )
        }
    }
}
