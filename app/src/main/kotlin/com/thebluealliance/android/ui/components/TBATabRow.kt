package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        contentColor = Color.White,
        edgePadding = 0.dp,
        divider = {
            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
        },
        indicator = {
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                height = 3.dp,
                color = Color.White,
            )
        },
        tabs = tabs,
    )
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
        modifier = modifier,
        selectedContentColor = Color.White,
        unselectedContentColor = Color.White.copy(alpha = 0.7f),
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
