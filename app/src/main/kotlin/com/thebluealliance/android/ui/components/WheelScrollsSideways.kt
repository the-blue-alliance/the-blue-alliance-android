package com.thebluealliance.android.ui.components

import android.view.ViewConfiguration
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch

/**
 * Lets a plain vertical mouse wheel scroll a horizontal-only row with [state], as desktop apps do.
 * Wheel down scrolls toward the end. Horizontal wheels and trackpad swipes are left to the row's
 * own scrolling, and touch never sends wheel events, so neither changes. Only for rows that don't
 * sit inside a vertical scroller, which would otherwise lose the wheel.
 */
fun Modifier.verticalWheelScrollsSideways(state: ScrollableState): Modifier =
    this then WheelScrollsSidewaysElement(state)

/**
 * The sideways scroll, in pixels, for a wheel event's [scrollDelta] (in wheel steps, positive y
 * being wheel down), or null to leave the event alone: it isn't purely vertical, or the row can't
 * move that way. [pixelsPerStep] is the platform's vertical scroll factor.
 */
internal fun sidewaysWheelDelta(
    scrollDelta: Offset,
    pixelsPerStep: Float,
    canScrollForward: Boolean,
    canScrollBackward: Boolean,
): Float? {
    if (scrollDelta.x != 0f || scrollDelta.y == 0f) return null
    val delta = scrollDelta.y * pixelsPerStep
    val canMove = if (delta > 0f) canScrollForward else canScrollBackward
    return delta.takeIf { canMove }
}

private data class WheelScrollsSidewaysElement(
    val state: ScrollableState,
) : ModifierNodeElement<WheelScrollsSidewaysNode>() {
    override fun create() = WheelScrollsSidewaysNode(state)

    override fun update(node: WheelScrollsSidewaysNode) {
        node.state = state
    }
}

private class WheelScrollsSidewaysNode(
    var state: ScrollableState,
) : Modifier.Node(),
    PointerInputModifierNode,
    CompositionLocalConsumerModifierNode {
    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        // Main runs child-first, so the row's own scrollable has already taken any horizontal part.
        if (pass != PointerEventPass.Main || pointerEvent.type != PointerEventType.Scroll) return
        val change = pointerEvent.changes.firstOrNull() ?: return
        if (change.type != PointerType.Mouse || change.isConsumed) return
        val pixelsPerStep =
            ViewConfiguration
                .get(
                    currentValueOf(LocalContext),
                ).scaledVerticalScrollFactor
        val delta =
            sidewaysWheelDelta(
                change.scrollDelta,
                pixelsPerStep,
                state.canScrollForward,
                state.canScrollBackward,
            ) ?: return
        change.consume()
        coroutineScope.launch { state.scrollBy(delta) }
    }

    override fun onCancelPointerInput() = Unit
}
