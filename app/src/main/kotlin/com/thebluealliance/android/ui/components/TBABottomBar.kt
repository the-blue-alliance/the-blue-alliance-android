package com.thebluealliance.android.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS

@Composable
fun TBABottomBar(
    modifier: Modifier = Modifier,
    currentRoute: NavKey,
    onNavigate: (NavKey) -> Unit,
    onReselect: () -> Unit,
) {
    NavigationBar(
        modifier = modifier,
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { dest ->
            val selected = dest.isSelectedFor(currentRoute)
            NavigationBarItem(
                selected = selected,
                onClick = topLevelDestinationClick(dest, currentRoute, onNavigate, onReselect),
                icon = { TopLevelDestinationIcon(dest, selected) },
                label = {
                    // One line at the user's font size; ellipsizes rather than wraps or shrinks.
                    Text(
                        dest.label,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                modifier = Modifier.handCursor(),
            )
        }
    }
}
