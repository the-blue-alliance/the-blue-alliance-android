package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.ui.common.LoadingBox

enum class OprSortColumn {
    TEAM, OPR, DPR, CCWM
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventInsightsTab(
    oprs: EventOPRs?,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (oprs == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }

    var sortColumn by remember { mutableStateOf(OprSortColumn.OPR) }
    var sortAscending by remember { mutableStateOf(false) }

    val teamKeys = oprs.oprs.keys.union(oprs.dprs.keys).union(oprs.ccwms.keys).toList()

    val sortedTeams = remember(oprs, sortColumn, sortAscending) {
        teamKeys.sortedWith { a, b ->
            val result = when (sortColumn) {
                OprSortColumn.TEAM -> {
                    val teamA = a.substring(3).toIntOrNull() ?: 0
                    val teamB = b.substring(3).toIntOrNull() ?: 0
                    teamA.compareTo(teamB)
                }
                OprSortColumn.OPR -> (oprs.oprs[a] ?: 0.0).compareTo(oprs.oprs[b] ?: 0.0)
                OprSortColumn.DPR -> (oprs.dprs[a] ?: 0.0).compareTo(oprs.dprs[b] ?: 0.0)
                OprSortColumn.CCWM -> (oprs.ccwms[a] ?: 0.0).compareTo(oprs.ccwms[b] ?: 0.0)
            }
            if (sortAscending) result else -result
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF5C6BC0))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderItem(
                    text = "Team",
                    modifier = Modifier.weight(1.2f),
                    sortColumn = OprSortColumn.TEAM,
                    currentSort = sortColumn,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == OprSortColumn.TEAM) sortAscending = !sortAscending
                        else {
                            sortColumn = OprSortColumn.TEAM
                            sortAscending = true
                        }
                    }
                )
                HeaderItem(
                    text = "OPR",
                    modifier = Modifier.weight(1f),
                    sortColumn = OprSortColumn.OPR,
                    currentSort = sortColumn,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == OprSortColumn.OPR) sortAscending = !sortAscending
                        else {
                            sortColumn = OprSortColumn.OPR
                            sortAscending = false
                        }
                    }
                )
                HeaderItem(
                    text = "DPR",
                    modifier = Modifier.weight(1f),
                    sortColumn = OprSortColumn.DPR,
                    currentSort = sortColumn,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == OprSortColumn.DPR) sortAscending = !sortAscending
                        else {
                            sortColumn = OprSortColumn.DPR
                            sortAscending = false
                        }
                    }
                )
                HeaderItem(
                    text = "CCWM",
                    modifier = Modifier.weight(1f),
                    sortColumn = OprSortColumn.CCWM,
                    currentSort = sortColumn,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == OprSortColumn.CCWM) sortAscending = !sortAscending
                        else {
                            sortColumn = OprSortColumn.CCWM
                            sortAscending = false
                        }
                    }
                )
            }
        }

        items(sortedTeams) { teamKey ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = teamKey.substring(3),
                        modifier = Modifier.weight(1.2f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "%.2f".format(oprs.oprs[teamKey] ?: 0.0),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "%.2f".format(oprs.dprs[teamKey] ?: 0.0),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "%.2f".format(oprs.ccwms[teamKey] ?: 0.0),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun HeaderItem(
    text: String,
    modifier: Modifier = Modifier,
    sortColumn: OprSortColumn,
    currentSort: OprSortColumn,
    ascending: Boolean,
    onSortClick: () -> Unit
) {
    Row(
        modifier = modifier.clickable { onSortClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        if (currentSort == sortColumn) {
            Icon(
                imageVector = if (ascending) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = if (ascending) "Sorted Ascending" else "Sorted Descending",
                tint = Color.White
            )
        }
    }
}
