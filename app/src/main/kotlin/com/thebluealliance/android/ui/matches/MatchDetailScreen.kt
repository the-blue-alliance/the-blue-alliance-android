package com.thebluealliance.android.ui.matches

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.formatBreakdownValue
import com.thebluealliance.android.domain.getFullLabel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.rpBonuses
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.common.shareTbaUrl
import com.thebluealliance.android.ui.components.MediaGridItem
import com.thebluealliance.android.ui.components.MediaGridRow
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.mediaUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToTeamEvent: (teamKey: String, eventKey: String) -> Unit = { _, _ -> },
    onNavigateToEvent: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit,
    viewModel: MatchDetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TBATopAppBar(
                title = {
                    Text(
                        text = uiState.match?.getFullLabel(uiState.playoffType) ?: "Match",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }
                    uiState.match?.let { match ->
                        IconButton(onClick = {
                            val eventLabel = uiState.eventName?.let { "${uiState.year} $it - " } ?: ""
                            context.shareTbaUrl(
                                title = "$eventLabel${match.getFullLabel(uiState.playoffType)}",
                                url = "https://www.thebluealliance.com/match/${match.key}",
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            val match = uiState.match
            if (match == null) {
                LoadingBox(
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = innerPadding,
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
                    item(key = "alliance_teams") {
                        AllianceTeams(
                            redTeamKeys = match.redTeamKeys,
                            blueTeamKeys = match.blueTeamKeys,
                            onTeamClick = { teamKey ->
                                val ek = uiState.eventKey
                                if (ek != null) onNavigateToTeamEvent(teamKey, ek)
                            },
                        )
                    }

                    // Media
                    val mediaItems = uiState.videos
                        .filter { mediaUrl(it.type, it.key) != null }
                        .map { MediaGridItem(type = it.type, foreignKey = it.key) }
                    if (mediaItems.isNotEmpty()) {
                        item(key = "media_header") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "Match Video",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                        item(key = "media_grid") {
                            MediaGridRow(
                                items = mediaItems,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
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
                        val orderedFields = getOrderedBreakdownFields(
                            year = uiState.year,
                            redBreakdown = redBreakdown,
                            blueBreakdown = blueBreakdown,
                        )

                        items(orderedFields, key = { "breakdown_${it.first}" }) { (apiKey, label) ->
                            BreakdownRow(
                                label = label,
                                redValue = formatBreakdownValue(apiKey, redBreakdown[apiKey] ?: "-"),
                                blueValue = formatBreakdownValue(apiKey, blueBreakdown[apiKey] ?: "-"),
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
    val rpBonuses = remember(match.scoreBreakdown) { match.rpBonuses() }

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
            if (rpBonuses != null) {
                RpDots(rpBonuses.red, MaterialTheme.colorScheme.error)
            }
            match.redAdvancement?.let {
                Box(
                    modifier = Modifier
                        .height(36.dp)  // Reserve consistent space
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (match.winningAlliance == "red") FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        lineHeight = 14.sp,  // Tighter line spacing
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .padding(top = 4.dp)
                    )
                }
            }
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
            if (rpBonuses != null) {
                RpDots(rpBonuses.blue, MaterialTheme.colorScheme.primary)
            }
            match.blueAdvancement?.let {
                Box(
                    modifier = Modifier
                        .height(36.dp)  // Reserve consistent space
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (match.winningAlliance == "blue") FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        lineHeight = 14.sp,  // Tighter line spacing
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AllianceTeams(
    redTeamKeys: List<String>,
    blueTeamKeys: List<String>,
    onTeamClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            redTeamKeys.forEach { key ->
                Text(
                    text = key.removePrefix("frc"),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clickable { onTeamClick(key) }
                        .padding(top = 2.dp),
                )
            }
        }

        // Invisible separator to match ScoreSummary alignment
        Text(
            text = "-",
            style = MaterialTheme.typography.displaySmall,
            color = Color.Transparent,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            blueTeamKeys.forEach { key ->
                Text(
                    text = key.removePrefix("frc"),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onTeamClick(key) }
                        .padding(top = 2.dp),
                )
            }
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(0.2f),
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.Center,
        )
        Text(
            text = blueValue,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(0.2f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RpDots(bonuses: List<Boolean>, achievedColor: Color) {
    Row(
        modifier = Modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        bonuses.forEach { achieved ->
            Canvas(modifier = Modifier.size(8.dp)) {
                if (achieved) {
                    drawCircle(
                        color = achievedColor,
                        radius = size.minDimension / 2,
                    )
                } else {
                    drawCircle(
                        color = Color(0xFF9CA3AF),
                        radius = size.minDimension / 2 - 1.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }
            }
        }
    }
}


private fun camelCaseToLabel(key: String): String {
    return key.replace(Regex("([A-Z])"), " $1")
        .replace("_", " ")
        .trim()
        .replaceFirstChar { it.uppercase() }
}

private fun getOrderedBreakdownFields(
    year: Int,
    redBreakdown: Map<String, String>,
    blueBreakdown: Map<String, String>,
): List<Pair<String, String>> {
    val yearFields = breakdownFieldsByYear[year]
    if (yearFields == null) {
        // Fallback: show all keys with camelCase-to-label conversion
        val allKeys = (redBreakdown.keys + blueBreakdown.keys).distinct()
            .filter { it != "totalPoints" }
        return allKeys.map { it to camelCaseToLabel(it) }
    }

    val knownKeys = yearFields.map { it.first }.toSet()
    val result = yearFields.toMutableList()

    // Append any unrecognized keys at the end
    val allKeys = (redBreakdown.keys + blueBreakdown.keys).distinct()
    for (key in allKeys) {
        if (key !in knownKeys) {
            result.add(key to camelCaseToLabel(key))
        }
    }
    return result
}

private val breakdownFields2026 = listOf(
    "autoTowerRobot1" to "Auto tower 1",
    "autoTowerRobot2" to "Auto tower 2",
    "autoTowerRobot3" to "Auto tower 3",
    "autoTowerPoints" to "Auto tower",
    "totalAutoPoints" to "Total auto",
    "totalTeleopPoints" to "Total teleop",
    "endGameTowerRobot1" to "Endgame 1",
    "endGameTowerRobot2" to "Endgame 2",
    "endGameTowerRobot3" to "Endgame 3",
    "endGameTowerPoints" to "Endgame tower",
    "totalTowerPoints" to "Total tower",
    "energizedAchieved" to "Energized bonus",
    "superchargedAchieved" to "Supercharged bonus",
    "traversalAchieved" to "Traversal bonus",
    "minorFoulCount" to "Minor fouls",
    "majorFoulCount" to "Major fouls",
    "g206Penalty" to "G206 penalty",
    "foulPoints" to "Foul points",
    "adjustPoints" to "Adjust",
    "totalPoints" to "Total",
    "rp" to "RP",
)

private val breakdownFields2025 = listOf(
    "autoLineRobot1" to "Auto leave 1",
    "autoLineRobot2" to "Auto leave 2",
    "autoLineRobot3" to "Auto leave 3",
    "autoMobilityPoints" to "Auto mobility",
    "autoCoralPoints" to "Auto coral",
    "autoPoints" to "Total auto",
    "teleopCoralPoints" to "Teleop coral",
    "wallAlgaeCount" to "Processor algae",
    "netAlgaeCount" to "Net algae",
    "algaePoints" to "Algae points",
    "endGameRobot1" to "Endgame 1",
    "endGameRobot2" to "Endgame 2",
    "endGameRobot3" to "Endgame 3",
    "endGameBargePoints" to "Barge points",
    "teleopPoints" to "Total teleop",
    "coopertitionCriteriaMet" to "Coopertition",
    "autoBonusAchieved" to "Auto bonus",
    "coralBonusAchieved" to "Coral bonus",
    "bargeBonusAchieved" to "Barge bonus",
    "foulCount" to "Fouls",
    "techFoulCount" to "Tech fouls",
    "foulPoints" to "Foul points",
    "adjustPoints" to "Adjust",
    "totalPoints" to "Total",
    "rp" to "RP",
)

private val breakdownFields2024 = listOf(
    "autoLineRobot1" to "Auto leave 1",
    "autoLineRobot2" to "Auto leave 2",
    "autoLineRobot3" to "Auto leave 3",
    "autoLeavePoints" to "Auto leave points",
    "autoAmpNoteCount" to "Auto amp notes",
    "autoSpeakerNoteCount" to "Auto speaker notes",
    "autoTotalNotePoints" to "Auto note points",
    "autoPoints" to "Total auto",
    "teleopAmpNoteCount" to "Teleop amp notes",
    "teleopSpeakerNoteCount" to "Teleop speaker notes",
    "teleopSpeakerNoteAmplifiedCount" to "Amplified speaker",
    "teleopTotalNotePoints" to "Teleop note points",
    "endGameRobot1" to "Endgame 1",
    "endGameRobot2" to "Endgame 2",
    "endGameRobot3" to "Endgame 3",
    "endGameHarmonyPoints" to "Harmony",
    "endGameNoteInTrapPoints" to "Trap",
    "endGameOnStagePoints" to "On stage",
    "endGameParkPoints" to "Park",
    "teleopPoints" to "Total teleop",
    "coopertitionBonusAchieved" to "Coopertition",
    "melodyBonusAchieved" to "Melody bonus",
    "ensembleBonusAchieved" to "Ensemble bonus",
    "foulCount" to "Fouls",
    "techFoulCount" to "Tech fouls",
    "foulPoints" to "Foul points",
    "adjustPoints" to "Adjust",
    "totalPoints" to "Total",
    "rp" to "RP",
)

private val breakdownFields2023 = listOf(
    "autoLineRobot1" to "Auto line 1",
    "autoLineRobot2" to "Auto line 2",
    "autoLineRobot3" to "Auto line 3",
    "autoMobilityPoints" to "Auto mobility",
    "autoGamePieceCount" to "Auto game pieces",
    "autoGamePiecePoints" to "Auto game piece points",
    "autoChargeStationPoints" to "Auto charge station",
    "autoPoints" to "Total auto",
    "teleopGamePieceCount" to "Teleop game pieces",
    "teleopGamePiecePoints" to "Teleop game piece points",
    "endGameRobot1" to "Endgame 1",
    "endGameRobot2" to "Endgame 2",
    "endGameRobot3" to "Endgame 3",
    "endGameChargeStationPoints" to "Endgame charge station",
    "endGameParkPoints" to "Endgame park",
    "teleopPoints" to "Total teleop",
    "activationBonusAchieved" to "Activation bonus",
    "sustainabilityBonusAchieved" to "Sustainability bonus",
    "coopertitionCriteriaMet" to "Coopertition",
    "foulCount" to "Fouls",
    "techFoulCount" to "Tech fouls",
    "foulPoints" to "Foul points",
    "adjustPoints" to "Adjust",
    "totalPoints" to "Total",
    "rp" to "RP",
)

private val breakdownFieldsByYear = mapOf(
    2026 to breakdownFields2026,
    2025 to breakdownFields2025,
    2024 to breakdownFields2024,
    2023 to breakdownFields2023,
)
