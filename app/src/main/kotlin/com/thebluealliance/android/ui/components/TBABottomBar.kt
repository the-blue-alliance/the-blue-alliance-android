package com.thebluealliance.android.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS
import com.thebluealliance.android.ui.theme.TBABlue

@Composable
fun TBABottomBar(
    modifier: Modifier = Modifier,
    currentRoute: NavKey,
    onNavigate: (NavKey) -> Unit,
    onReselect: () -> Unit,
) {
    val moreSubScreens = setOf(Screen.MyTBA, Screen.Settings, Screen.About, Screen.Thanks)
    val isOnMoreSubScreen = currentRoute in moreSubScreens

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { dest ->
            val selected = currentRoute == dest.key || (dest.key == Screen.More && isOnMoreSubScreen)
            NavigationBarItem(
                selected = selected,
                onClick = dropUnlessResumed {
                    if (selected) {
                        onReselect()
                    } else {
                        onNavigate(dest.key)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                        contentDescription = dest.label,
                    )
                },
                label = { Text(dest.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = TBABlue,
                    indicatorColor = TBABlue,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}
