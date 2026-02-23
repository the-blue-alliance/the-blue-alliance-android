package com.thebluealliance.android.shortcuts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.lifecycle.ReportFragment
import com.thebluealliance.android.domain.model.ModelType

/**
 * Report that an item that is a valid shortcut (i.e. a team or an event) was visited when this
 * composable enters composition.
 *
 * This effect will manage checking whether the provided key is a valid shortcut - callers
 * can (and should) blindly call this for all teams and events to ensure proper tracking.
 *
 * This reports the use to ShortcutManager so that the system can rank shortcuts appropriately.
 */
@Composable
fun ReportShortcutVisitEffect(
    modelKey: String?
) {
    val context = LocalContext.current
    LaunchedEffect(modelKey) {
        modelKey?.let {
            val shortcuts = ShortcutManagerCompat.getDynamicShortcuts(context)
            if (shortcuts.any { it.id == modelKey }) {
                ShortcutManagerCompat.reportShortcutUsed(context, modelKey)
            }
        }
    }
}
