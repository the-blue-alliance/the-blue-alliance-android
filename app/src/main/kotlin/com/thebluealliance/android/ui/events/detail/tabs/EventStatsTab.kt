package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.TeamStats
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox

enum class StatsSortBy {
    OPR, DPR, CCWM, TEAM
}

@Composable
fun EventStatsTab(
    stats: List<TeamStats>?,
    onTeamClick: (String) -> Unit = {},
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (stats == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }
    if (stats.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No stats available",
        )
        return
    }

    var sortBy by remember { mutableStateOf(StatsSortBy.OPR) }
    var menuExpanded by remember { mutableStateOf(false) }

    // Stats list
    val sorted = when (sortBy) {
        StatsSortBy.OPR -> stats.sortedByDescending { it.opr }
        StatsSortBy.DPR -> stats.sortedBy { it.dpr }
        StatsSortBy.CCWM -> stats.sortedByDescending { it.ccwm }
        StatsSortBy.TEAM -> stats.sortedBy { it.teamKey.removePrefix("frc").toIntOrNull() ?: Int.MAX_VALUE }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        // Header with sort menu - FIXED at top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Team",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.25f),
            )
            Text(
                text = "OPR",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.25f),
            )
            Text(
                text = "DPR",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.25f),
            )
            Text(
                text = "CCWM",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.25f),
            )
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Sort options",
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Sort by OPR") },
                        onClick = {
                            sortBy = StatsSortBy.OPR
                            menuExpanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by DPR") },
                        onClick = {
                            sortBy = StatsSortBy.DPR
                            menuExpanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by CCWM") },
                        onClick = {
                            sortBy = StatsSortBy.CCWM
                            menuExpanded = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by Team") },
                        onClick = {
                            sortBy = StatsSortBy.TEAM
                            menuExpanded = false
                        },
                    )
                }
            }
        }

        // Scrollable stats list - GROWS to fill remaining space
        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            items(sorted, key = { it.teamKey }) { teamStat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTeamClick(teamStat.teamKey) }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = teamStat.teamKey.removePrefix("frc"),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(0.25f),
                    )
                    Text(
                        text = "%.2f".format(teamStat.opr),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.25f),
                    )
                    Text(
                        text = "%.2f".format(teamStat.dpr),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.25f),
                    )
                    Text(
                        text = "%.2f".format(teamStat.ccwm),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.25f),
                    )
                }
            }
        }
    }
}


