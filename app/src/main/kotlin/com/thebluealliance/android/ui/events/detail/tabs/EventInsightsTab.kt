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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.EventCOPRs
import com.thebluealliance.android.domain.model.EventInsights
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import com.thebluealliance.android.util.openUrl

sealed class StatType {
    object StandardOPRs : StatType()
    object QualInsights : StatType()
    object PlayoffInsights : StatType()
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
    insights: EventInsights?,
    isRefreshing: Boolean = false,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    val hasOprData = oprs != null && oprs.oprs.isNotEmpty()
    val hasCoprData = coprs != null && coprs.coprs.isNotEmpty()
    val hasInsightsData = insights?.qual != null || insights?.playoff != null
    val hasAnyData = hasOprData || hasCoprData || hasInsightsData

    if (!hasAnyData) {
        if (isRefreshing) {
            LoadingBox(modifier = Modifier.padding(innerPadding))
        } else {
            EmptyBox("No insights", modifier = Modifier.padding(innerPadding))
        }
        return
    }

    val context = LocalContext.current
    var oprSortColumn by remember { mutableStateOf(OprSortColumn.OPR) }
    var oprSortAscending by remember { mutableStateOf(false) }
    var coprSortColumn by remember { mutableStateOf(CoprSortColumn.VALUE) }
    var coprSortAscending by remember { mutableStateOf(false) }
    var showStatSelector by remember { mutableStateOf(false) }
    val defaultStatType = when {
        hasOprData -> StatType.StandardOPRs
        hasInsightsData && insights.qual != null -> StatType.QualInsights
        hasInsightsData && insights.playoff != null -> StatType.PlayoffInsights
        hasCoprData -> StatType.COPR(coprs.coprs.keys.first())
        else -> StatType.StandardOPRs
    }
    var selectedStatType by remember { mutableStateOf(defaultStatType) }

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
                    oprs = oprs ?: EventOPRs(),
                    sortColumn = oprSortColumn,
                    sortAscending = oprSortAscending,
                    onSortChange = { column, ascending ->
                        oprSortColumn = column
                        oprSortAscending = ascending
                    },
                    onShowStatSelector = { showStatSelector = true },
                    innerPadding = innerPadding,
                    onOpenOprLink = { context.openUrl("https://www.thebluealliance.com/opr") },
                )
            }
            is StatType.QualInsights -> {
                InsightsView(
                    title = "Qual Insights",
                    insightsData = insights?.qual,
                    onShowStatSelector = { showStatSelector = true },
                    innerPadding = innerPadding,
                    onOpenOprLink = { context.openUrl("https://www.thebluealliance.com/opr") },
                )
            }
            is StatType.PlayoffInsights -> {
                InsightsView(
                    title = "Playoff Insights",
                    insightsData = insights?.playoff,
                    onShowStatSelector = { showStatSelector = true },
                    innerPadding = innerPadding,
                    onOpenOprLink = { context.openUrl("https://www.thebluealliance.com/opr") },
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
                    onOpenOprLink = { context.openUrl("https://www.thebluealliance.com/opr") },
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

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(StatType.QualInsights) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSelection is StatType.QualInsights,
                            onClick = { onSelect(StatType.QualInsights) }
                        )
                        Text(
                            text = "Qual Insights",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(StatType.PlayoffInsights) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSelection is StatType.PlayoffInsights,
                            onClick = { onSelect(StatType.PlayoffInsights) }
                        )
                        Text(
                            text = "Playoff Insights",
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
private fun InsightsView(
    title: String,
    insightsData: String?,
    onShowStatSelector: () -> Unit,
    innerPadding: PaddingValues,
    onOpenOprLink: () -> Unit,
) {
    val insightHeader = when (title) {
        "Qual Insights" -> "Qual Insight"
        "Playoff Insights" -> "Playoff Insight"
        else -> "Insight"
    }

    if (insightsData == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No $title data available")
        }
        return
    }

    val insightsList = remember(insightsData) {
        try {
            val jsonElement = Json.parseToJsonElement(insightsData)
            val jsonObject = jsonElement as? JsonObject ?: return@remember emptyList()
            parseInsightsData(jsonObject)
        } catch (_: Exception) {
            emptyList()
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
                Text(
                    text = insightHeader,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    text = "Value",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
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

        items(insightsList) { insight ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = insight.name,
                        modifier = Modifier.weight(1.5f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = insight.value,
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
                    .clickable { onOpenOprLink() }
                    .padding(16.dp),
            )
        }
    }
}

data class InsightItem(val name: String, val value: String)

private fun parseInsightsData(jsonObject: JsonObject): List<InsightItem> {
    val items = mutableListOf<InsightItem>()

    for ((key, value) in jsonObject) {
        val formattedName = formatStatName(key)

        when (value) {
            is JsonArray -> {
                if (key.endsWith("_count") && value.size >= 3) {
                    // Format as: success / opportunities (percentage%)
                    val success = (value[0] as? JsonPrimitive)?.int ?: 0
                    val opportunities = (value[1] as? JsonPrimitive)?.int ?: 0
                    val percentage = (value[2] as? JsonPrimitive)?.doubleOrNull ?: 0.0
                    items.add(InsightItem(formattedName, "$success / $opportunities (%.1f%%)".format(percentage)))
                } else if (key == "high_score" && value.size >= 3) {
                    // Format as: score (match)
                    val score = (value[0] as? JsonPrimitive)?.int ?: 0
                    val matchName = (value[2] as? JsonPrimitive)?.content ?: ""
                    items.add(InsightItem(formattedName, "$score ($matchName)"))
                } else {
                    // Generic array formatting
                    items.add(InsightItem(formattedName, value.toString()))
                }
            }
            is JsonPrimitive -> {
                val formattedValue = when {
                    value.isString -> value.content
                    else -> {
                        val num = value.doubleOrNull ?: 0.0
                        if (num % 1.0 == 0.0) num.toInt().toString()
                        else "%.2f".format(num)
                    }
                }
                items.add(InsightItem(formattedName, formattedValue))
            }
            else -> {
                // Skip JsonObject and other types
            }
        }
    }

    return items
}

private fun formatStatName(key: String): String {
    return key
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
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
    onOpenOprLink: () -> Unit,
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
                    .clickable { onOpenOprLink() }
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
    onOpenOprLink: () -> Unit,
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
                    .clickable { onOpenOprLink() }
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
