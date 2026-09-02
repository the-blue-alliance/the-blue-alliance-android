package com.thebluealliance.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

private val HORIZONTAL_TARGET_PADDING = 12.dp

@Composable
fun TopBarYearPicker(
    selectedYear: Int,
    years: List<Int>,
    onYearSelected: (Int) -> Unit,
    title: @Composable () -> Unit,
) {
    if (years.isEmpty()) {
        title()
        return
    }

    var yearDropdownExpanded by remember { mutableStateOf(false) }
    val contentColor = LocalContentColor.current.copy(alpha = 0.85f)

    // The padding below grows the touch/hover target around the text; the matching negative
    // offset cancels its horizontal contribution so the title keeps the same left edge as
    // plain-Text top bar titles on other screens.
    Box(modifier = Modifier.offset(x = -HORIZONTAL_TARGET_PADDING)) {
        Column(
            modifier =
                Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { yearDropdownExpanded = true }
                    .padding(horizontal = HORIZONTAL_TARGET_PADDING, vertical = 4.dp),
        ) {
            title()
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedYear.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select year",
                    modifier = Modifier.size(16.dp),
                    tint = contentColor,
                )
            }
        }
        DropdownMenu(
            expanded = yearDropdownExpanded,
            onDismissRequest = { yearDropdownExpanded = false },
        ) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = { Text(year.toString()) },
                    onClick = {
                        onYearSelected(year)
                        yearDropdownExpanded = false
                    },
                )
            }
        }
    }
}
