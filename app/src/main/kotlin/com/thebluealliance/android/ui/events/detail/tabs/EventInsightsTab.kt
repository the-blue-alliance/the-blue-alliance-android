package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.EventCOPRs
import com.thebluealliance.android.domain.model.EventInsights
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.theme.TBAIndigo400
import com.thebluealliance.android.util.openUrl
import com.thebluealliance.android.util.teamNumber
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int

/**
 * Half of the gutter between two table columns, applied inside every cell of both the header and
 * the data rows so the columns stay aligned. The rows carry [TABLE_ROW_PADDING] instead of the
 * usual 16.dp so the outermost cells' content still lands on the screen's 16.dp margin.
 */
private val COLUMN_GUTTER = 4.dp

/** Row inset that, plus [COLUMN_GUTTER] inside the end cells, restores the 16.dp screen margin. */
private val TABLE_ROW_PADDING = 12.dp

/**
 * Width the header rows' overflow button occupies. The data rows reserve the same trailing slot,
 * so both rows share one weighted region and their columns cannot drift apart.
 */
private val OVERFLOW_SLOT_WIDTH = 48.dp + COLUMN_GUTTER

/**
 * One column of an insights table.
 *
 * The header cell and the data cells of a column are driven by the same [TableColumn], so a
 * column's width and alignment are decided in one place instead of being duplicated between the
 * two rows. A [numeric] column sets its values in tabular figures and right-aligns them so they
 * stack on the decimal point, and right-aligns its header label onto that same right edge (see
 * [TableHeaderCell] for why). A text column stays start-aligned throughout.
 */
private data class TableColumn(
    val weight: Float,
    val numeric: Boolean = false,
)

/** Every numeric cell shows two decimals, so right-aligned values line up digit for digit. */
private fun formatStatValue(value: Double): String = "%.2f".format(value)

/** OpenType tabular figures: every digit takes the same advance, so the columns grid up. */
private const val TABULAR_FIGURES = "tnum"

private val OPR_TEAM_COLUMN = TableColumn(weight = 1.2f)
private val OPR_VALUE_COLUMN = TableColumn(weight = 1f, numeric = true)

private val COPR_TEAM_COLUMN = TableColumn(weight = 1.2f)
private val COPR_VALUE_COLUMN = TableColumn(weight = 2f, numeric = true)

sealed class StatType {
    object StandardOPRs : StatType()

    object QualInsights : StatType()

    object PlayoffInsights : StatType()

    data class COPR(
        val statName: String,
    ) : StatType()
}

enum class OprSortColumn {
    TEAM,
    OPR,
    DPR,
    CCWM,
}

enum class CoprSortColumn {
    TEAM,
    VALUE,
}

private val StatTypeSaver =
    listSaver<StatType, String>(
        save = {
            when (it) {
                StatType.StandardOPRs -> listOf("StandardOPRs")
                StatType.QualInsights -> listOf("QualInsights")
                StatType.PlayoffInsights -> listOf("PlayoffInsights")
                is StatType.COPR -> listOf("COPR", it.statName)
            }
        },
        restore = {
            when (it[0]) {
                "QualInsights" -> StatType.QualInsights
                "PlayoffInsights" -> StatType.PlayoffInsights
                "COPR" -> StatType.COPR(it[1])
                else -> StatType.StandardOPRs
            }
        },
    )

