package com.thebluealliance.android.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS

@Composable
fun TBABottomBar(
    currentRoute: NavKey,
    onNavigate: (NavKey) -> Unit,
    onReselect: () -> Unit,
) {
    val moreSubScreens = setOf(Screen.MyTBA, Screen.Settings, Screen.About, Screen.Thanks)
    val isOnMoreSubScreen = currentRoute in moreSubScreens

    NavigationBar {
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
            )
        }
    }
}
