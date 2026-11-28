package com.thebluealliance.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.theme.BrandFocusRing
import com.thebluealliance.android.ui.theme.BrandHeaderContainer
import com.thebluealliance.android.ui.theme.OnBrand
import com.thebluealliance.android.ui.theme.OnBrandRipple
import com.thebluealliance.android.ui.theme.SurfaceFocusRing
import com.thebluealliance.android.ui.theme.TbaIcons

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
        modifier = modifier.readingBand(BrandHeaderContainer),
        trailingContent = trailingContent,
    )
}

/** Whether [info] is the entry for the header labelled [label], which its menu marks. */
internal fun isCurrentSection(
    info: SectionHeaderInfo,
    label: String,
): Boolean = info.label == label

/**
 * A section header that opens a menu to jump to any of [allHeaders], wherever it is in the list,
 * and always shows the menu's arrow. [isStuck] marks the header pinned at the top, the only one
 * that takes keyboard focus. [clickLabel] is what activating it does, read by screen readers.
 */
@Composable
fun SectionHeader(
    label: String,
    isStuck: Boolean,
    allHeaders: List<SectionHeaderInfo>,
    onHeaderSelected: (SectionHeaderInfo) -> Unit,
    modifier: Modifier = Modifier,
    clickLabel: String = "Jump to section",
) {
    var menuExpanded by remember { mutableStateOf(false) }

    BrandFocusRing(BrandHeaderContainer) {
        Box(
            modifier =
                modifier
                    .fillMaxWidth()
                    .readingBand(BrandHeaderContainer)
                    // Only the stuck header takes keyboard focus, so Tab meets one header before
                    // the rows (see tabOrderedStickyHeader); its menu reaches every section.
                    .focusProperties { canFocus = isStuck }
                    .handCursor()
                    .clickable(
                        interactionSource = null,
                        indication = OnBrandRipple,
                        onClickLabel = clickLabel,
                        role = Role.DropdownList,
                    ) { menuExpanded = true },
        ) {
            SectionHeaderRow(
                label = label,
                trailingContent = {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = TbaIcons.ArrowDropDown,
                        contentDescription = null,
                        tint = OnBrand,
                        modifier = Modifier.size(20.dp),
                    )
                },
            )

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                SurfaceFocusRing {
                    allHeaders.forEach { info ->
                        val isSelected = isCurrentSection(info, label)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = info.label,
                                    fontWeight =
                                        if (isSelected) FontWeight.Bold else FontWeight.Normal,
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
                            modifier = Modifier.handCursor().selectedIfCurrent(isSelected),
                        )
                    }
                }
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
            color = OnBrand,
            // Not fill: a short label keeps the trailing content beside it, and a long one wraps
            // rather than pushing that content off the row.
            modifier = Modifier.weight(1f, fill = false).semantics { heading() },
        )
        trailingContent?.invoke(this)
    }
}
