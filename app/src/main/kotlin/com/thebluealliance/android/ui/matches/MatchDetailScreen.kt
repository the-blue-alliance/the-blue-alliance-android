package com.thebluealliance.android.ui.matches

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.fullLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTeam: (String) -> Unit = {},
    onNavigateToEvent: (String) -> Unit = {},
    viewModel: MatchDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = uiState.match?.fullLabel ?: "Match",
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
                    // Event name + match time
                    item(key = "event_info") {
                        EventInfo(
                            eventName = uiState.eventName,
                            eventKey = uiState.eventKey,
                            formattedTime = uiState.formattedTime,
                            onNavigateToEvent = onNavigateToEvent,
                        )
                    }

                    // Score summary
                    item(key = "score_header") {
                        ScoreSummary(match)
                    }

                    item(key = "divider_teams") {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    // Teams
                    item(key = "red_teams") {
                        AllianceTeams("Red alliance", match.redTeamKeys, MaterialTheme.colorScheme.error, onNavigateToTeam)
                    }
                    item(key = "blue_teams") {
                        AllianceTeams("Blue alliance", match.blueTeamKeys, MaterialTheme.colorScheme.primary, onNavigateToTeam)
                    }

                    // Videos
                    val videos = uiState.videos
                    if (videos.isNotEmpty()) {
                        item(key = "videos_header") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "Videos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                        items(videos, key = { "video_${it.key}" }) { video ->
                            VideoRow(video)
                        }
                    }

                    // Score breakdown
                    val breakdown = uiState.scoreBreakdown
                    if (breakdown != null) {
                        item(key = "breakdown_header") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "Score breakdown",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }

                        val redBreakdown = breakdown["red"] ?: emptyMap()
                        val blueBreakdown = breakdown["blue"] ?: emptyMap()
                        val allKeys = (redBreakdown.keys + blueBreakdown.keys).distinct()
                            .filter { it != "totalPoints" }

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
private fun EventInfo(
    eventName: String?,
    eventKey: String?,
    formattedTime: String?,
    onNavigateToEvent: (String) -> Unit,
) {
    if (eventName == null && formattedTime == null) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        if (eventName != null && eventKey != null) {
            Text(
                text = eventName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToEvent(eventKey) },
            )
        }
        if (formattedTime != null) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
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
private fun VideoRow(video: MatchVideo) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val url = when (video.type) {
                    "youtube" -> "https://www.youtube.com/watch?v=${video.key}"
                    else -> return@clickable
                }
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 12.dp),
        )
        Text(
            text = "Watch on YouTube",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
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

private fun formatBreakdownKey(key: String): String {
    // Handle well-known TBA field names
    val mapped = breakdownKeyNames[key]
    if (mapped != null) return mapped

    return key.replace(Regex("([A-Z])"), " $1")
        .replace("_", " ")
        .trim()
        .replaceFirstChar { it.uppercase() }
}

private val breakdownKeyNames = mapOf(
    "adjustPoints" to "Adjust",
    "autoPoints" to "Auto",
    "teleopPoints" to "Teleop",
    "foulPoints" to "Foul points",
    "foulCount" to "Fouls",
    "techFoulCount" to "Tech fouls",
    "rp" to "RP",
    "autoLineRobot1" to "Auto line robot 1",
    "autoLineRobot2" to "Auto line robot 2",
    "autoLineRobot3" to "Auto line robot 3",
    "endGameRobot1" to "Endgame robot 1",
    "endGameRobot2" to "Endgame robot 2",
    "endGameRobot3" to "Endgame robot 3",
    "autoGamePieceCount" to "Auto game pieces",
    "teleopGamePieceCount" to "Teleop game pieces",
    "autoMobilityPoints" to "Auto mobility",
    "autoGamePiecePoints" to "Auto game piece points",
    "teleopGamePiecePoints" to "Teleop game piece points",
    "endGameParkPoints" to "Endgame park points",
    "endGameChargeStationPoints" to "Endgame charge station",
    "autoChargeStationPoints" to "Auto charge station",
    "activationBonusAchieved" to "Activation bonus",
    "sustainabilityBonusAchieved" to "Sustainability bonus",
    "coopertitionCriteriaMet" to "Coopertition",
)
