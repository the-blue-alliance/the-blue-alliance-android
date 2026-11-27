package com.thebluealliance.android.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput

/**
 * A [androidx.compose.material3.pulltorefresh.PullToRefreshBox] that only a finger or stylus can
 * pull. A mouse wheel or trackpad scroll past the top of a list never ends in a release, so it
 * would leave the arrow stuck half-pulled; those devices refresh with the keyboard instead.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouchPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    val gate = remember { TouchPullGate() }
    Box(
        modifier
            .pullToRefresh(state = state, isRefreshing = isRefreshing, onRefresh = onRefresh)
            // Inside pullToRefresh, so the list's overscroll reaches the gate first.
            .nestedScroll(gate)
            .pointerInput(gate) {
                awaitPointerEventScope {
                    while (true) gate.observe(awaitPointerEvent(PointerEventPass.Initial))
                }
            },
        contentAlignment = contentAlignment,
    ) {
        content()
        PullToRefreshDefaults.Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state,
        )
    }
}

/** Whether [event] comes from a finger or stylus, the inputs that can pull to refresh. */
internal fun isPullInput(event: PointerEvent): Boolean =
    event.type != PointerEventType.Scroll && isPullPointer(event.changes.map { it.type })

/** Whether every pointer in [types] is a finger or stylus. */
internal fun isPullPointer(types: List<PointerType>): Boolean =
    types.isNotEmpty() &&
        types.all {
            it == PointerType.Touch || it == PointerType.Stylus || it == PointerType.Eraser
        }

/**
 * Takes the downward overscroll pull-to-refresh would turn into a pull, unless the latest input
 * was a finger or stylus.
 */
internal class TouchPullGate : NestedScrollConnection {
    var lastInputPulls = true
        private set

    fun observe(event: PointerEvent) {
        lastInputPulls = isPullInput(event)
    }

    fun observe(pulls: Boolean) {
        lastInputPulls = pulls
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = if (!lastInputPulls && available.y > 0f) Offset(0f, available.y) else Offset.Zero
}
