package com.thebluealliance.android.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ScrollIndicatorState
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.theme.TBAMotionTokens
import kotlinx.coroutines.delay

private val ThumbThickness = 4.dp
private val EdgeInset = 4.dp
private val TrackInset = 4.dp
private val MinThumbLength = 48.dp

/** Width of the strip along the edge where a pointer grabs the thumb or wakes the scrollbar. */
private val GrabWidth = 16.dp

/** How long the scrollbar stays after scrolling stops. */
private const val FADE_DELAY_MS = 1500L

private val HoverEventTypes =
    setOf(PointerEventType.Enter, PointerEventType.Move, PointerEventType.Exit)

/**
 * Whether a hover event of [eventType] leaves the scrollbar awake: a mouse inside the list and
 * [onStrip], the edge strip where the thumb is grabbed. Touch and stylus moves never hover it.
 */
internal fun hoversStrip(
    eventType: PointerEventType,
    pointerType: PointerType,
    onStrip: Boolean,
): Boolean = pointerType == PointerType.Mouse && eventType != PointerEventType.Exit && onStrip

/** Whether a press can grab the thumb: any touch or stylus, but only a mouse's primary button. */
internal fun canGrabThumb(
    pointerType: PointerType,
    primaryPressed: Boolean,
): Boolean = pointerType != PointerType.Mouse || primaryPressed

/** Where the thumb sits on a track: [start] past the track's top, [length] long. */
internal data class ScrollbarThumb(
    val start: Float,
    val length: Float,
)

/**
 * Thumb for a list scrolled [scrollOffset] into [contentSize] through a [viewportSize] window, on a
 * [trackLength] track: as long as the visible share of the content, but at least [minLength]. Null
 * when the content fits or a size is not yet known.
 */
internal fun scrollbarThumb(
    scrollOffset: Int,
    contentSize: Int,
    viewportSize: Int,
    trackLength: Float,
    minLength: Float,
): ScrollbarThumb? {
    if (scrollOffset == Int.MAX_VALUE || contentSize == Int.MAX_VALUE) return null
    if (viewportSize == Int.MAX_VALUE) return null
    val range = contentSize - viewportSize
    if (range <= 0 || trackLength <= 0f) return null
    val length =
        (trackLength * viewportSize / contentSize)
            .coerceIn(minOf(minLength, trackLength), trackLength)
    val fraction = (scrollOffset.toFloat() / range).coerceIn(0f, 1f)
    return ScrollbarThumb(start = (trackLength - length) * fraction, length = length)
}

/** Content distance to scroll so a [thumbLength] thumb on a [trackLength] track moves [thumbDelta]. */
internal fun scrollbarDragDistance(
    thumbDelta: Float,
    contentSize: Int,
    viewportSize: Int,
    trackLength: Float,
    thumbLength: Float,
): Float {
    val free = trackLength - thumbLength
    if (free <= 0f) return 0f
    return thumbDelta * (contentSize - viewportSize) / free
}

/** The track's top and bottom in a [height]-tall list, clear of its vertical [contentPadding]. */
private fun Density.scrollbarTrack(
    height: Float,
    contentPadding: PaddingValues,
): ClosedFloatingPointRange<Float> {
    val top = contentPadding.calculateTopPadding().toPx() + TrackInset.toPx()
    val bottom = height - contentPadding.calculateBottomPadding().toPx() - TrackInset.toPx()
    return top..maxOf(top, bottom)
}

private fun Density.thumbFor(
    indicator: ScrollIndicatorState,
    track: ClosedFloatingPointRange<Float>,
): ScrollbarThumb? =
    scrollbarThumb(
        scrollOffset = indicator.scrollOffset,
        contentSize = indicator.contentSize,
        viewportSize = indicator.viewportSize,
        trackLength = track.endInclusive - track.start,
        minLength = MinThumbLength.toPx(),
    )

