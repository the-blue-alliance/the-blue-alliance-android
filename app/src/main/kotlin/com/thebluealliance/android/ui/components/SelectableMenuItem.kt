package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SelectableDropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import com.thebluealliance.android.ui.theme.SurfaceFocusRing
import com.thebluealliance.android.ui.theme.TbaIcons

/**
 * A menu that shows where you are, holding [SelectableMenuItem]s. It takes M3's standard menu
 * container colour and shape, which the items' own container matches.
 */
@Composable
fun SelectableMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = MenuDefaults.standaloneGroupShape,
        containerColor = MenuDefaults.groupStandardContainerColor,
    ) {
        SurfaceFocusRing { content() }
    }
}

/**
 * One entry of a [SelectableMenu], such as the year picker or a section jump menu.
 *
 * It is M3's [SelectableDropdownMenuItem]: the current entry gets the selected container, shape
 * and a leading check, and TalkBack hears the menu as a radio group with that entry selected.
 * [index] and [count] place the item in the menu, which picks its corner shapes. When the menu
 * opens from the keyboard, focus starts on the current entry.
 */
@Composable
fun SelectableMenuItem(
    text: String,
    selected: Boolean,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val inputModeManager = LocalInputModeManager.current
    if (selected) {
        LaunchedEffect(Unit) {
            if (!focusesOnOpen(selected, inputModeManager.inputMode)) return@LaunchedEffect
            // The menu's popup window takes a frame or two before it can hold focus.
            repeat(MAX_FOCUS_FRAMES) {
                withFrameNanos {}
                if (focusRequester.requestFocus()) return@LaunchedEffect
            }
        }
    }
    SelectableDropdownMenuItem(
        selected = selected,
        onClick = onClick,
        text = { Text(text) },
        shapes = MenuDefaults.itemShape(index, count),
        modifier = modifier.focusRequester(focusRequester).handCursor(),
        selectedLeadingIcon = {
            Icon(
                imageVector = TbaIcons.Check,
                contentDescription = null,
                modifier = Modifier.size(MenuDefaults.LeadingIconSize),
            )
        },
    )
}

/** Whether a menu item takes focus as its menu opens: the current one, opened from the keyboard. */
internal fun focusesOnOpen(
    selected: Boolean,
    inputMode: InputMode,
): Boolean = selected && inputMode == InputMode.Keyboard

private const val MAX_FOCUS_FRAMES = 10
