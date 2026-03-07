package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.RankingSortOrder
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import java.util.Locale

enum class RankingSortColumn {
    TEAM, PRIMARY, SECONDARY
}

internal data class RankingSortState(
    val column: RankingSortColumn = RankingSortColumn.PRIMARY,
    val ascending: Boolean = false,
)

internal fun rankingHeaderLabels(rankingSortOrders: List<RankingSortOrder>?): Pair<String, String> {
    val primaryLabel = rankingSortOrders?.getOrNull(0)?.name?.takeIf { it.isNotBlank() } ?: "RS"
    val secondaryLabel = rankingSortOrders?.getOrNull(1)?.name?.takeIf { it.isNotBlank() } ?: "Sort 2"
    return primaryLabel to secondaryLabel
}

internal fun nextRankingSortState(
    current: RankingSortState,
    selectedColumn: RankingSortColumn,
): RankingSortState {
    if (current.column == selectedColumn) {
        return current.copy(ascending = !current.ascending)
    }
    val ascending = when (selectedColumn) {
        RankingSortColumn.TEAM -> true
        RankingSortColumn.PRIMARY, RankingSortColumn.SECONDARY -> false
    }
    return RankingSortState(column = selectedColumn, ascending = ascending)
}

internal fun sortRankings(
    rankings: List<Ranking>,
    sortState: RankingSortState,
): List<Ranking> = rankings.sortedWith { a, b ->
    val result = when (sortState.column) {
        RankingSortColumn.TEAM -> {
            val teamA = a.teamKey.removePrefix("frc").toIntOrNull() ?: 0
            val teamB = b.teamKey.removePrefix("frc").toIntOrNull() ?: 0
            teamA.compareTo(teamB)
        }
        RankingSortColumn.PRIMARY -> {
            val valA = a.sortOrders.getOrNull(0) ?: 0.0
            val valB = b.sortOrders.getOrNull(0) ?: 0.0
            valA.compareTo(valB)
        }
        RankingSortColumn.SECONDARY -> {
            val valA = a.sortOrders.getOrNull(1) ?: 0.0
            val valB = b.sortOrders.getOrNull(1) ?: 0.0
            valA.compareTo(valB)
        }
    }
    if (sortState.ascending) result else -result
}

@Composable
fun EventRankingsTab(
    rankings: List<Ranking>?,
    rankingSortOrders: List<RankingSortOrder>?,
    rankingExtraStatsInfo: List<RankingSortOrder>?,
    onTeamClick: (String) -> Unit = {},
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (rankings == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }
    if (rankings.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No rankings",
        )
        return
    }

    val (primaryLabel, secondaryLabel) = rankingHeaderLabels(rankingSortOrders)

    var sortState by remember { mutableStateOf(RankingSortState()) }

    val sortedRankings = remember(rankings, sortState) {
        sortRankings(rankings, sortState)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        stickyHeader {
            RankingHeaderRow(
                primaryLabel = primaryLabel,
                secondaryLabel = secondaryLabel,
                sortState = sortState,
                onSortSelected = { selectedColumn ->
                    sortState = nextRankingSortState(sortState, selectedColumn)
                },
            )
        }
        items(sortedRankings, key = { "${it.eventKey}_${it.teamKey}" }) { ranking ->
            RankingItem(
                ranking = ranking,
                sortOrders = rankingSortOrders,
                extraStatsInfo = rankingExtraStatsInfo,
                onTeamClick = onTeamClick,
            )
        }
    }
}

@Composable
private fun RankingHeaderRow(
    primaryLabel: String,
    secondaryLabel: String,
    sortState: RankingSortState,
    onSortSelected: (RankingSortColumn) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF5C6BC0))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Rank",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(0.12f),
        )
        RankingHeaderItem(
            text = "Team",
            modifier = Modifier.weight(0.22f),
            sortColumn = RankingSortColumn.TEAM,
            currentSort = sortState.column,
            ascending = sortState.ascending,
            onSortClick = { onSortSelected(RankingSortColumn.TEAM) },
        )
        Text(
            text = "Record",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(0.22f),
        )
        RankingHeaderItem(
            text = primaryLabel,
            modifier = Modifier.weight(0.18f),
            sortColumn = RankingSortColumn.PRIMARY,
            currentSort = sortState.column,
            ascending = sortState.ascending,
            onSortClick = { onSortSelected(RankingSortColumn.PRIMARY) },
        )
        RankingHeaderItem(
            text = secondaryLabel,
            modifier = Modifier.weight(0.14f),
            sortColumn = RankingSortColumn.SECONDARY,
            currentSort = sortState.column,
            ascending = sortState.ascending,
            onSortClick = { onSortSelected(RankingSortColumn.SECONDARY) },
        )
        // Spacer for chevron alignment
        Spacer(modifier = Modifier.weight(0.12f))
    }
}

@Composable
private fun RankingHeaderItem(
    text: String,
    modifier: Modifier = Modifier,
    sortColumn: RankingSortColumn,
    currentSort: RankingSortColumn,
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
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
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
private fun RankingItem(
    ranking: Ranking,
    sortOrders: List<RankingSortOrder>?,
    extraStatsInfo: List<RankingSortOrder>?,
    onTeamClick: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron_rotation"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "#${ranking.rank}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.12f),
            )
            Text(
                text = ranking.teamKey.removePrefix("frc"),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(0.22f)
                    .clickable { onTeamClick(ranking.teamKey) },
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "${ranking.wins}-${ranking.losses}-${ranking.ties}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.22f),
            )

            // Show first two sort order values (without labels, header has them)
            val primarySortValue = ranking.sortOrders.getOrNull(0)?.let { value ->
                val precision = sortOrders?.getOrNull(0)?.precision ?: 2
                String.format(Locale.US, "%.${precision}f", value)
            } ?: "--"

            Text(
                text = primarySortValue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.18f),
            )

            val secondarySortValue = ranking.sortOrders.getOrNull(1)?.let { value ->
                val precision = sortOrders?.getOrNull(1)?.precision ?: 2
                String.format(Locale.US, "%.${precision}f", value)
            } ?: "--"

            Text(
                text = secondarySortValue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.14f),
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier
                    .rotate(rotationAngle)
                    .weight(0.12f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(start = 8.dp)
            ) {
                // Show all other sort orders (tiebreakers)
                if (sortOrders != null && ranking.sortOrders.size > 2) {
                    Text(
                        text = "Tiebreakers:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )

                    for (i in 2 until minOf(ranking.sortOrders.size, sortOrders.size)) {
                        val sortOrder = sortOrders[i]
                        val value = ranking.sortOrders[i]
                        val formattedValue = String.format(Locale.US, "%.${sortOrder.precision}f", value)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                        ) {
                            Text(
                                text = sortOrder.name,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formattedValue,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }

                // Show extra stats if available
                if (ranking.extraStats.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "Additional Stats:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    ranking.extraStats.forEachIndexed { index, value ->
                        val info = extraStatsInfo?.getOrNull(index)
                        val statName = info?.name ?: "Stat ${index + 1}"
                        val precision = info?.precision ?: 2
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                        ) {
                            Text(
                                text = statName,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = String.format(Locale.US, "%.${precision}f", value),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider()
    }
}
