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
        bus.messages.collect { hostState.showSnackbar(it) }
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
