package com.thebluealliance.android.ui.components

import android.os.SystemClock
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics

/**
 * Marks one region of the window (navigation, the screen, a pane) for keyboard and screen-reader
 * traversal: Tab finishes the region before moving on, and TalkBack reads it as a unit. Pass
 * [requester] to let focus be sent into the region.
 */
fun Modifier.focusRegion(requester: FocusRequester? = null): Modifier =
    this
        .then(if (requester != null) Modifier.focusRequester(requester) else Modifier)
        .semantics { isTraversalGroup = true }
        .focusGroup()

/** Where keyboard focus starts when it first enters the window: the navigation region. */
class WindowFocusEntry {
    /** Attach to the navigation region with [focusRegion]. */
    val navigation = FocusRequester()

    /** Uptime of the last first-keyboard-focus entry; the key that caused it is older. */
    internal var enteredAt = -1L
}

/**
 * Whether focus entering the window's content in [direction] is the first keyboard focus. The
 * first Tab or arrow press after touch gives the window focus as a downward entry and is then
 * handled as a key, so without help it lands on the topmost element (the top bar) and moves on
 * from there, leaving the rail for last. That entry goes to the navigation instead, and its key is
 * dropped (see [causedEntry]). Tab wrapping around re-enters as [FocusDirection.Next] and keeps placement order.
 */
internal fun isFirstKeyboardFocus(direction: FocusDirection): Boolean =
    direction == FocusDirection.Down

/**
 * Whether a key pressed at [keyTime] (uptime) is the one that caused a first keyboard focus at
 * [enteredAt], when the window hands focus over before passing the key on. A later key is not.
 */
internal fun causedEntry(
    keyTime: Long,
    enteredAt: Long,
): Boolean = keyTime <= enteredAt

/** The keys that move focus, which the first keyboard focus swallows. */
internal fun isFocusKey(key: Key): Boolean =
    key == Key.Tab ||
        key == Key.DirectionUp ||
        key == Key.DirectionDown ||
        key == Key.DirectionLeft ||
        key == Key.DirectionRight

/**
 * Wraps the window's regions so that the first keyboard focus starts at [entry]'s navigation
 * region when one is showing (see [isFirstKeyboardFocus]).
 */
fun Modifier.focusEntersAt(entry: WindowFocusEntry): Modifier =
    this
        .onPreviewKeyEvent { event ->
            event.type == KeyEventType.KeyDown &&
                isFocusKey(event.key) &&
                causedEntry(event.nativeKeyEvent.eventTime, entry.enteredAt)
        }.focusProperties {
            onEnter = {
                if (isFirstKeyboardFocus(requestedFocusDirection) &&
                    entry.navigation.requestFocus()
                ) {
                    entry.enteredAt = SystemClock.uptimeMillis()
                }
            }
        }.focusGroup()

/** Whether a pager's [page] may take keyboard focus while [currentPage] is showing. */
internal fun isPageFocusable(
    page: Int,
    currentPage: Int,
): Boolean = page == currentPage

/**
 * Makes pager [page] a focus group that keyboard focus enters only while it is [state]'s current
 * page. A pager lays out the pages beside the current one when a focus search reaches its edge, so
 * without this Tab and arrows move into a neighbouring tab's page and the pager follows, switching
 * tabs with no click. Refused there, the search moves on past the pager instead.
 */
fun Modifier.pagerPageFocus(
    state: PagerState,
    page: Int,
): Modifier =
    this
        .focusProperties {
            onEnter = { if (!isPageFocusable(page, state.currentPage)) cancelFocusChange() }
        }.focusGroup()
