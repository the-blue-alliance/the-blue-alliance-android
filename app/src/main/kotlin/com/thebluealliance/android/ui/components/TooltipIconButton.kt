package com.thebluealliance.android.ui.components

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.CoroutineScope
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
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()
    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
        tooltip = { PlainTooltip { Text(label) } },
        state = tooltipState,
    ) {
        IconButton(
            onClick = dismissingTooltip(tooltipState, scope, onClick),
            modifier = modifier.handCursor(),
        ) {
            Icon(icon, contentDescription = label, tint = tint)
        }
    }
}

/** [onClick], after hiding [tooltipState]'s tooltip if it's showing. */
@OptIn(ExperimentalMaterial3Api::class)
internal fun dismissingTooltip(
    tooltipState: TooltipState,
    scope: CoroutineScope,
    onClick: () -> Unit,
): () -> Unit =
    {
        if (tooltipState.isVisible) scope.launch { tooltipState.dismiss() }
        onClick()
    }
