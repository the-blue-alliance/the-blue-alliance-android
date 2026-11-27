package com.thebluealliance.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.navigation3.runtime.NavEntryDecorator
import com.thebluealliance.android.ui.components.FocusReturn
import com.thebluealliance.android.ui.components.LocalEntryFocus
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/** How long a returning screen waits for its launcher to be composed, as lists load from the database. */
private const val LAUNCHER_WAIT_MS = 2_000L

/**
 * Gives each back-stack entry its [FocusReturn] memory and, when an entry shows again in keyboard
 * mode, moves focus back to the element that opened the screen the user just left.
 */
@Composable
fun <T : Any> rememberFocusReturnNavEntryDecorator(focusReturn: FocusReturn): NavEntryDecorator<T> =
    remember(focusReturn) {
        NavEntryDecorator(
            onPop = { contentKey -> focusReturn.forget(contentKey) },
            decorate = { entry ->
                val entryFocus = focusReturn.entry(entry.contentKey)
                val inputModeManager = LocalInputModeManager.current
                // Runs each time the entry enters composition: when it first opens (nothing to
                // return to) and when the screen over it closes.
                LaunchedEffect(entryFocus) {
                    val key =
                        entryFocus.takeReturn(inputModeManager.inputMode) ?: return@LaunchedEffect
                    val requester =
                        withTimeoutOrNull(LAUNCHER_WAIT_MS) {
                            snapshotFlow { entryFocus.requesters[key] }.filterNotNull().first()
                        } ?: return@LaunchedEffect
                    // Let the launcher be placed before it takes focus.
                    withFrameNanos {}
                    requester.requestFocus()
                }
                CompositionLocalProvider(LocalEntryFocus provides entryFocus) { entry.Content() }
            },
        )
    }
