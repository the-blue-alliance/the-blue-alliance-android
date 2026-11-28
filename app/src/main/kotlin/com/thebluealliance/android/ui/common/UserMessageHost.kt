package com.thebluealliance.android.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.components.focusRegion
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.theme.TbaIcons
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/**
 * Shows [UserMessageBus] messages as snackbars. Placed in a Column between the content and the
 * bottom bar, it takes no height and draws upward over the content, so nothing shifts.
 *
 * A message with an action stays up until it's used or dismissed (Material's default once a
 * snackbar has an action), with a close button beside it. Leaving [screen] dismisses it: its
 * action belongs to the screen that posted it.
 */
@Composable
fun UserMessageHost(
    padNavigationBars: Boolean,
    screen: Any?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bus = remember(context) { context.userMessageBus() }
    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(bus) {
        bus.messages.showLatest { message ->
            val action = message.action
            val result =
                hostState.showSnackbar(
                    message = message.text,
                    actionLabel = action?.label,
                    withDismissAction = action != null,
                )
            if (result == SnackbarResult.ActionPerformed) action?.onPerform?.invoke()
        }
    }
    LaunchedEffect(screen) {
        val showing = hostState.currentSnackbarData
        if (showing?.visuals?.actionLabel != null) showing.dismiss()
    }
    Box(modifier.fillMaxWidth().height(0.dp)) {
        SnackbarHost(
            hostState = hostState,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .wrapContentHeight(Alignment.Bottom, unbounded = true)
                    .then(if (padNavigationBars) Modifier.navigationBarsPadding() else Modifier),
        ) { data -> UserMessageSnackbar(data) }
    }
}

/**
 * Material's snackbar, with the hand pointer on its buttons, and Esc dismissing it while it has
 * keyboard focus.
 */
@Composable
private fun UserMessageSnackbar(data: SnackbarData) {
    val visuals = data.visuals
    Snackbar(
        modifier =
            Modifier
                // The margin Material's own snackbar keeps from the window edges.
                .padding(12.dp)
                .focusRegion()
                .onKeyEvent { event ->
                    val esc = event.key == Key.Escape && event.type == KeyEventType.KeyUp
                    if (esc) data.dismiss()
                    esc
                },
        action =
            visuals.actionLabel?.let { label ->
                {
                    TextButton(
                        onClick = data::performAction,
                        modifier = Modifier.handCursor(),
                        colors =
                            ButtonDefaults.textButtonColors(
                                contentColor = SnackbarDefaults.actionColor,
                            ),
                    ) { Text(label) }
                }
            },
        dismissAction =
            if (visuals.withDismissAction) {
                {
                    IconButton(
                        onClick = data::dismiss,
                        modifier = Modifier.handCursor(),
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                contentColor = SnackbarDefaults.dismissActionContentColor,
                            ),
                    ) { Icon(TbaIcons.Close, contentDescription = "Dismiss") }
                }
            } else {
                null
            },
    ) { Text(visuals.message) }
}

/**
 * Shows each message with [show], cancelling the one still showing. Cancelling
 * showSnackbar dismisses its snackbar, so a burst of taps doesn't queue up minutes of messages.
 */
internal suspend fun <T> Flow<T>.showLatest(show: suspend (T) -> Unit) = collectLatest(show)
