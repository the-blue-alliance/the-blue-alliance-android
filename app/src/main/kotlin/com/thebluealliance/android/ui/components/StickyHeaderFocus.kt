package com.thebluealliance.android.ui.components

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.relocation.BringIntoViewModifierNode
import androidx.compose.ui.unit.LayoutDirection

/**
 * Keyboard order for a LazyColumn's sticky headers. The list places its stuck header after its
 * rows so it draws over them, and Tab follows placement order, so without this Tab reached the
 * header only after every visible row. Put [stickyHeaderFocusOrder] on the LazyColumn and add the
 * headers with [tabOrderedStickyHeader]: Tab then reaches the stuck header first and the
 * rows after it, and Shift+Tab runs the same way backwards.
 */
@Stable
class StickyHeaderFocusOrder internal constructor(
    internal val focusManager: FocusManager,
    private val layoutDirection: LayoutDirection,
) {
    internal val header = FocusRequester()
    internal val list = FocusRequester()
    internal var listHasFocus = false

    /**
     * How many headers are composed right now. A list can have none at all (Teams without
     * favorites), and then Tab moves through the rows as usual.
     */
    internal var attachedHeaders = 0

    /** The list itself takes focus only for the moment [leaveBackward] needs it. */
    internal var listFocusable = false

    /** Focuses the header's first target, the way Tab reaches it. */
    internal fun focusHeaderStart(): Boolean = attachedHeaders > 0 && header.requestFocus()

    /**
     * Focuses the header's last target, the way Shift+Tab reaches it. A group can't be entered
     * with a one-dimensional direction, so this enters from the end side instead.
     */
    internal fun focusHeaderEnd(): Boolean =
        attachedHeaders > 0 && header.requestFocus(headerEndEntry(layoutDirection))

    /**
     * Moves focus to whatever comes before the list. The header is last in placement order, so
     * the default search from it lands on the last visible row instead.
     */
    internal fun leaveBackward() {
        listFocusable = true
        try {
            if (list.requestFocus()) focusManager.moveFocus(FocusDirection.Previous)
        } finally {
            listFocusable = false
        }
    }
}

@Composable
fun rememberStickyHeaderFocusOrder(): StickyHeaderFocusOrder {
    val focusManager = LocalFocusManager.current
    val layoutDirection = LocalLayoutDirection.current
    return remember(focusManager, layoutDirection) {
        StickyHeaderFocusOrder(focusManager, layoutDirection)
    }
}

/**
 * Whether a stuck header takes focus that arrives in [direction]. [fromRows] is true when focus
 * is already in the list. Moving forward, the header comes before the rows, so it takes focus
 * arriving from before the list but not from a row (the rows go on, then focus leaves the list).
 * Moving back, it takes focus from the first row but not from after the list (the last row is
 * first there).
 */
internal fun headerTakesFocus(
    direction: FocusDirection,
    fromRows: Boolean,
): Boolean =
    when (direction) {
        FocusDirection.Next -> !fromRows
        FocusDirection.Previous -> fromRows
        else -> true
    }

/** The direction that enters a row of targets at its end: moving left in LTR, right in RTL. */
internal fun headerEndEntry(layoutDirection: LayoutDirection): FocusDirection =
    if (layoutDirection == LayoutDirection.Ltr) FocusDirection.Left else FocusDirection.Right

/** Orders this LazyColumn's sticky headers before its rows for Tab (see [StickyHeaderFocusOrder]). */
fun Modifier.stickyHeaderFocusOrder(order: StickyHeaderFocusOrder): Modifier =
    this
        .onFocusChanged { order.listHasFocus = it.hasFocus }
        .focusRequester(order.list)
        .focusProperties {
            canFocus = order.listFocusable
            onEnter = {
                if (requestedFocusDirection == FocusDirection.Next) {
                    order.focusHeaderStart()
                }
            }
            onExit = {
                if (requestedFocusDirection == FocusDirection.Previous) {
                    order.focusHeaderEnd()
                }
            }
        }.focusTarget()

/**
 * A sticky header that Tab reaches before the rows under it (see [StickyHeaderFocusOrder]). The
 * rows must hold focus targets: with none, use [inPlaceStickyHeader], since Tab from the header
 * would search downward for a row and scroll the list looking for one.
 */
fun LazyListScope.tabOrderedStickyHeader(
    focusOrder: StickyHeaderFocusOrder,
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyItemScope.(Int) -> Unit,
) {
    stickyHeader(key = key, contentType = contentType) { index ->
        DisposableEffect(focusOrder) {
            focusOrder.attachedHeaders++
            onDispose { focusOrder.attachedHeaders-- }
        }
        StuckHeaderBox(Modifier.stickyHeaderFocus(focusOrder)) { content(index) }
    }
}

/**
 * A sticky header over rows that take no focus (a table of figures). The header's targets are
 * then the list's only ones, so Tab already reaches them in order; this only keeps a focused
 * header from scrolling the list (see [KeepStuckHeaderInPlace]).
 */
fun LazyListScope.inPlaceStickyHeader(
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyItemScope.(Int) -> Unit,
) {
    stickyHeader(key = key, contentType = contentType) { index ->
        StuckHeaderBox(Modifier) { content(index) }
    }
}

@Composable
private fun StuckHeaderBox(
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier.then(KeepStuckHeaderInPlace), propagateMinConstraints = true) { content() }
}

private fun Modifier.stickyHeaderFocus(order: StickyHeaderFocusOrder): Modifier =
    this
        .focusRequester(order.header)
        .focusProperties {
            onEnter = {
                if (!headerTakesFocus(requestedFocusDirection, order.listHasFocus)) {
                    cancelFocusChange()
                }
            }
            onExit = {
                when (requestedFocusDirection) {
                    // The rows start below the header.
                    FocusDirection.Next -> order.focusManager.moveFocus(FocusDirection.Down)
                    FocusDirection.Previous -> order.leaveBackward()
                    else -> Unit
                }
            }
        }.focusGroup()

/**
 * Stops a focused header from scrolling the list. The stuck header is always on screen, but the
 * list treats the area under it as covered, so bringing the header itself into view scrolls up
 * past the top and pulls the refresh indicator down. Scrolling inside the header (the Rankings
 * header's sideways scroll) still happens below this node.
 */
private object KeepStuckHeaderInPlace : ModifierNodeElement<KeepStuckHeaderInPlaceNode>() {
    override fun create() = KeepStuckHeaderInPlaceNode()

    override fun update(node: KeepStuckHeaderInPlaceNode) = Unit

    override fun hashCode(): Int = "KeepStuckHeaderInPlace".hashCode()

    override fun equals(other: Any?): Boolean = other === this
}

private class KeepStuckHeaderInPlaceNode :
    Modifier.Node(),
    BringIntoViewModifierNode {
    override suspend fun bringIntoView(
        childCoordinates: LayoutCoordinates,
        boundsProvider: () -> Rect?,
    ) = Unit
}
