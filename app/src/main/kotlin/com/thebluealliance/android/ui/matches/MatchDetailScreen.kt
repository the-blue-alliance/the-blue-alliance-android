package com.thebluealliance.android.ui.matches

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Match

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTeam: (String) -> Unit = {},
    viewModel: MatchDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = uiState.match?.let { matchLabel(it) } ?: "Match",
                    maxLines = 1,
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            val match = uiState.match
            if (match == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // Score summary
                    item(key = "score_header") {
                        ScoreSummary(match)
                    }

                    item(key = "divider_teams") {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    // Teams
                    item(key = "red_teams") {
                        AllianceTeams("Red Alliance", match.redTeamKeys, MaterialTheme.colorScheme.error, onNavigateToTeam)
                    }
                    item(key = "blue_teams") {
                        AllianceTeams("Blue Alliance", match.blueTeamKeys, MaterialTheme.colorScheme.primary, onNavigateToTeam)
                    }

                    // Score breakdown
                    val breakdown = uiState.scoreBreakdown
                    if (breakdown != null) {
                        item(key = "breakdown_header") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "Score Breakdown",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }

                        val redBreakdown = breakdown["red"] ?: emptyMap()
                        val blueBreakdown = breakdown["blue"] ?: emptyMap()
                        val allKeys = (redBreakdown.keys + blueBreakdown.keys).distinct()

                        items(allKeys, key = { "breakdown_$it" }) { field ->
                            BreakdownRow(
                                label = formatBreakdownKey(field),
                                redValue = redBreakdown[field] ?: "-",
                                blueValue = blueBreakdown[field] ?: "-",
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreSummary(match: Match) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Red", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
            Text(
                text = if (match.redScore < 0) "—" else match.redScore.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = if (match.winningAlliance == "red") FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.error,
            )
        }
        Text(
            text = "-",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Blue", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Text(
                text = if (match.blueScore < 0) "—" else match.blueScore.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = if (match.winningAlliance == "blue") FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun AllianceTeams(
    title: String,
    teamKeys: List<String>,
    color: androidx.compose.ui.graphics.Color,
    onTeamClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color,
        )
        teamKeys.forEach { key ->
            Text(
                text = key.removePrefix("frc"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onTeamClick(key) }
                    .padding(start = 8.dp, top = 2.dp),
            )
        }
    }
}

@Composable
private fun BreakdownRow(label: String, redValue: String, blueValue: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = redValue,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(0.2f),
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.Center,
        )
        Text(
            text = blueValue,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(0.2f),
            textAlign = TextAlign.Center,
        )
    }
}

private fun matchLabel(match: Match): String = when (match.compLevel) {
    "qm" -> "Qual ${match.matchNumber}"
    "qf" -> "QF${match.setNumber}-${match.matchNumber}"
    "sf" -> "SF${match.setNumber}-${match.matchNumber}"
    "f" -> "Final ${match.setNumber}-${match.matchNumber}"
    else -> "${match.compLevel}${match.setNumber}-${match.matchNumber}"
}

private fun formatBreakdownKey(key: String): String =
    key.replace(Regex("([A-Z])"), " $1")
        .replace("_", " ")
        .trim()
        .replaceFirstChar { it.uppercase() }
