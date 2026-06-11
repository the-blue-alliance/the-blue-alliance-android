package com.thebluealliance.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.theme.TBAIndigo400

data class SectionHeaderInfo(
    val key: String,
    val label: String,
    val itemIndex: Int,
)

@Composable
fun SectionHeader(
    label: String,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    SectionHeaderRow(
        label = label,
        modifier = modifier.background(TBAIndigo400),
        trailingContent = trailingContent,
    )
}

@Composable
fun SectionHeader(
    label: String,
    isStuck: Boolean,
    allHeaders: List<SectionHeaderInfo>,
    onHeaderSelected: (SectionHeaderInfo) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(TBAIndigo400)
                .clickable(enabled = isStuck) { menuExpanded = true },
    ) {
        SectionHeaderRow(
            label = label,
            trailingContent =
                if (isStuck) {
                    {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Jump to section",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                } else {
                    null
                },
        )

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            allHeaders.forEach { info ->
                val isSelected = info.label == label
                DropdownMenuItem(
                    text = {
                        Text(
                            text = info.label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onHeaderSelected(info)
                    },
                )
            }
        }
    }
}

// Background lives on the clickable container in the interactive overload, so the
// ripple indication draws above the fill instead of being painted over by it.
@Composable
private fun SectionHeaderRow(
    label: String,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )
        trailingContent?.invoke(this)
    }
}
