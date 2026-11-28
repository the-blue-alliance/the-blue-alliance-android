package com.thebluealliance.android.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/**
 * Shows [UserMessageBus] messages as snackbars. Placed in a Column between the content and the
 * bottom bar, it takes no height and draws upward over the content, so nothing shifts.
 */
@Composable
fun UserMessageHost(
    padNavigationBars: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bus = remember(context) { context.userMessageBus() }
    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(bus) {
        bus.messages.showLatest { hostState.showSnackbar(it.text) }
    }
    Box(modifier.fillMaxWidth().height(0.dp)) {
        SnackbarHost(
            hostState = hostState,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .wrapContentHeight(Alignment.Bottom, unbounded = true)
                    .then(if (padNavigationBars) Modifier.navigationBarsPadding() else Modifier),
        )
    }
}

/**
 * Shows each message with [show], cancelling the one still showing. Cancelling
 * showSnackbar dismisses its snackbar, so a burst of taps doesn't queue up minutes of messages.
 */
internal suspend fun <T> Flow<T>.showLatest(show: suspend (T) -> Unit) = collectLatest(show)
