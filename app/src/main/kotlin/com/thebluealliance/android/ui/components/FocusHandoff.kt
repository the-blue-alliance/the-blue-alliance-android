package com.thebluealliance.android.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.InputMode

/**
 * Hands keyboard focus to the content that replaces a control the user just pressed, such as a
 * "Show 2025 media" button that gives way to the photos it asked for. Without it, the focused
 * control leaves the composition and focus falls back to the start of the window.
 *
 * Attach [requester] to the element that should take focus, call [arm] from the control's click
 * handler, and run [FocusHandoffEffect]. Only a keyboard press arms it, so touch is unchanged.
 */
@Stable
class FocusHandoff {
    val requester = FocusRequester()

    /** The selection (a year, a stat type) the press asked for, or null when nothing is pending. */
    internal var armedFor: Any? by mutableStateOf(null)

    /** Whether the screen has shown [armedFor] since it was armed. */
    internal var reached: Boolean by mutableStateOf(false)

    /** Arms the hand-off for a press that selects [selection], when it came from a keyboard. */
    fun arm(
        selection: Any,
        inputMode: InputMode,
    ) {
        armedFor = if (inputMode == InputMode.Keyboard) selection else null
        reached = false
    }

    internal fun disarm() {
        armedFor = null
        reached = false
    }
}

@Composable
fun rememberFocusHandoff(): FocusHandoff = remember { FocusHandoff() }

/** What [FocusHandoffEffect] does for one state of the screen. */
internal enum class FocusHandoffStep {
    /** Not armed, or the selection hasn't reached the screen yet. */
    WAIT,

    /** The selection's content is up: focus it and stop. */
    FOCUS_CONTENT,

    /**
     * The selection is empty too and the control is back in its place (relabeled): keep focus on
     * it, and stay armed in case content arrives for the same selection.
     */
    FOCUS_CONTROL,

    /** The screen moved on to another selection: stop. */
    DISARM,
}

internal fun focusHandoffStep(
    armedFor: Any?,
    reached: Boolean,
    current: Any,
    hasContent: Boolean,
    controlShown: Boolean,
): FocusHandoffStep =
    when {
        armedFor == null -> FocusHandoffStep.WAIT
        armedFor != current -> if (reached) FocusHandoffStep.DISARM else FocusHandoffStep.WAIT
        hasContent -> FocusHandoffStep.FOCUS_CONTENT
        controlShown -> FocusHandoffStep.FOCUS_CONTROL
        else -> FocusHandoffStep.WAIT
    }

/**
 * Moves focus to [handoff]'s requester once the armed selection is [current] and its content is
 * composed. [hasContent] means the replacement content is shown; [controlShown] means the pressed
 * control is shown again instead (the requester should be on both).
 */
@Composable
fun FocusHandoffEffect(
    handoff: FocusHandoff,
    current: Any,
    hasContent: Boolean,
    controlShown: Boolean = false,
) {
    val armedFor = handoff.armedFor
    LaunchedEffect(armedFor, current, hasContent, controlShown) {
        if (armedFor == current) handoff.reached = true
        when (focusHandoffStep(armedFor, handoff.reached, current, hasContent, controlShown)) {
            FocusHandoffStep.WAIT -> Unit
            FocusHandoffStep.DISARM -> handoff.disarm()
            FocusHandoffStep.FOCUS_CONTROL -> requestWhenComposed(handoff.requester)
            FocusHandoffStep.FOCUS_CONTENT -> {
                requestWhenComposed(handoff.requester)
                handoff.disarm()
            }
        }
    }
}

/** Lazy items compose during layout, so the target can take a frame or two to attach. */
private suspend fun requestWhenComposed(requester: FocusRequester) {
    repeat(MAX_FOCUS_FRAMES) {
        withFrameNanos {}
        if (requester.requestFocus()) return
    }
}

private const val MAX_FOCUS_FRAMES = 10
