package com.thebluealliance.android.ui.components

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics

/** The top bar's refresh callback, for the title node that owns the Refresh action. */
internal val LocalTopBarRefresh = compositionLocalOf<(() -> Unit)?> { null }

/**
 * Exposes pull-to-refresh as a "Refresh" accessibility action, since the drag gesture has no
 * semantic equivalent. Goes on a node TalkBack focuses: the top-bar title's owner.
 */
internal fun Modifier.refreshAction(onRefresh: (() -> Unit)?): Modifier =
    if (onRefresh == null) {
        this
    } else {
        semantics {
            customActions =
                listOf(
                    CustomAccessibilityAction("Refresh") {
                        onRefresh()
                        true
                    },
                )
        }
    }
