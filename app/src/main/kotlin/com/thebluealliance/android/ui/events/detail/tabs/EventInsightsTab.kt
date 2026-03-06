package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.EventCOPRs
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.ui.common.LoadingBox

sealed class StatType {
    object StandardOPRs : StatType()
    data class COPR(val statName: String) : StatType()
}

enum class OprSortColumn {
    TEAM, OPR, DPR, CCWM
}

enum class CoprSortColumn {
    TEAM, VALUE
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventInsightsTab(
    oprs: EventOPRs?,
    coprs: EventCOPRs?,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (oprs == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }

    val uriHandler = LocalUriHandler.current
    var oprSortColumn by remember { mutableStateOf(OprSortColumn.OPR) }
    var oprSortAscending by remember { mutableStateOf(false) }
    var coprSortColumn by remember { mutableStateOf(CoprSortColumn.VALUE) }
    var coprSortAscending by remember { mutableStateOf(false) }
    var showStatSelector by remember { mutableStateOf(false) }
    var selectedStatType by remember { mutableStateOf<StatType>(StatType.StandardOPRs) }

    // Get available COPR stat names
    val coprStatNames = remember(coprs) {
        coprs?.coprs?.keys?.sorted() ?: emptyList()
    }

    if (showStatSelector) {
        StatSelectorDialog(
            currentSelection = selectedStatType,
            availableCoprStats = coprStatNames,
            onDismiss = { showStatSelector = false },
            onSelect = { statType ->
                selectedStatType = statType
                showStatSelector = false
                if (statType is StatType.COPR) {
                    coprSortColumn = CoprSortColumn.VALUE
                    coprSortAscending = false
                }
            }
        )
    }

    val layoutDirection = LocalLayoutDirection.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(layoutDirection),
                end = innerPadding.calculateEndPadding(layoutDirection)
            )
    ) {
        when (val statType = selectedStatType) {
            is StatType.StandardOPRs -> {
                StandardOPRsView(
                    oprs = oprs,
                    sortColumn = oprSortColumn,
                    sortAscending = oprSortAscending,
                    onSortChange = { column, ascending ->
                        oprSortColumn = column
                        oprSortAscending = ascending
                    },
                    onShowStatSelector = { showStatSelector = true },
                    innerPadding = innerPadding,
                    uriHandler = uriHandler,
                )
            }
            is StatType.COPR -> {
                val coprData = coprs?.coprs?.get(statType.statName) ?: emptyMap()
                COPRView(
                    statName = statType.statName,
                    coprData = coprData,
                    sortColumn = coprSortColumn,
                    sortAscending = coprSortAscending,
                    onSortChange = { column, ascending ->
                        coprSortColumn = column
                        coprSortAscending = ascending
                    },
                    onShowStatSelector = { showStatSelector = true },
                    innerPadding = innerPadding,
                    uriHandler = uriHandler,
                )
            }
        }
    }
}

