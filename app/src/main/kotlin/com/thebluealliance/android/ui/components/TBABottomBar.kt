package com.thebluealliance.android.ui.components

import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS

/** Smallest a bottom-bar label shrinks to so it stays on one line at large font scales. */
private val MIN_LABEL_FONT_SIZE = 8.sp

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
                    // One line at every font scale: the bar is a fixed height, so the longest
                    // label steps down in size rather than wrapping mid-word.
                    Text(
                        text = dest.label,
                        maxLines = 1,
                        // At the smallest step a narrow split screen can still be too tight.
                        overflow = TextOverflow.Ellipsis,
                        autoSize =
                            TextAutoSize.StepBased(
                                minFontSize = MIN_LABEL_FONT_SIZE,
                                maxFontSize = LocalTextStyle.current.fontSize,
                            ),
                    )
                },
                modifier = Modifier.handCursor(),
            )
        }
    }
}
