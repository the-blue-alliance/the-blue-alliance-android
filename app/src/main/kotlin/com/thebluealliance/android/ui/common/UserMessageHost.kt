package com.thebluealliance.android.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.components.focusRegion
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.theme.TbaIcons
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/**
 * The state of the window's [UserMessageHost]: the snackbar showing, and how much of the bottom of
 * the window it covers when it [reservesSpace]. The frame hands that to the screens as [insets],
 * and keeps focused items above it with [bringIntoViewSpec].
 */
@Stable
class UserMessageState {
    internal val hostState = SnackbarHostState()

    /** The snackbar host's height, from the window's bottom edge (or the bottom bar) up. */
    internal var hostHeightPx by mutableIntStateOf(0)

    /** Whether the snackbar showing takes room at the bottom of the window. */
    val reservesSpace: Boolean
        get() = reservesSpace(hostState.currentSnackbarData?.visuals)

    /** Pixels at the bottom of the screen that a snackbar taking room covers, or 0. */
    internal val coveredPx: Int
        get() = if (reservesSpace) hostHeightPx else 0

    /** [coveredPx] as a bottom inset, for [LocalUserMessageInsets]. */
    val insets: WindowInsets = UserMessageInsets { coveredPx }

    /** [parent] keeping focused items clear of the snackbar, for LocalBringIntoViewSpec. */
    @OptIn(ExperimentalFoundationApi::class)
    fun bringIntoViewSpec(parent: BringIntoViewSpec): BringIntoViewSpec =
        AboveSnackbarBringIntoViewSpec(parent) { coveredPx }
}

@Composable
fun rememberUserMessageState(): UserMessageState = remember { UserMessageState() }

/**
 * Shows [UserMessageBus] messages as snackbars. Placed in a Column between the content and the
 * bottom bar, it takes no height and draws upward over the content, so nothing shifts. A message
 * with an action stays up until it's used or dismissed (Material's default once a snackbar has an
 * action), with a close button beside it; while one does, [state] reports the height it covers so
 * screens can pad their lists by it. Leaving [screen] dismisses it: its action belongs to the
 * screen that posted it.
 */
@Composable
fun UserMessageHost(
    state: UserMessageState,
    padNavigationBars: Boolean,
    screen: Any?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bus = remember(context) { context.userMessageBus() }
    val hostState = state.hostState
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
    // On leaving, not on arriving: a message the new screen posts right away stays.
    DisposableEffect(screen) {
        onDispose {
            val showing = hostState.currentSnackbarData
            if (showing?.visuals?.actionLabel != null) showing.dismiss()
        }
    }
    Box(modifier.fillMaxWidth().height(0.dp)) {
        SnackbarHost(
            hostState = hostState,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .wrapContentHeight(Alignment.Bottom, unbounded = true)
                    .onSizeChanged { state.hostHeightPx = it.height }
                    .then(if (padNavigationBars) Modifier.navigationBarsPadding() else Modifier),
        ) { data -> UserMessageSnackbar(data) }
    }
}

/**
 * Material's snackbar, with the hand pointer on its buttons, and Esc dismissing it while it has
 * keyboard focus. Like [com.thebluealliance.android.ui.components.EscapeHandler], Esc acts on
 * release and only for a press seen going down here, and the press is claimed so nothing
 * beneath acts on it.
 */
@Composable
private fun UserMessageSnackbar(shown: SnackbarData) {
    val focus = remember(shown) { SnackbarFocus() }
    val first = remember { FocusRequester() }
    val last = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    // Every way out (the action, the close button, Esc) first sends keyboard focus back out.
    val data =
        remember(shown) {
            FocusReturningSnackbarData(shown) { exitFocus(focus, first, last, focusManager) }
        }
    val visuals = data.visuals
    val hasAction = visuals.actionLabel != null
    val escPressed = remember { mutableStateOf(false) }
    Snackbar(
        modifier =
            Modifier
                // The margin Material's own snackbar keeps from the window edges.
                .padding(12.dp)
                .onFocusChanged { focus.onFocusChanged(it.hasFocus) }
                .focusProperties {
                    onEnter = {
                        focus.onEnter(requestedFocusDirection)
                        if (focus.entersAtAction(hasAction)) first.requestFocus()
                    }
                }.focusRegion()
                .onKeyEvent { event ->
                    if (event.key != Key.Escape) return@onKeyEvent false
                    when (event.type) {
                        KeyEventType.KeyDown ->
                            if (event.nativeKeyEvent.repeatCount == 0) escPressed.value = true
                        KeyEventType.KeyUp -> {
                            val act = escPressed.value && !event.nativeKeyEvent.isCanceled
                            escPressed.value = false
                            if (act) data.dismiss()
                        }
                    }
                    true
                },
        action =
            visuals.actionLabel?.let { label ->
                {
                    TextButton(
                        onClick = data::performAction,
                        modifier = Modifier.focusRequester(first).handCursor(),
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
                        modifier = Modifier.focusRequester(last).handCursor(),
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

/** [data], sending keyboard focus out of the snackbar with [exitFocus] before it closes. */
private class FocusReturningSnackbarData(
    private val data: SnackbarData,
    private val exitFocus: () -> Unit,
) : SnackbarData by data {
    override fun performAction() {
        exitFocus()
        data.performAction()
    }

    override fun dismiss() {
        exitFocus()
        data.dismiss()
    }
}

/** Moves keyboard focus out of the snackbar the way [focus] says, if it says to. */
private fun exitFocus(
    focus: SnackbarFocus,
    first: FocusRequester,
    last: FocusRequester,
    focusManager: FocusManager,
) {
    val exit = focus.exit() ?: return
    when (exit.from) {
        SnackbarEdge.First -> first.requestFocus()
        // With no close button, the action is the last button too.
        SnackbarEdge.Last -> if (!last.requestFocus()) first.requestFocus()
        null -> Unit
    }
    focusManager.moveFocus(exit.direction)
}

/**
 * Shows each message with [show], cancelling the one still showing. Cancelling
 * showSnackbar dismisses its snackbar, so a burst of taps doesn't queue up minutes of messages.
 */
internal suspend fun <T> Flow<T>.showLatest(show: suspend (T) -> Unit) = collectLatest(show)
