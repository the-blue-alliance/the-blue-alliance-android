package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.common.SidewaysBringIntoView
import com.thebluealliance.android.ui.theme.BrandFocusRing
import com.thebluealliance.android.ui.theme.OnBrand
import com.thebluealliance.android.ui.theme.OnBrandMuted

@Composable
fun TBATabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
    BrandFocusRing(MaterialTheme.colorScheme.primaryContainer) {
        SidewaysBringIntoView {
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = modifier.verticalWheelScrollsSideways(scrollState),
                scrollState = scrollState,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = OnBrand,
                edgePadding = 0.dp,
                divider = {
                    HorizontalDivider(
                        thickness = TabRowDividerThickness,
                        color = TabRowDividerColor,
                    )
                },
                indicator = {
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                        height = 3.dp,
                        color = OnBrand,
                    )
                },
                tabs = tabs,
            )
        }
    }
}

private val TabRowDividerThickness = DividerDefaults.Thickness
private val TabRowDividerColor = OnBrand.copy(alpha = 0.12f)

/**
 * [TBATabRow] under content capped to the reading column, such as myTBA's account row: the band,
 * tabs, and indicator sit in the same column, start-aligned, and like [readingBand] the band and
 * its divider reach the window's edges while the margins beside the column are hidden. No wider
 * than the column, it lays out as [TBATabRow].
 */
@Composable
fun ReadingTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit,
) {
    ReadingBandBox(modifier) {
        TBATabRow(
            selectedTabIndex = selectedTabIndex,
            modifier =
                Modifier
                    .readingWidth()
                    .readingBand(MaterialTheme.colorScheme.primaryContainer)
                    .readingBandDivider(),
            tabs = tabs,
        )
    }
}

/** Continues the tab row's bottom divider across the [LocalReadingBandBleed] beside it. */
@Composable
private fun Modifier.readingBandDivider(): Modifier {
    val bleed = LocalReadingBandBleed.current
    // With no bleed the tab row's own divider already spans the row.
    if (bleed <= 0.dp) return this
    return drawBehind {
        val px = bleed.toPx()
        val thickness = TabRowDividerThickness.toPx()
        drawRect(
            TabRowDividerColor,
            topLeft = Offset(-px, size.height - thickness),
            size = Size(size.width + px * 2, thickness),
        )
    }
}

@Composable
fun TBATab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier.handCursor(),
        selectedContentColor = OnBrand,
        unselectedContentColor = OnBrandMuted,
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = label)
                trailing?.invoke()
            }
        },
    )
}
