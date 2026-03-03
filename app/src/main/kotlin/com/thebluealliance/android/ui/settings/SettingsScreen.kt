package com.thebluealliance.android.ui.settings

import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.BuildConfig
import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.messaging.NotificationBuilder
import com.thebluealliance.android.messaging.NotificationChannelManager
import com.thebluealliance.android.tracking.MatchTrackingNotificationBuilder
import com.thebluealliance.android.tracking.MatchTrackingService
import com.thebluealliance.android.tracking.TrackedTeamState
import com.thebluealliance.android.ui.theme.ThemeMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.thebluealliance.android.ui.components.TBATopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        topBar = {
            TBATopAppBar(
                title = { Text("Settings") },
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
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "Theme",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                ThemeMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = themeMode == mode,
                        onClick = { viewModel.setThemeMode(mode) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = ThemeMode.entries.size,
                        ),
                    ) {
                        Text(
                            when (mode) {
                                ThemeMode.AUTO -> "Auto"
                                ThemeMode.LIGHT -> "Light"
                                ThemeMode.DARK -> "Dark"
                            }
                        )
                    }
                }
            }

            if (BuildConfig.DEBUG) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text(
                    text = "Debug",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                val context = LocalContext.current
                Button(
                    onClick = { sendTestMatchScore(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text("Test: Match score notification")
                }

                Button(
                    onClick = { sendTestUpcomingMatch(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text("Test: Upcoming match notification")
                }

                Button(
                    onClick = { sendTestEventUpdate(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text("Test: Event update notification")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text(
                    text = "Live Updates",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )

                Button(
                    onClick = { sendTrackerNextOnly(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text("Track: Next")
                }

                Button(
                    onClick = { sendTrackerNextAndNowOther(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text("Track: Next + Now (other team playing)")
                }

                Button(
                    onClick = { sendTrackerNowPlaying(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text("Track: Next + Now (our team playing)")
                }

                Button(
                    onClick = { sendTrackerAllThree(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text("Track: Next + Now + Last")
                }

                Button(
                    onClick = { sendTrackerQualsDoneWaiting(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text("Track: Quals complete, Last only")
                }

                Button(
                    onClick = { sendTrackerAllDone(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text("Track: Event over, Last only")
                }

                Button(
                    onClick = { dismissTracker(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    Text("Track: Dismiss")
                }
            }
        }
    }
}

private fun sendTestMatchScore(context: Context) {
    val builder = NotificationBuilder(context)
    val notification = builder.build(
        channelId = NotificationChannelManager.CHANNEL_MATCH,
        title = "BCVI Q42 Results",
        body = "177, 254, 1114 beat 2056, 148, 67 scoring 152-138.",
        eventKey = "2026bcvi",
        matchKey = "2026bcvi_qm42",
        teamKey = "frc177",
        notificationType = "match_score",
    )
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.notify(9001, notification)
}

private fun sendTestUpcomingMatch(context: Context) {
    val builder = NotificationBuilder(context)
    val notification = builder.build(
        channelId = NotificationChannelManager.CHANNEL_MATCH,
        title = "BCVI Q43 Starting Soon",
        body = "BC District Event, Quals 43: 177, 1519, 4909 will play 2791, 3467, 78.",
        eventKey = "2026bcvi",
        matchKey = "2026bcvi_qm43",
        teamKey = "frc177",
        notificationType = "upcoming_match",
    )
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.notify(9002, notification)
}

// -- Match Tracking debug helpers --

private fun fakeMatch(
    key: String,
    eventKey: String = "2026bcvi",
    compLevel: CompLevel = CompLevel.QUAL,
    matchNumber: Int = 1,
    setNumber: Int = 1,
    time: Long? = null,
    predictedTime: Long? = null,
    actualTime: Long? = null,
    redTeamKeys: List<String> = listOf("frc1431", "frc177", "frc175"),
    blueTeamKeys: List<String> = listOf("frc2468", "frc1357", "frc433"),
    redScore: Int = -1,
    blueScore: Int = -1,
    winningAlliance: String? = null,
) = Match(
    key = key,
    eventKey = eventKey,
    compLevel = compLevel,
    matchNumber = matchNumber,
    setNumber = setNumber,
    time = time,
    predictedTime = predictedTime,
    actualTime = actualTime,
    redTeamKeys = redTeamKeys,
    blueTeamKeys = blueTeamKeys,
    redScore = redScore,
    blueScore = blueScore,
    winningAlliance = winningAlliance,
)

private fun postTrackerNotification(context: Context, state: TrackedTeamState) {
    val notification = MatchTrackingNotificationBuilder.build(context, state)
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.notify(MatchTrackingNotificationBuilder.NOTIFICATION_ID, notification)
}

private fun sendTrackerNextOnly(context: Context) {
    val nextTime = (System.currentTimeMillis() / 1000) + 900 // 15 min from now
    val state = TrackedTeamState(
        teamKey = "frc175",
        eventKey = "2026bcvi",
        playoffType = PlayoffType.OTHER,
        nextMatch = fakeMatch("2026bcvi_qm31", matchNumber = 31, predictedTime = nextTime),
        currentMatch = null,
        lastMatch = null,
        isTeamPlaying = false,
        record = null,
        autoDismissAfter = null,
    )
    postTrackerNotification(context, state)
}

private fun sendTrackerNextAndNowOther(context: Context) {
    val nextTime = (System.currentTimeMillis() / 1000) + 900
    val state = TrackedTeamState(
        teamKey = "frc175",
        eventKey = "2026bcvi",
        playoffType = PlayoffType.OTHER,
        nextMatch = fakeMatch("2026bcvi_qm31", matchNumber = 31, predictedTime = nextTime),
        currentMatch = fakeMatch(
            "2026bcvi_qm23", matchNumber = 23,
            redTeamKeys = listOf("frc1431", "frc5678", "frc189"),
            blueTeamKeys = listOf("frc9012", "frc3456", "frc7890"),
        ),
        lastMatch = null,
        isTeamPlaying = false,
        record = null,
        autoDismissAfter = null,
    )
    postTrackerNotification(context, state)
}

private fun sendTrackerNowPlaying(context: Context) {
    val nextTime = (System.currentTimeMillis() / 1000) + 1800
    val state = TrackedTeamState(
        teamKey = "frc175",
        eventKey = "2026bcvi",
        playoffType = PlayoffType.OTHER,
        nextMatch = fakeMatch("2026bcvi_qm31", matchNumber = 31, predictedTime = nextTime),
        currentMatch = fakeMatch("2026bcvi_qm23", matchNumber = 23),
        lastMatch = null,
        isTeamPlaying = true,
        record = null,
        autoDismissAfter = null,
    )
    postTrackerNotification(context, state)
}

private fun sendTrackerAllThree(context: Context) {
    val nextTime = (System.currentTimeMillis() / 1000) + 900
    val state = TrackedTeamState(
        teamKey = "frc175",
        eventKey = "2026bcvi",
        playoffType = PlayoffType.OTHER,
        nextMatch = fakeMatch("2026bcvi_qm31", matchNumber = 31, predictedTime = nextTime),
        currentMatch = fakeMatch(
            "2026bcvi_qm23", matchNumber = 23,
            redTeamKeys = listOf("frc1431", "frc5678", "frc189"),
            blueTeamKeys = listOf("frc9012", "frc3456", "frc7890"),
        ),
        lastMatch = fakeMatch(
            "2026bcvi_qm15", matchNumber = 15,
            redTeamKeys = listOf("frc1431", "frc177", "frc189"),
            blueTeamKeys = listOf("frc175", "frc1994", "frc433"),
            redScore = 145, blueScore = 132, winningAlliance = "red",
        ),
        isTeamPlaying = false,
        record = null,
        autoDismissAfter = null,
    )
    postTrackerNotification(context, state)
}

private fun sendTrackerQualsDoneWaiting(context: Context) {
    val state = TrackedTeamState(
        teamKey = "frc175",
        eventKey = "2026bcvi",
        playoffType = PlayoffType.OTHER,
        nextMatch = null,
        currentMatch = null,
        lastMatch = fakeMatch(
            "2026bcvi_qm60", matchNumber = 60,
            redTeamKeys = listOf("frc175", "frc254", "frc1114"),
            blueTeamKeys = listOf("frc2056", "frc148", "frc67"),
            redScore = 152, blueScore = 138, winningAlliance = "red",
        ),
        isTeamPlaying = false,
        record = com.thebluealliance.android.tracking.TeamRecord(wins = 8, losses = 4, ties = 0),
        autoDismissAfter = null,
    )
    postTrackerNotification(context, state)
}

private fun sendTrackerAllDone(context: Context) {
    val state = TrackedTeamState(
        teamKey = "frc175",
        eventKey = "2026bcvi",
        playoffType = PlayoffType.OTHER,
        nextMatch = null,
        currentMatch = null,
        lastMatch = fakeMatch(
            "2026bcvi_f1", matchNumber = 1, compLevel = CompLevel.FINAL,
            redTeamKeys = listOf("frc175", "frc254", "frc1114"),
            blueTeamKeys = listOf("frc2056", "frc148", "frc67"),
            redScore = 165, blueScore = 138, winningAlliance = "red",
        ),
        isTeamPlaying = false,
        record = com.thebluealliance.android.tracking.TeamRecord(wins = 8, losses = 4, ties = 0),
        autoDismissAfter = null,
    )
    postTrackerNotification(context, state)
}

private fun dismissTracker(context: Context) {
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.cancel(MatchTrackingNotificationBuilder.NOTIFICATION_ID)
    MatchTrackingService.stop(context)
}

private fun sendTestEventUpdate(context: Context) {
    val builder = NotificationBuilder(context)
    val notification = builder.build(
        channelId = NotificationChannelManager.CHANNEL_EVENT,
        title = "BCVI Schedule Updated",
        body = "The BC District Event match schedule has been updated. The next match starts at 14:30 ET.",
        eventKey = "2026bcvi",
        notificationType = "schedule_updated",
    )
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.notify(9003, notification)
}
