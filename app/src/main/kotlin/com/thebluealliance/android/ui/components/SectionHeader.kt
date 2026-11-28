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
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.theme.BrandFocusRing
import com.thebluealliance.android.ui.theme.BrandHeaderContainer
import com.thebluealliance.android.ui.theme.OnBrand
import com.thebluealliance.android.ui.theme.OnBrandRipple
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

/**
 * A section header that opens a menu to jump to any of [allHeaders], wherever it is in the list,
 * and always shows the menu's arrow. [isStuck] marks the header pinned at the top, the only one
 * that takes keyboard focus.
 */
@Composable
fun SectionHeader(
    label: String,
    isStuck: Boolean,
    allHeaders: List<SectionHeaderInfo>,
    onHeaderSelected: (SectionHeaderInfo) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    BrandFocusRing(BrandHeaderContainer) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .readingBand(BrandHeaderContainer)
                    // Only the stuck header takes keyboard focus, so Tab meets one header before
                    // the rows (see tabOrderedStickyHeader); its menu reaches every section.
                    .focusProperties { canFocus = isStuck }
                    .handCursor()
                    .clickable(
                        interactionSource = null,
                        indication = OnBrandRipple,
                        onClickLabel = "Jump to section",
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

            SelectableMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                allHeaders.forEachIndexed { index, info ->
                    SelectableMenuItem(
                        text = info.label,
                        selected = info.label == label,
                        index = index,
                        count = allHeaders.size,
                        onClick = {
                            menuExpanded = false
                            onHeaderSelected(info)
                        },
                    )
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
            modifier = Modifier.semantics { heading() },
        )
        trailingContent?.invoke(this)
    }
}