/**
 * Draws a thin scrollbar at the end edge of a vertically scrolling list or grid using [state]. It
 * fades in while the list scrolls, while [keepVisible], or while a mouse rests on the edge strip,
 * and fades out shortly after. While shown, its thumb can be dragged with a mouse or finger. The track
 * stays clear of the list's vertical [contentPadding], such as the space under a navigation bar.
 */
@Composable
fun Modifier.verticalScrollbar(
    state: ScrollableState,
    contentPadding: PaddingValues = PaddingValues(),
    keepVisible: Boolean = false,
): Modifier {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    val direction = LocalLayoutDirection.current
    val alpha = remember { Animatable(0f) }
    var dragging by remember { mutableStateOf(false) }
    var hovering by remember { mutableStateOf(false) }
    val active = state.isScrollInProgress || dragging || keepVisible || hovering
    LaunchedEffect(active) {
        if (active) {
            alpha.animateTo(1f, TBAMotionTokens.fastEffectsSpec())
        } else {
            delay(FADE_DELAY_MS)
            alpha.animateTo(0f, TBAMotionTokens.defaultEffectsSpec())
        }
    }
    return this
        .pointerInput(state, contentPadding, direction) {
            awaitPointerEventScope {
                try {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull() ?: continue
                        val grab = GrabWidth.toPx()
                        val onStrip =
                            if (direction == LayoutDirection.Ltr) {
                                change.position.x >= size.width - grab
                            } else {
                                change.position.x <= grab
                            }
                        if (event.type in HoverEventTypes) {
                            hovering = hoversStrip(event.type, change.type, onStrip)
                            continue
                        }
                        if (event.type != PointerEventType.Press || !onStrip) continue
                        if (alpha.value <= 0f) continue
                        if (!canGrabThumb(change.type, event.buttons.isPrimaryPressed)) continue
                        val indicator = state.scrollIndicatorState ?: continue
                        val track = scrollbarTrack(size.height.toFloat(), contentPadding)
                        val thumb = thumbFor(indicator, track) ?: continue
                        val thumbTop = track.start + thumb.start
                        if (change.position.y !in thumbTop..thumbTop + thumb.length) continue
                        change.consume()
                        dragging = true
                        try {
                            var lastY = change.position.y
                            while (true) {
                                val move = awaitPointerEvent(PointerEventPass.Initial)
                                val drag = move.changes.firstOrNull { it.id == change.id } ?: break
                                drag.consume()
                                if (!drag.pressed) break
                                val delta = drag.position.y - lastY
                                lastY = drag.position.y
                                val current = state.scrollIndicatorState ?: break
                                val length = thumbFor(current, track)?.length ?: break
                                state.dispatchRawDelta(
                                    scrollbarDragDistance(
                                        thumbDelta = delta,
                                        contentSize = current.contentSize,
                                        viewportSize = current.viewportSize,
                                        trackLength = track.endInclusive - track.start,
                                        thumbLength = length,
                                    ),
                                )
                            }
                        } finally {
                            dragging = false
                        }
                    }
                } finally {
                    // A restarted or removed pointerInput must not leave the scrollbar pinned.
                    hovering = false
                    dragging = false
                }
            }
        }.drawWithContent {
            drawContent()
            val shown = alpha.value
            if (shown <= 0f) return@drawWithContent
            val indicator = state.scrollIndicatorState ?: return@drawWithContent
            val track = scrollbarTrack(size.height, contentPadding)
            val thumb = thumbFor(indicator, track) ?: return@drawWithContent
            val thickness = ThumbThickness.toPx()
            val x =
                if (layoutDirection == LayoutDirection.Ltr) {
                    size.width - EdgeInset.toPx() - thickness
                } else {
                    EdgeInset.toPx()
                }
            drawRoundRect(
                color = color,
                alpha = shown,
                topLeft = Offset(x, track.start + thumb.start),
                size = Size(thickness, thumb.length),
                cornerRadius = CornerRadius(thickness / 2),
            )
        }
}
