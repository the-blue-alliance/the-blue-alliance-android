package com.thebluealliance.android.ui.settings

import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.BuildConfig
import com.thebluealliance.android.messaging.NotificationBuilder
import com.thebluealliance.android.messaging.NotificationChannelManager
import com.thebluealliance.android.ui.theme.ThemeMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.thebluealliance.android.ui.theme.TBABlue

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
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TBABlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
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
