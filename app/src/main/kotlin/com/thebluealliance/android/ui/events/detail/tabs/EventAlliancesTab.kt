package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.displayTitle
import com.thebluealliance.android.domain.model.playoffSummary
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.util.teamNumber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventAlliancesTab(
    alliances: List<Alliance>?,
    onTeamClick: (teamKey: String) -> Unit = {},
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (alliances == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding),
        )
        return
    }
    if (alliances.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No alliances",
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        items(alliances, key = { "${it.eventKey}_${it.number}" }) { alliance ->
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
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
        modifier = Modifier.clickable { onTeamClick(teamKey) },
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
