package com.thebluealliance.android.ui.components

import android.view.KeyEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

/** Esc does its job on release, as dialogs do; the press is only claimed so nothing else acts. */
internal enum class EscapeKeyStep { Ignore, Claim, Act }

internal fun escapeKeyStep(
    keyCode: Int,
    action: Int,
): EscapeKeyStep =
    when {
        keyCode != KeyEvent.KEYCODE_ESCAPE -> EscapeKeyStep.Ignore
        action == KeyEvent.ACTION_UP -> EscapeKeyStep.Act
        else -> EscapeKeyStep.Claim
    }

/**
 * Calls [onEscape] when Esc is pressed in this window and nothing focused used it, the way
 * `BackHandler` hooks Back. It listens for keys the view tree leaves unhandled, so it works with no
 * focused element, such as after a mouse click.
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
        val listener =
            ViewCompat.OnUnhandledKeyEventListenerCompat { _, event ->
                when (escapeKeyStep(event.keyCode, event.action)) {
                    EscapeKeyStep.Ignore -> false
                    EscapeKeyStep.Claim -> true
                    EscapeKeyStep.Act -> {
                        currentOnEscape()
                        true
                    }
                }
            }
        ViewCompat.addOnUnhandledKeyEventListener(view, listener)
        onDispose { ViewCompat.removeOnUnhandledKeyEventListener(view, listener) }
    }
}
