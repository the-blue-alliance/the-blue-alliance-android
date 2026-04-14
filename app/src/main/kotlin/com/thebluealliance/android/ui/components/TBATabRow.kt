package com.thebluealliance.android.ui.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.ui.theme.TBABlue

@Composable
fun TBATabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit,
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = TBABlue,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        edgePadding = 0.dp,
        divider = {
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary)
        },
        indicator = {
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                height = 3.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        },
        tabs = tabs,
    )
}