private inline fun <reified T : Enum<T>> enumSaver(): Saver<T, String> =
    Saver(
        save = { it.name },
        restore = { enumValueOf<T>(it) },
    )

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
    var oprSortColumn by rememberSaveable(stateSaver = enumSaver<OprSortColumn>()) {
        mutableStateOf(OprSortColumn.OPR)
    }
    var oprSortAscending by rememberSaveable { mutableStateOf(false) }
    var coprSortColumn by rememberSaveable(stateSaver = enumSaver<CoprSortColumn>()) {
        mutableStateOf(CoprSortColumn.VALUE)
    }
    var coprSortAscending by rememberSaveable { mutableStateOf(false) }
    var showStatSelector by rememberSaveable { mutableStateOf(false) }
    val defaultStatType =
        when {
            hasOprData -> StatType.StandardOPRs
            hasInsightsData && insights.qual != null -> StatType.QualInsights
            hasInsightsData && insights.playoff != null -> StatType.PlayoffInsights
            hasCoprData -> StatType.COPR(coprs.coprs.keys.first())
            else -> StatType.StandardOPRs
        }
    var selectedStatType by rememberSaveable(stateSaver = StatTypeSaver) {
        mutableStateOf(defaultStatType)
    }

    // Get available COPR stat names
    val coprStatNames =
        remember(coprs) {
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
            },
        )
    }

    val layoutDirection = LocalLayoutDirection.current
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                ),
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
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(StatType.StandardOPRs) }
                                .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = currentSelection is StatType.StandardOPRs,
                            onClick = { onSelect(StatType.StandardOPRs) },
                        )
                        Text(
                            text = "OPRs (OPR / DPR / CCWM)",
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }

                item {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(StatType.QualInsights) }
                                .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = currentSelection is StatType.QualInsights,
                            onClick = { onSelect(StatType.QualInsights) },
                        )
                        Text(
                            text = "Qual Insights",
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }

                item {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(StatType.PlayoffInsights) }
                                .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = currentSelection is StatType.PlayoffInsights,
                            onClick = { onSelect(StatType.PlayoffInsights) },
                        )
                        Text(
                            text = "Playoff Insights",
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }

                if (availableCoprStats.isNotEmpty()) {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "Component OPRs",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }

                    items(availableCoprStats) { statName ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(StatType.COPR(statName)) }
                                    .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected =
                                    currentSelection is StatType.COPR &&
                                        currentSelection.statName == statName,
                                onClick = { onSelect(StatType.COPR(statName)) },
                            )
                            Text(
                                text = statName,
                                modifier = Modifier.padding(start = 8.dp),
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
    val insightHeader =
        when (title) {
            "Qual Insights" -> "Qual Insight"
            "Playoff Insights" -> "Playoff Insight"
            else -> "Insight"
        }

    if (insightsData == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("No $title data available")
        }
        return
    }

    val insightsList =
        remember(insightsData) {
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
        // Left as it was: both of this table's columns hold prose, not figures, and they are
        // wide enough that spending COLUMN_GUTTER on each edge would wrap values that fit today.
        stickyHeader {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(TBAIndigo400)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = insightHeader,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.weight(1.5f),
                )
                Text(
                    text = "Value",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onShowStatSelector,
                    modifier = Modifier.padding(0.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Select stat type",
                        tint = Color.White,
                    )
                }
            }
        }

        items(insightsList) { insight ->
            Column {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = insight.name,
                        modifier = Modifier.weight(1.5f),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = insight.value,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
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
                modifier =
                    Modifier
                        .clickable { onOpenOprLink() }
                        .padding(16.dp),
            )
        }
    }
}

data class InsightItem(
    val name: String,
    val value: String,
)

