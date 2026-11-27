package com.thebluealliance.android.ui.components

import android.view.KeyEvent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import java.util.WeakHashMap

/**
 * The Esc handlers of one window, newest last. Like `BackHandler`'s dispatcher, only the newest
 * handler runs, so an inner surface's Esc wins over the screen's beneath it.
 *
 * Esc acts on release, as dialogs do, and only for a press this dispatcher saw go down: when a
 * menu or the keyboard closes on the press, the release that follows does nothing here. The press
 * is claimed so nothing else acts on it.
 */
internal class EscapeDispatcher {
    private val handlers = mutableListOf<() -> Unit>()
    private var pressed = false

    val isEmpty: Boolean get() = handlers.isEmpty()

    fun add(handler: () -> Unit) {
        handlers += handler
    }

    fun remove(handler: () -> Unit) {
        handlers.remove(handler)
        if (handlers.isEmpty()) pressed = false
    }

    /** Handles one unhandled key event; true if it consumed it. */
    fun onKey(
        keyCode: Int,
        action: Int,
        repeatCount: Int,
        isCanceled: Boolean,
    ): Boolean {
        if (keyCode != KeyEvent.KEYCODE_ESCAPE || handlers.isEmpty()) return false
        return when (action) {
            KeyEvent.ACTION_DOWN -> {
                if (repeatCount == 0) pressed = true
                true
            }
            KeyEvent.ACTION_UP -> {
                val act = pressed && !isCanceled
                pressed = false
                if (act) handlers.last().invoke()
                act
            }
            else -> false
        }
    }
}

private val dispatchers = WeakHashMap<View, Pair<EscapeDispatcher, Any>>()

private fun View.escapeDispatcher(): EscapeDispatcher =
    dispatchers
        .getOrPut(this) {
            val dispatcher = EscapeDispatcher()
            val listener =
                ViewCompat.OnUnhandledKeyEventListenerCompat { _, event ->
                    dispatcher.onKey(
                        event.keyCode,
                        event.action,
                        event.repeatCount,
                        event.isCanceled,
                    )
                }
            ViewCompat.addOnUnhandledKeyEventListener(this, listener)
            dispatcher to listener
        }.first

private fun View.releaseEscapeDispatcher() {
    val (dispatcher, listener) = dispatchers[this] ?: return
    if (!dispatcher.isEmpty) return
    ViewCompat.removeOnUnhandledKeyEventListener(
        this,
        listener as ViewCompat.OnUnhandledKeyEventListenerCompat,
    )
    dispatchers.remove(this)
}

/**
 * Calls [onEscape] when Esc is pressed in this window and nothing focused used it, the way
 * `BackHandler` hooks Back. It listens for keys the view tree leaves unhandled, so it works with no
 * focused element, such as after a mouse click. When several are enabled in one window, the most
 * recently enabled one runs.
 */
@Composable
fun EscapeHandler(
    enabled: Boolean = true,
    onEscape: () -> Unit,
) {
    val view = LocalView.current
    val currentOnEscape by rememberUpdatedState(onEscape)
    DisposableEffect(view, enabled) {
        if (!enabled) return@DisposableEffect onDispose {}
        val handler = { currentOnEscape() }
        val dispatcher = view.escapeDispatcher()
        dispatcher.add(handler)
        onDispose {
            dispatcher.remove(handler)
            view.releaseEscapeDispatcher()
        }
    }
}
