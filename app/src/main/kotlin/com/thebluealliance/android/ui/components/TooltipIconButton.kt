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
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import kotlinx.coroutines.launch

/**
 * An icon button whose [label] is both the icon's content description and a plain tooltip below
 * it. The tooltip shows while a mouse rests on the button, while it has keyboard focus, and on a
 * long press, as M3's TooltipBox does by default. The button shows the hand pointer.
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
) {
    val m3State = rememberTooltipState()
    val tooltipState = remember(m3State) { FocusTooltipState(m3State) }
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
        tooltip = { PlainTooltip { Text(label) } },
        state = tooltipState,
    ) {
        IconButton(
            onClick = dismissingTooltip(tooltipState, onClick),
            modifier =
                modifier
                    .handCursor()
                    .onFocusChanged { tooltipState.onFocusChanged(it.isFocused) }
                    .onPreviewKeyEvent { event ->
                        val modified =
                            event.isAltPressed || event.isCtrlPressed || event.isMetaPressed
                        if (tooltipState.onKey(event.type, event.key, modified)) {
                            scope.launch { tooltipState.show() }
                        }
                        false
                    },
        ) {
            Icon(icon, contentDescription = label, tint = tint)
        }
    }
}

/**
 * [TooltipIconButton]'s tooltip state: [tooltip], told when its button gains and loses focus and
 * which keys reach it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Stable
internal class FocusTooltipState(
    private val tooltip: TooltipState,
) : TooltipState by tooltip {
    /** Called with the button's focus whenever it changes. */
    @Suppress("UNUSED_PARAMETER")
    fun onFocusChanged(isFocused: Boolean) = Unit

    /**
     * Called for each key event that reaches the focused button. Returns whether to show the
     * tooltip now.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onKey(
        type: KeyEventType,
        key: Key,
        withAltCtrlOrMeta: Boolean,
    ): Boolean = false

    override suspend fun show(mutatePriority: MutatePriority) = tooltip.show(mutatePriority)
}

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
