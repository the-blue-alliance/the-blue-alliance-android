package com.thebluealliance.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.R
import com.thebluealliance.android.ui.theme.TBABlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TBATopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: androidx.compose.foundation.layout.WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = TBABlue,
        titleContentColor = Color.White,
        navigationIconContentColor = Color.White,
        actionIconContentColor = Color.White,
    ),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    showLamp: Boolean = false,
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = if (showLamp) {
            {
                // Use IconButton container (48dp) to match back button width in detail views,
                // so title text shares a consistent left edge across all screens.
                IconButton(onClick = {}, enabled = false) {
                    Icon(
                        painter = painterResource(id = R.drawable.tba_lamp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White,
                    )
                }
            }
        } else {
            navigationIcon
        },
        actions = actions,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior,
    )
}