private fun parseInsightsData(jsonObject: JsonObject): List<InsightItem> {
    val items = mutableListOf<InsightItem>()

    for ((key, value) in jsonObject) {
        val formattedName = formatStatName(key)

        when (value) {
            is JsonArray -> {
                if ((key.endsWith("_count") || key.endsWith("_conversion")) && value.size >= 3) {
                    // Format as: success / opportunities (percentage%)
                    val success = (value[0] as? JsonPrimitive)?.int ?: 0
                    val opportunities = (value[1] as? JsonPrimitive)?.int ?: 0
                    val percentage = (value[2] as? JsonPrimitive)?.doubleOrNull ?: 0.0
                    items.add(
                        InsightItem(
                            formattedName,
                            "$success / $opportunities (%.1f%%)".format(percentage),
                        ),
                    )
                } else if (key == "high_score" && value.size >= 3) {
                    // Format as: score (match)
                    val score = (value[0] as? JsonPrimitive)?.int ?: 0
                    val matchName = (value[2] as? JsonPrimitive)?.content ?: ""
                    items.add(InsightItem(formattedName, "$score ($matchName)"))
                } else if (value.size == 3) {
                    // Format as: event total / alliance avg / team avg
                    val total = (value[0] as? JsonPrimitive)?.doubleOrNull ?: 0.0
                    val allianceAvg = (value[1] as? JsonPrimitive)?.doubleOrNull ?: 0.0
                    val teamAvg = (value[2] as? JsonPrimitive)?.doubleOrNull ?: 0.0
                    val totalStr =
                        if (total % 1.0 ==
                            0.0
                        ) {
                            total.toInt().toString()
                        } else {
                            "%.2f".format(total)
                        }
                    items.add(
                        InsightItem(
                            formattedName,
                            "$totalStr total / ${"%.2f".format(
                                allianceAvg,
                            )} alliance / ${"%.2f".format(teamAvg)} team",
                        ),
                    )
                } else {
                    // Generic array formatting
                    items.add(InsightItem(formattedName, value.toString()))
                }
            }
            is JsonPrimitive -> {
                val formattedValue =
                    when {
                        value.isString -> value.content
                        else -> {
                            val num = value.doubleOrNull ?: 0.0
                            if (num % 1.0 == 0.0) {
                                num.toInt().toString()
                            } else {
                                "%.2f".format(num)
                            }
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

private fun formatStatName(key: String): String =
    key
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
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
    val teamKeys =
        oprs.oprs.keys
            .union(oprs.dprs.keys)
            .union(oprs.ccwms.keys)
            .toList()

    val sortedTeams =
        remember(oprs, sortColumn, sortAscending) {
            teamKeys.sortedWith { a, b ->
                val result =
                    when (sortColumn) {
                        OprSortColumn.TEAM -> {
                            val teamA = a.teamNumber.toIntOrNull() ?: 0
                            val teamB = b.teamNumber.toIntOrNull() ?: 0
                            teamA.compareTo(teamB)
                        }
                        OprSortColumn.OPR -> (oprs.oprs[a] ?: 0.0).compareTo(oprs.oprs[b] ?: 0.0)
                        OprSortColumn.DPR -> (oprs.dprs[a] ?: 0.0).compareTo(oprs.dprs[b] ?: 0.0)
                        OprSortColumn.CCWM -> (oprs.ccwms[a] ?: 0.0).compareTo(oprs.ccwms[b] ?: 0.0)
                    }
                if (sortAscending) result else -result
            }
        }

    // Numeric columns all sort descending first — the interesting end of an OPR is the top.
    val onValueSortClick: (OprSortColumn) -> Unit = { column ->
        if (sortColumn == column) {
            onSortChange(column, !sortAscending)
        } else {
            onSortChange(column, false)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
    ) {
        stickyHeader {
            InsightsTableHeaderRow(onShowStatSelector = onShowStatSelector) {
                TableHeaderCell(
                    text = "Team",
                    column = OPR_TEAM_COLUMN,
                    sorted = sortColumn == OprSortColumn.TEAM,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == OprSortColumn.TEAM) {
                            onSortChange(OprSortColumn.TEAM, !sortAscending)
                        } else {
                            onSortChange(OprSortColumn.TEAM, true)
                        }
                    },
                )
                TableHeaderCell(
                    text = "OPR",
                    column = OPR_VALUE_COLUMN,
                    sorted = sortColumn == OprSortColumn.OPR,
                    ascending = sortAscending,
                    onSortClick = { onValueSortClick(OprSortColumn.OPR) },
                )
                TableHeaderCell(
                    text = "DPR",
                    column = OPR_VALUE_COLUMN,
                    sorted = sortColumn == OprSortColumn.DPR,
                    ascending = sortAscending,
                    onSortClick = { onValueSortClick(OprSortColumn.DPR) },
                )
                TableHeaderCell(
                    text = "CCWM",
                    column = OPR_VALUE_COLUMN,
                    sorted = sortColumn == OprSortColumn.CCWM,
                    ascending = sortAscending,
                    onSortClick = { onValueSortClick(OprSortColumn.CCWM) },
                )
            }
        }

        items(sortedTeams) { teamKey ->
            Column {
                InsightsTableDataRow {
                    TableDataCell(text = teamKey.teamNumber, column = OPR_TEAM_COLUMN)
                    TableDataCell(
                        text = formatStatValue(oprs.oprs[teamKey] ?: 0.0),
                        column = OPR_VALUE_COLUMN,
                    )
                    TableDataCell(
                        text = formatStatValue(oprs.dprs[teamKey] ?: 0.0),
                        column = OPR_VALUE_COLUMN,
                    )
                    TableDataCell(
                        text = formatStatValue(oprs.ccwms[teamKey] ?: 0.0),
                        column = OPR_VALUE_COLUMN,
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
                modifier =
                    Modifier
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

    val sortedTeams =
        remember(coprData, sortColumn, sortAscending) {
            teamKeys.sortedWith { a, b ->
                val result =
                    when (sortColumn) {
                        CoprSortColumn.TEAM -> {
                            val teamA = a.teamNumber.toIntOrNull() ?: 0
                            val teamB = b.teamNumber.toIntOrNull() ?: 0
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
            InsightsTableHeaderRow(onShowStatSelector = onShowStatSelector) {
                TableHeaderCell(
                    text = "Team",
                    column = COPR_TEAM_COLUMN,
                    sorted = sortColumn == CoprSortColumn.TEAM,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == CoprSortColumn.TEAM) {
                            onSortChange(CoprSortColumn.TEAM, !sortAscending)
                        } else {
                            onSortChange(CoprSortColumn.TEAM, true)
                        }
                    },
                )
                TableHeaderCell(
                    text = statName,
                    column = COPR_VALUE_COLUMN,
                    sorted = sortColumn == CoprSortColumn.VALUE,
                    ascending = sortAscending,
                    onSortClick = {
                        if (sortColumn == CoprSortColumn.VALUE) {
                            onSortChange(CoprSortColumn.VALUE, !sortAscending)
                        } else {
                            onSortChange(CoprSortColumn.VALUE, false)
                        }
                    },
                )
            }
        }

        items(sortedTeams) { teamKey ->
            Column {
                InsightsTableDataRow {
                    TableDataCell(text = teamKey.teamNumber, column = COPR_TEAM_COLUMN)
                    TableDataCell(
                        text = formatStatValue(coprData[teamKey] ?: 0.0),
                        column = COPR_VALUE_COLUMN,
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
                modifier =
                    Modifier
                        .clickable { onOpenOprLink() }
                        .padding(16.dp),
            )
        }
    }
}

/**
 * The header row shared by every insights table: the indigo band, the row's own inset, and the
 * trailing overflow button that opens the stat selector.
 */
@Composable
private fun InsightsTableHeaderRow(
    onShowStatSelector: () -> Unit,
    cells: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(TBAIndigo400)
                .padding(horizontal = TABLE_ROW_PADDING, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        cells()
        Box(
            // A fixed slot rather than a weighted one: the data rows below reserve the same width,
            // so both rows share one weighted region and their columns cannot drift apart. The end
            // padding keeps the button's outer edge on the 16.dp screen margin.
            modifier =
                Modifier
                    .width(OVERFLOW_SLOT_WIDTH)
                    .padding(end = COLUMN_GUTTER),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onShowStatSelector) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Select stat type",
                    tint = Color.White,
                )
            }
        }
    }
}

/** A data row of an insights table, reserving the same trailing slot as the header above it. */
@Composable
private fun InsightsTableDataRow(cells: @Composable RowScope.() -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = TABLE_ROW_PADDING, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        cells()
        Spacer(modifier = Modifier.width(OVERFLOW_SLOT_WIDTH))
    }
}

/**
 * A column's header cell. Pass [onSortClick] to make the column sortable.
 *
 * Clip + clickable sit outside the cell's own [COLUMN_GUTTER], so the sort target is a rounded
 * band spanning the whole column with the label inset from both its edges, instead of a highlight
 * glued to the glyphs. The header row's height is set by its 48.dp icon button, so the vertical
 * padding does not change the header's layout.
 *
 * A numeric column right-aligns its label onto the values' shared right edge (matching the data
 * cells below) and leads it with the sort carat, so the carat sits to the label's left and does not
 * disrupt that right edge. A text column keeps a start-aligned label with a trailing sort carat.
 *
 * Why right-align numerics but not the team column: Material 3 ships no data-table component, so
 * the governing spec is Material 2 — Data tables, "Text alignment": right-align numeric columns,
 * left-align text (https://m2.material.io/components/data-tables). The team number is left-aligned
 * because it is an identifier (nominal — a label made of digits, like a jersey number), not a
 * measured quantity scanned by magnitude, so the numeric rule does not apply to it. Leading the
 * sort carat on a right-aligned header follows Material Components' numeric header-cell + sort
 * handling (material-components-web `2139200`).
 */
@Composable
private fun RowScope.TableHeaderCell(
    text: String,
    column: TableColumn,
    sorted: Boolean = false,
    ascending: Boolean = false,
    onSortClick: (() -> Unit)? = null,
) {
    val sortTarget =
        if (onSortClick == null) {
            Modifier
        } else {
            Modifier.clip(MaterialTheme.shapes.small).clickable { onSortClick() }
        }
    Row(
        modifier =
            Modifier
                .weight(column.weight)
                .then(sortTarget)
                .padding(horizontal = COLUMN_GUTTER, vertical = 8.dp),
        horizontalArrangement = if (column.numeric) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (sorted && column.numeric) {
            SortIndicator(ascending)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )
        if (sorted && !column.numeric) {
            SortIndicator(ascending)
        }
    }
}

/** A column's data cell, aligned and styled from the same [TableColumn] as its header. */
@Composable
private fun RowScope.TableDataCell(
    text: String,
    column: TableColumn,
) {
    val style = MaterialTheme.typography.bodyLarge
    Text(
        text = text,
        modifier =
            Modifier
                .weight(column.weight)
                .padding(horizontal = COLUMN_GUTTER),
        style = if (column.numeric) style.copy(fontFeatureSettings = TABULAR_FIGURES) else style,
        textAlign = if (column.numeric) TextAlign.End else TextAlign.Start,
    )
}

@Composable
private fun SortIndicator(ascending: Boolean) {
    Icon(
        imageVector = if (ascending) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
        contentDescription = if (ascending) "Sorted Ascending" else "Sorted Descending",
        tint = Color.White,
    )
}
