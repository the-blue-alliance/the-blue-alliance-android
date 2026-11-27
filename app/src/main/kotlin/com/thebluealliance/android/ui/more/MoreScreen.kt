package com.thebluealliance.android.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.PlatinumSponsor
import com.thebluealliance.android.ui.components.SmallTextLinkVerticalPadding
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarTitle
import com.thebluealliance.android.ui.components.TopLevelContentWindowInsets
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.components.paddedClickable
import com.thebluealliance.android.ui.components.readingWidth
import com.thebluealliance.android.ui.components.returnsFocus
import com.thebluealliance.android.ui.components.withoutBottom
import com.thebluealliance.android.ui.theme.TbaIcons

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MoreScreen(
    onNavigateToMyTBA: () -> Unit,
    onNavigateToRegionalAdvancement: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToThanks: () -> Unit,
    onNavigateToSearch: () -> Unit,
    sponsors: List<PlatinumSponsor> = emptyList(),
) {
    Scaffold(
        contentWindowInsets = TopLevelContentWindowInsets,
        topBar = {
            TBATopAppBar(
                title = { TopBarTitle("More") },
                secondaryActions =
                    listOf(
                        TopBarAction("Search", TbaIcons.Search, onNavigateToSearch),
                    ),
                showLamp = true,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding.withoutBottom())
                    .readingWidth()
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        ) {
            MoreItem(icon = TbaIcons.Star, label = "myTBA", onClick = onNavigateToMyTBA)
            HorizontalDivider()
            MoreItem(
                icon = TbaIcons.EmojiEvents,
                label = "Regional Advancement",
                onClick = onNavigateToRegionalAdvancement,
            )
            HorizontalDivider()
            MoreItem(
                icon = TbaIcons.Settings,
                label = "Settings",
                onClick = onNavigateToSettings,
            )
            HorizontalDivider()

            Spacer(modifier = Modifier.weight(1f))

            // Each link carries its own 8.dp inset inside the tap target, so the row's own
            // padding drops by the same amount and the separator's spaces are no longer needed.
            // The links' own vertical padding makes them 48dp targets, replacing the row's top gap.
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier.paddedClickable(
                            MaterialTheme.shapes.small,
                            horizontal = 8.dp,
                            vertical = SmallTextLinkVerticalPadding,
                            onClickLabel = "Open About",
                            onClick = onNavigateToAbout,
                        ),
                )
                Text(
                    text = "·",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Text(
                    text = "Thanks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier.paddedClickable(
                            MaterialTheme.shapes.small,
                            horizontal = 8.dp,
                            vertical = SmallTextLinkVerticalPadding,
                            onClickLabel = "Open Thanks",
                            onClick = onNavigateToThanks,
                        ),
                )
                Text(
                    text = "·",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Text(
                    text = versionLabel(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            SponsorCredits(sponsors = sponsors, modifier = Modifier.padding(top = 8.dp))

            // The footer clears the gesture bar; the column's background runs on behind it.
            Spacer(modifier = Modifier.height(12.dp + innerPadding.calculateBottomPadding()))
        }
    }
}

@Composable
private fun MoreItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .returnsFocus()
                .handCursor()
                .clickable(onClickLabel = "Open $label", onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}
