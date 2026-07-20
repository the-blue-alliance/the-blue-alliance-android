package com.thebluealliance.android.wear.tracker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import com.thebluealliance.android.wear.R
import com.thebluealliance.android.wear.complication.AvatarConverter
import com.thebluealliance.android.wear.complication.TeamTrackingComplicationConfigActivity
import com.thebluealliance.android.wear.worker.TeamTrackingComplicationWorker

class TeamTrackerActivity : ComponentActivity() {
    private lateinit var trackerPrefs: TeamTrackerPreferences
    private val state = mutableStateOf(TeamTrackerState())

    private val prefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            loadState()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        trackerPrefs = TeamTrackerPreferences(this)

        loadState()
        trackerPrefs.registerOnChangeListener(prefListener)

        // Trigger refresh to get latest data
        TeamTrackingComplicationWorker.enqueueImmediateRefresh(this)
        if (trackerPrefs.teamNumber.isNotBlank()) {
            TeamTrackingComplicationWorker.enqueuePeriodicRefresh(this)
        }

        setContent {
            MaterialTheme {
                AppScaffold {
                    val currentState by state
                    TeamTrackerScreen(
                        state = currentState,
                        onChangeTeam = { launchConfigActivity() },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadState()
    }

    override fun onDestroy() {
        super.onDestroy()
        trackerPrefs.unregisterOnChangeListener(prefListener)
    }

    private fun loadState() {
        state.value =
            TeamTrackerState(
                teamNumber = trackerPrefs.teamNumber,
                teamNickname = trackerPrefs.teamNickname,
                avatarBase64 = trackerPrefs.avatarBase64,
                eventName = trackerPrefs.eventName,
                record = trackerPrefs.record,
                hasActiveEvent = trackerPrefs.hasActiveEvent,
                isLoading = trackerPrefs.isLoading,
                lastMatchLabel = trackerPrefs.lastMatchLabel,
                lastMatchRedTeams = trackerPrefs.lastMatchRedTeams,
                lastMatchBlueTeams = trackerPrefs.lastMatchBlueTeams,
                lastMatchRedScore = trackerPrefs.lastMatchRedScore,
                lastMatchBlueScore = trackerPrefs.lastMatchBlueScore,
                lastMatchWinningAlliance = trackerPrefs.lastMatchWinningAlliance,
                lastAlliance = trackerPrefs.lastAlliance,
                lastMatchBonusRp = trackerPrefs.lastMatchBonusRp,
                nextMatchLabel = trackerPrefs.nextMatchLabel,
                nextMatchRedTeams = trackerPrefs.nextMatchRedTeams,
                nextMatchBlueTeams = trackerPrefs.nextMatchBlueTeams,
                nextMatchTime = trackerPrefs.nextMatchTime,
                nextMatchTimeIsEstimate = trackerPrefs.nextMatchTimeIsEstimate,
                nextAlliance = trackerPrefs.nextAlliance,
                upcomingEvents =
                    trackerPrefs.upcomingEvents
                        .split("|")
                        .filter { it.isNotBlank() },
            )
    }

    private fun launchConfigActivity() {
        startActivity(
            Intent(this, TeamTrackingComplicationConfigActivity::class.java),
        )
    }
}

data class TeamTrackerState(
    val teamNumber: String = "",
    val teamNickname: String = "",
    val avatarBase64: String? = null,
    val eventName: String = "",
    val record: String = "",
    val hasActiveEvent: Boolean = false,
    val isLoading: Boolean = false,
    val lastMatchLabel: String = "",
    val lastMatchRedTeams: String = "",
    val lastMatchBlueTeams: String = "",
    val lastMatchRedScore: Int = -1,
    val lastMatchBlueScore: Int = -1,
    val lastMatchWinningAlliance: String = "",
    val lastAlliance: String = "",
    val lastMatchBonusRp: Int = 0,
    val nextMatchLabel: String = "",
    val nextMatchRedTeams: String = "",
    val nextMatchBlueTeams: String = "",
    val nextMatchTime: String = "",
    val nextMatchTimeIsEstimate: Boolean = false,
    val nextAlliance: String = "",
    val upcomingEvents: List<String> = emptyList(),
)

private val AllianceRed = Color(0xFFF2B8B5)
private val AllianceBlue = Color(0xFF9FA8DA)
private val AllianceRedBg = Color(0xFFC62828)
private val AllianceBlueBg = Color(0xFF1565C0)

@Composable
private fun TeamTrackerScreen(
    state: TeamTrackerState,
    onChangeTeam: () -> Unit,
) {
    if (state.teamNumber.isBlank()) {
        EmptyState(onChangeTeam)
        return
    }

    val columnState = rememberTransformingLazyColumnState()

    // Reset scroll position when team changes
    LaunchedEffect(state.teamNumber) {
        columnState.scrollToItem(0)
    }

    ScreenScaffold(
        scrollState = columnState,
        edgeButton = {
            EdgeButton(
                onClick = onChangeTeam,
                buttonSize = EdgeButtonSize.Small,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = stringResource(R.string.change_team),
                )
            }
        },
    ) { contentPadding ->
        TransformingLazyColumn(
            state = columnState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = contentPadding,
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { TeamHeader(state) }

            val hasMatchData =
                state.lastMatchLabel.isNotBlank() || state.nextMatchLabel.isNotBlank()
            val sparseContent = !hasMatchData && state.upcomingEvents.size <= 1
            if (!state.isLoading && sparseContent) {
                item { Spacer(modifier = Modifier.height(4.dp)) }
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                item { EventSubtitle(state) }
            }

            if (state.lastMatchLabel.isNotBlank()) {
                item {
                    MatchSection(
                        title = stringResource(R.string.last_match),
                        matchLabel = state.lastMatchLabel,
                        redTeams = state.lastMatchRedTeams,
                        blueTeams = state.lastMatchBlueTeams,
                        redScore = state.lastMatchRedScore.takeIf { it >= 0 },
                        blueScore = state.lastMatchBlueScore.takeIf { it >= 0 },
                        winningAlliance = state.lastMatchWinningAlliance,
                        trackedTeam = state.teamNumber,
                        trackedAlliance = state.lastAlliance,
                        bonusRp = state.lastMatchBonusRp,
                    )
                }
            }

            if (state.nextMatchLabel.isNotBlank()) {
                item {
                    MatchSection(
                        title = stringResource(R.string.next_match),
                        matchLabel = state.nextMatchLabel,
                        redTeams = state.nextMatchRedTeams,
                        blueTeams = state.nextMatchBlueTeams,
                        trackedTeam = state.teamNumber,
                        timeText =
                            buildString {
                                if (state.nextMatchTimeIsEstimate) append("~")
                                append(state.nextMatchTime)
                            }.takeIf { state.nextMatchTime.isNotBlank() },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(onChangeTeam: () -> Unit) {
    ScreenScaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.tba_lamp),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                FilledTonalButton(
                    onClick = onChangeTeam,
                    label = { Text(stringResource(R.string.set_tracked_team)) },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun TeamHeader(state: TeamTrackerState) {
    val bitmap =
        remember(state.avatarBase64) {
            state.avatarBase64?.let { AvatarConverter.decodeBoundedBitmap(it) }
        }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (bitmap != null) {
            val bgColor =
                when (state.nextAlliance.ifBlank { state.lastAlliance }) {
                    "red" -> AllianceRedBg
                    "blue" -> AllianceBlueBg
                    else -> AllianceBlueBg
                }
            Box(
                modifier =
                    Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Team ${state.teamNumber} avatar",
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = state.teamNumber,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
private fun EventSubtitle(state: TeamTrackerState) {
    if (state.hasActiveEvent) {
        val text =
            if (state.record.isNotBlank()) {
                "${state.record} at ${state.eventName}"
            } else {
                state.eventName
            }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(vertical = 2.dp),
        )
    } else if (state.upcomingEvents.isNotEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 2.dp),
        ) {
            for (event in state.upcomingEvents) {
                Text(
                    text = event,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    } else {
        Text(
            text = stringResource(R.string.no_upcoming_events),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 2.dp),
        )
    }
}

@Composable
private fun MatchSection(
    title: String,
    matchLabel: String,
    redTeams: String,
    blueTeams: String,
    redScore: Int? = null,
    blueScore: Int? = null,
    winningAlliance: String = "",
    trackedTeam: String,
    trackedAlliance: String = "",
    bonusRp: Int = 0,
    timeText: String? = null,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
    ) {
        Text(
            text = "$title \u2014 $matchLabel",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 2.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AllianceRow(
                    color = AllianceRed,
                    teams = redTeams,
                    score = redScore,
                    isWinner = winningAlliance == "red",
                    trackedTeam = trackedTeam,
                    bonusRp = if (trackedAlliance == "red") bonusRp else 0,
                )
                AllianceRow(
                    color = AllianceBlue,
                    teams = blueTeams,
                    score = blueScore,
                    isWinner = winningAlliance == "blue",
                    trackedTeam = trackedTeam,
                    bonusRp = if (trackedAlliance == "blue") bonusRp else 0,
                )
            }
            if (timeText != null) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun AllianceRow(
    color: Color,
    teams: String,
    score: Int? = null,
    isWinner: Boolean = false,
    trackedTeam: String,
    bonusRp: Int = 0,
) {
    val teamList = teams.split(", ")
    val styledTeams =
        buildAnnotatedString {
            teamList.forEachIndexed { index, team ->
                if (team == trackedTeam) {
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append(team)
                    }
                } else {
                    append(team)
                }
                if (index < teamList.lastIndex) append(", ")
            }
        }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = styledTeams,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
            color = color,
            modifier = Modifier.weight(1f),
        )
        if (bonusRp > 0) {
            Text(
                text = "\u25CF".repeat(bonusRp) + " ",
                style = MaterialTheme.typography.labelSmall,
                color = color,
            )
        }
        if (score != null) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
                color = color,
            )
        }
    }
}
