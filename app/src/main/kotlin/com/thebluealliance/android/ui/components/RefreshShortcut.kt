package com.thebluealliance.android.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The refreshes of the screens on display, so one keyboard shortcut (F5, Ctrl+R) can refresh the
 * screen in front without each screen handling keys.
 *
 * A screen joins while its top bar has a refresh (see [TBATopAppBar]). The newest one wins: it is
 * the screen just navigated to, or the detail pane beside a list, while the one sliding out is
 * still composed.
 */
class RefreshTargets {
    private val targets = mutableListOf<Any>()
    private val refreshes = mutableMapOf<Any, () -> Unit>()

    /** Adds [refresh] as the newest target; call the returned function to remove it. */
    fun register(refresh: () -> Unit): () -> Unit {
        val token = Any()
        targets += token
        refreshes[token] = refresh
        return {
            targets -= token
            refreshes -= token
        }
    }

    /** Refreshes the newest target. Returns false when no screen on display can refresh. */
    fun refresh(): Boolean {
        val newest = targets.lastOrNull() ?: return false
        refreshes.getValue(newest)()
        return true
    }
}

/** The [RefreshTargets] the app's keyboard shortcuts refresh, if any. */
val LocalRefreshTargets = staticCompositionLocalOf<RefreshTargets?> { null }

/** Registers [onRefresh] with [LocalRefreshTargets] while it is non-null and composed. */
@Composable
internal fun RefreshShortcutEffect(onRefresh: (() -> Unit)?) {
    val targets = LocalRefreshTargets.current ?: return
    val latest by rememberUpdatedState(onRefresh)
    val hasRefresh = onRefresh != null
    DisposableEffect(targets, hasRefresh) {
        val unregister = if (hasRefresh) targets.register { latest?.invoke() } else null
        onDispose { unregister?.invoke() }
    }
}
