package com.thebluealliance.android.ui.components

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.currentCompositeKeyHashCode
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.InputMode
import java.util.concurrent.atomic.AtomicLong

/**
 * Keyboard focus memory for one back-stack entry: the elements in it that open other screens
 * ([returnsFocus]), and which of them opened the screen now covering it.
 */
@Stable
class EntryFocus internal constructor(
    private val owner: FocusReturn,
) {
    /** The entry's launchers that are composed now, by [returnsFocus] key. */
    internal val requesters = mutableStateMapOf<Long, FocusRequester>()

    /** The launcher that opened the screen covering this entry, to focus when it shows again. */
    internal var returnTo: Long? = null

    /** Launchers gone on return, such as menu items, by key, to the key focus returns to instead. */
    internal val standIns = mutableMapOf<Long, Long>()

    internal fun focusChanged(
        key: Long,
        focused: Boolean,
    ) {
        if (focused) {
            owner.focused = this to key
            // Focus is back in this entry, so an older return is spent.
            returnTo = null
        } else if (owner.focused == this to key) {
            owner.focused = null
        }
    }

    /** Whether [requester] is still the composed launcher for [key]. */
    internal fun holds(
        key: Long,
        requester: FocusRequester,
    ): Boolean = requesters[key] === requester

    /** The pending return to keep across activity recreation, or null when there is none. */
    internal fun savedReturn(): Long? = returnTo

    /** Restores a return saved by [savedReturn] after the activity was recreated. */
    internal fun restoreReturn(key: Long) {
        if (returnTo == null) returnTo = key
    }

    /** Takes the launcher to focus now that the entry shows again, if focus should go back. */
    internal fun takeReturn(inputMode: InputMode): Long? {
        val key = returnTo ?: return null
        returnTo = null
        return key.takeIf { returnsFocusIn(inputMode) }
    }
}

/**
 * Where keyboard focus goes back to when the user returns to a screen, for every back-stack entry
 * of a window. A screen opened from a focused [returnsFocus] element hands focus back to that
 * element when the screen is closed with the keyboard (Alt+Left, Esc, or Back in the top bar),
 * instead of focus starting over at the navigation.
 */
@Stable
class FocusReturn {
    private val entries = mutableMapOf<Any, EntryFocus>()

    /** The launcher holding keyboard focus right now, if any. */
    internal var focused: Pair<EntryFocus, Long>? = null

    /** The focus memory of the entry with [contentKey]. */
    fun entry(contentKey: Any): EntryFocus = entries.getOrPut(contentKey) { EntryFocus(this) }

    /** Forgets a popped entry. */
    fun forget(contentKey: Any) {
        val removed = entries.remove(contentKey) ?: return
        if (focused?.first === removed) focused = null
    }

    /** Call as a new screen opens: the focused launcher, if any, gets focus back on return. */
    fun onNavigate() {
        val (entry, key) = focused ?: return
        entry.returnTo = entry.standIns[key] ?: key
    }
}

/** Focus goes back only for keyboard users: in touch mode nothing shows focus, so phones are unchanged. */
internal fun returnsFocusIn(inputMode: InputMode): Boolean = inputMode == InputMode.Keyboard

/** The focus memory of the back-stack entry being composed; null outside navigation. */
val LocalEntryFocus = staticCompositionLocalOf<EntryFocus?> { null }

/**
 * Marks an element that opens another screen: if it has keyboard focus when that screen opens,
 * focus comes back to it when the user returns (see [FocusReturn]). Put it before the element's
 * `clickable`, or pass it as an M3 component's `modifier`. It is keyed by its place in the
 * composition, the same key `rememberSaveable` uses, so a lazy list item needs a stable key.
 * It's `composed` so it can join non-composable modifier helpers such as [paddedClickable].
 */
fun Modifier.returnsFocus(): Modifier =
    composed { Modifier.returnsFocus(currentCompositeKeyHashCode) }

/** [returnsFocus] under an explicit [key], for an anchor that [returnsFocusTo] shares. */
internal fun Modifier.returnsFocus(key: Long): Modifier =
    composed {
        val entry = LocalEntryFocus.current ?: return@composed Modifier
        val requester = remember { FocusRequester() }
        DisposableEffect(entry, key, requester) {
            entry.requesters[key] = requester
            onDispose {
                if (entry.requesters[key] === requester) entry.requesters.remove(key)
            }
        }
        Modifier
            .onFocusChanged { entry.focusChanged(key, it.isFocused) }
            .focusRequester(requester)
    }

/** Stand-in keys count down from -1, so each stand-in has its own; they are never saved. */
private val nextStandInKey = AtomicLong(-1L)

/**
 * Marks a launcher that is gone when the user returns, such as a menu item, so focus comes back to
 * the element marked `returnsFocus(anchor)` instead, such as the button that opened the menu.
 */
internal fun Modifier.returnsFocusTo(anchor: Long): Modifier =
    composed {
        val entry = LocalEntryFocus.current ?: return@composed Modifier
        // Its own key: items composed in a loop share a composite key hash.
        val key = remember { nextStandInKey.getAndDecrement() }
        DisposableEffect(entry, key, anchor) {
            entry.standIns[key] = anchor
            onDispose { if (entry.standIns[key] == anchor) entry.standIns.remove(key) }
        }
        Modifier.onFocusChanged { entry.focusChanged(key, it.isFocused) }
    }
