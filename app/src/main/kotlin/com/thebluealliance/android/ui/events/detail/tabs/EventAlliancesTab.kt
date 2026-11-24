package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.displayTitle
import com.thebluealliance.android.domain.model.playoffSummary
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.paddedClickable
import com.thebluealliance.android.ui.components.tableInset
import com.thebluealliance.android.ui.components.tableWidth
import com.thebluealliance.android.util.teamNumber

@Composable
fun EventAlliancesTab(
    alliances: List<Alliance>?,
    onTeamClick: (teamKey: String) -> Unit = {},
    isRefreshing: Boolean = false,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (alliances == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding),
        )
        return
    }
    if (alliances.isEmpty()) {
        // An empty list while the first fetch is still in flight is loading, not empty.
        if (isRefreshing) {
            LoadingBox(modifier = Modifier.padding(innerPadding))
        } else {
            EmptyBox(modifier = Modifier.padding(innerPadding), message = "No alliances")
        }
        return
    }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val direction = LocalLayoutDirection.current
        val contentWidth =
            maxWidth - innerPadding.calculateStartPadding(direction) -
                innerPadding.calculateEndPadding(direction)
        if (alliancesUseGrid(contentWidth)) {
            // Two columns inside the table cap; the side margins stay part of the scrolling grid.
            val inset = tableInset(contentWidth)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding =
                    PaddingValues(
                        start = innerPadding.calculateStartPadding(direction) + inset,
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(direction) + inset,
                        bottom = innerPadding.calculateBottomPadding(),
                    ),
            ) {
                items(alliances, key = { "${it.eventKey}_${it.number}" }) { alliance ->
                    AllianceItem(alliance, onTeamClick, Modifier.fillMaxWidth())
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding,
            ) {
                items(alliances, key = { "${it.eventKey}_${it.number}" }) { alliance ->
                    AllianceItem(alliance, onTeamClick, Modifier.tableWidth())
                }
            }
        }
    }
}

/** Windows at least this wide lay alliances out two to a row. */
private val ALLIANCE_GRID_MIN_WIDTH = 840.dp

internal fun alliancesUseGrid(availableWidth: Dp): Boolean =
    availableWidth >= ALLIANCE_GRID_MIN_WIDTH

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AllianceItem(
    alliance: Alliance,
    onTeamClick: (teamKey: String) -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier =
            modifier
                // 8.dp here + 8.dp on each child keeps content on the 16.dp margin
                // while letting the pick slots' tap targets absorb the other half.
                .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = alliance.displayTitle,
                style = MaterialTheme.typography.titleSmall,
            )
            alliance.playoffSummary?.let { summary ->
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ) {
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
        FlowRow(
            modifier = Modifier.padding(top = 4.dp),
            // Slots carry their own 8.dp horizontal padding inside their tap target,
            // which supplies the 16.dp gap that used to come from the arrangement.
            horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            alliance.picks.forEachIndexed { index, teamKey ->
                AllianceSlot(
                    label = if (index == 0) "Captain" else "Pick $index",
                    teamKey = teamKey,
                    onTeamClick = onTeamClick,
                )
            }
            alliance.backupIn?.let { backupKey ->
                AllianceSlot(
                    label = "Backup",
                    teamKey = backupKey,
                    onTeamClick = onTeamClick,
                )
            }
        }
    }
}

@Composable
private fun AllianceSlot(
    label: String,
    teamKey: String,
    onTeamClick: (teamKey: String) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier.paddedClickable(
                MaterialTheme.shapes.small,
                horizontal = 8.dp,
                vertical = 4.dp,
                onClickLabel = "View team ${teamKey.teamNumber}",
            ) { onTeamClick(teamKey) },
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = teamKey.teamNumber,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