@Composable
private fun StatSelectorDialog(
    currentSelection: StatType,
    availableCoprStats: List<String>,
    onDismiss: () -> Unit,
    onSelect: (StatType) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Stat Type") },
        text = {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(StatType.StandardOPRs) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSelection is StatType.StandardOPRs,
                            onClick = { onSelect(StatType.StandardOPRs) }
                        )
                        Text(
                            text = "OPRs (OPR / DPR / CCWM)",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                if (availableCoprStats.isNotEmpty()) {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "Component OPRs",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(availableCoprStats) { statName ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(StatType.COPR(statName)) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentSelection is StatType.COPR && currentSelection.statName == statName,
                                onClick = { onSelect(StatType.COPR(statName)) }
                            )
                            Text(
                                text = statName,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StandardOPRsView(
    oprs: EventOPRs,
    sortColumn: OprSortColumn,
    sortAscending: Boolean,
    onSortChange: (OprSortColumn, Boolean) -> Unit,
    onShowStatSelector: () -> Unit,
    innerPadding: PaddingValues,
    uriHandler: androidx.compose.ui.platform.UriHandler,
) {
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
        contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
    ) {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF5C6BC0))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OprHeaderItem(
                    text = "Team",
                    modifier = Modifier.weight(1.2f),
                    sortColumn = OprSortColumn.TEAM,
                    currentSort = sortColumn,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == OprSortColumn.TEAM) {
                            onSortChange(OprSortColumn.TEAM, !sortAscending)
                        } else {
                            onSortChange(OprSortColumn.TEAM, true)
                        }
                    }
                )
                OprHeaderItem(
                    text = "OPR",
                    modifier = Modifier.weight(1f),
                    sortColumn = OprSortColumn.OPR,
                    currentSort = sortColumn,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == OprSortColumn.OPR) {
                            onSortChange(OprSortColumn.OPR, !sortAscending)
                        } else {
                            onSortChange(OprSortColumn.OPR, false)
                        }
                    }
                )
                OprHeaderItem(
                    text = "DPR",
                    modifier = Modifier.weight(1f),
                    sortColumn = OprSortColumn.DPR,
                    currentSort = sortColumn,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == OprSortColumn.DPR) {
                            onSortChange(OprSortColumn.DPR, !sortAscending)
                        } else {
                            onSortChange(OprSortColumn.DPR, false)
                        }
                    }
                )
                OprHeaderItem(
                    text = "CCWM",
                    modifier = Modifier.weight(1f),
                    sortColumn = OprSortColumn.CCWM,
                    currentSort = sortColumn,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == OprSortColumn.CCWM) {
                            onSortChange(OprSortColumn.CCWM, !sortAscending)
                        } else {
                            onSortChange(OprSortColumn.CCWM, false)
                        }
                    }
                )
                IconButton(
                    onClick = onShowStatSelector,
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Select stat type",
                        tint = Color.White
                    )
                }
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
                    Box(modifier = Modifier.weight(0.4f))
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }

        item {
            Text(
                text = "Learn more about OPR",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { uriHandler.openUri("https://www.thebluealliance.com/opr") }
                    .padding(16.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun COPRView(
    statName: String,
    coprData: Map<String, Double>,
    sortColumn: CoprSortColumn,
    sortAscending: Boolean,
    onSortChange: (CoprSortColumn, Boolean) -> Unit,
    onShowStatSelector: () -> Unit,
    innerPadding: PaddingValues,
    uriHandler: androidx.compose.ui.platform.UriHandler,
) {
    val teamKeys = coprData.keys.toList()

    val sortedTeams = remember(coprData, sortColumn, sortAscending) {
        teamKeys.sortedWith { a, b ->
            val result = when (sortColumn) {
                CoprSortColumn.TEAM -> {
                    val teamA = a.substring(3).toIntOrNull() ?: 0
                    val teamB = b.substring(3).toIntOrNull() ?: 0
                    teamA.compareTo(teamB)
                }
                CoprSortColumn.VALUE -> (coprData[a] ?: 0.0).compareTo(coprData[b] ?: 0.0)
            }
            if (sortAscending) result else -result
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
    ) {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF5C6BC0))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoprHeaderItem(
                    text = "Team",
                    modifier = Modifier.weight(1.2f),
                    sortColumn = CoprSortColumn.TEAM,
                    currentSort = sortColumn,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == CoprSortColumn.TEAM) {
                            onSortChange(CoprSortColumn.TEAM, !sortAscending)
                        } else {
                            onSortChange(CoprSortColumn.TEAM, true)
                        }
                    }
                )
                CoprHeaderItem(
                    text = statName,
                    modifier = Modifier.weight(2f),
                    sortColumn = CoprSortColumn.VALUE,
                    currentSort = sortColumn,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == CoprSortColumn.VALUE) {
                            onSortChange(CoprSortColumn.VALUE, !sortAscending)
                        } else {
                            onSortChange(CoprSortColumn.VALUE, false)
                        }
                    }
                )
                IconButton(
                    onClick = onShowStatSelector,
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Select stat type",
                        tint = Color.White
                    )
                }
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
                        text = "%.2f".format(coprData[teamKey] ?: 0.0),
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }

        item {
            Text(
                text = "Learn more about OPR",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { uriHandler.openUri("https://www.thebluealliance.com/opr") }
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun OprHeaderItem(
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

@Composable
private fun CoprHeaderItem(
    text: String,
    modifier: Modifier = Modifier,
    sortColumn: CoprSortColumn,
    currentSort: CoprSortColumn,
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
