package com.thebluealliance.android.ui.components

import androidx.compose.foundation.MutatePriority
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import kotlinx.coroutines.launch

/**
 * An icon button whose [label] is both the icon's content description and a plain tooltip below
 * it, or above it with [tooltipAbove] for a button at the bottom of the window. The tooltip shows
 * while a mouse rests on the button and on a long press, as M3's TooltipBox does by default, and for 1.5 s when the user moves keyboard focus onto the button with Tab or an
 * arrow key (see [FocusTooltipState]). The button shows the hand pointer.
 *
 * A click dismisses the tooltip: one shown by keyboard focus would otherwise stay up over the
 * sheet or menu the click opens, because focus moving into that popup doesn't hide it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipIconButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    tooltipAbove: Boolean = false,
) {
    val m3State = rememberTooltipState()
    val tooltipState = remember(m3State) { FocusTooltipState(m3State) }
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(
                if (tooltipAbove) TooltipAnchorPosition.Above else TooltipAnchorPosition.Below,
            ),
        tooltip = { PlainTooltip { Text(label) } },
        state = tooltipState,
    ) {
        IconButton(
            onClick = dismissingTooltip(tooltipState, onClick),
            modifier =
                modifier
                    .handCursor()
                    .onFocusChanged { tooltipState.onFocusChanged(it.isFocused) }
                    .onPreInterceptKeyBeforeSoftKeyboard { event ->
                        val modified = event.withAltCtrlOrMeta
                        if (tooltipState.onKeyBeforeSoftKeyboard(event.type, event.key, modified)) {
                            scope.launch { tooltipState.showForFocus() }
                        }
                        false
                    }.onPreviewKeyEvent { event ->
                        if (tooltipState.onKey(event.type, event.key, event.withAltCtrlOrMeta)) {
                            scope.launch { tooltipState.showForFocus() }
                        }
                        false
                    },
        ) {
            Icon(icon, contentDescription = label, tint = tint)
        }
    }
}

/**
 * [TooltipIconButton]'s tooltip state: [tooltip], with keyboard focus handled so the tooltip shows
 * only when the user moves focus onto the button, and hides on its own.
 *
 * TooltipBox shows its tooltip whenever the anchor gains focus, at a priority M3 never times out,
 * so it stays up for as long as the button keeps focus. That includes focus nobody asked for: on a
 * phone, a screen opened by keyboard or a deep link gets focus on its Back button, and the tooltip
 * would sit over the first tab. TooltipBox's focus show is dropped here. Instead the button shows
 * the tooltip at default priority, which M3 hides after 1.5 s, when the first key to reach it after
 * gaining focus (Shift aside) is the release of the Tab or arrow key that moved focus there (focus
 * moves on the press, so the release goes to the newly focused button). Keys are read before the
 * keyboard app gets them as well as after, because the keyboard app can drop the release of a Tab
 * that leaves a text field. Focus placed by the app is followed by no key, or by the release of the
 * Enter, Alt or Ctrl that opened the screen, and shows nothing.
 * Hover and long press go straight to [tooltip].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Stable
internal class FocusTooltipState(
    private val tooltip: TooltipState,
) : TooltipState by tooltip {
    /** The button has keyboard focus. */
    private var focused = false

    /** The button has focus and hasn't received a key since it gained it. */
    private var awaitingFirstKey = false

    /** The button gained focus and TooltipBox's show for that focus hasn't arrived yet. */
    private var focusShowPending = false

    /** Called with the button's focus whenever it changes. */
    fun onFocusChanged(isFocused: Boolean) {
        focused = isFocused
        awaitingFirstKey = isFocused
        focusShowPending = isFocused
    }

    /**
     * Called for each key event that reaches the focused button. Returns whether to show the
     * tooltip now: only for the first key after focus arrives, when that key moved focus here.
     */
    fun onKey(
        type: KeyEventType,
        key: Key,
        withAltCtrlOrMeta: Boolean,
    ): Boolean {
        // Shift+Tab's Shift can be released before its Tab.
        if (isShiftKey(key)) return false
        val show = awaitingFirstKey && movedFocusHere(type, key, withAltCtrlOrMeta)
        awaitingFirstKey = false
        return show
    }

    /**
     * Called for each key event that reaches the focused button before the keyboard app sees it,
     * which is before [onKey] sees it. Returns whether to show the tooltip now, as [onKey] does.
     *
     * After a Tab out of a text field, the keyboard app can drop the release, so [onKey] never gets
     * it; this pass still does. A release seen in both passes counts once.
     */
    fun onKeyBeforeSoftKeyboard(
        type: KeyEventType,
        key: Key,
        withAltCtrlOrMeta: Boolean,
    ): Boolean = onKey(type, key, withAltCtrlOrMeta)

    /**
     * Shows the tooltip for focus that [onKey] approved, unless focus has already moved on. Default
     * priority, so M3 hides the non-persistent tooltip after 1.5 s.
     */
    suspend fun showForFocus() {
        if (focused) tooltip.show()
    }

    override suspend fun show(mutatePriority: MutatePriority) {
        // TooltipBox shows for anchor focus at PreventUserInput, as it does for a long press.
        if (mutatePriority == MutatePriority.PreventUserInput && focusShowPending) {
            focusShowPending = false
            return
        }
        tooltip.show(mutatePriority)
    }
}

/**
 * Whether a key event reaching a just-focused element is the release of the Tab, Shift+Tab or
 * arrow key that moved focus to it.
 */
internal fun movedFocusHere(
    type: KeyEventType,
    key: Key,
    withAltCtrlOrMeta: Boolean,
): Boolean = type == KeyEventType.KeyUp && isFocusKey(key) && !withAltCtrlOrMeta

private val KeyEvent.withAltCtrlOrMeta: Boolean
    get() = isAltPressed || isCtrlPressed || isMetaPressed

internal fun isShiftKey(key: Key): Boolean = key == Key.ShiftLeft || key == Key.ShiftRight

/** [onClick], after hiding [tooltipState]'s tooltip if it's showing. */
@OptIn(ExperimentalMaterial3Api::class)
internal fun dismissingTooltip(
    tooltipState: TooltipState,
    onClick: () -> Unit,
): () -> Unit =
    {
        if (tooltipState.isVisible) tooltipState.dismiss()
        onClick()
    }
