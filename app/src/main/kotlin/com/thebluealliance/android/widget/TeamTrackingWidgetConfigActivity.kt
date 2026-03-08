package com.thebluealliance.android.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.thebluealliance.android.ui.theme.TBATheme
import com.thebluealliance.android.ui.theme.TBABlue
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class TeamTrackingWidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If the user backs out, cancel widget placement
        setResult(Activity.RESULT_CANCELED)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            TBATheme {
                Dialog(onDismissRequest = { finish() }) {
                    Card(
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Column {
                            // Blue header bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TBABlue)
                                    .padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Team Tracker",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f),
                                )
                                IconButton(
                                    onClick = { finish() },
                                    modifier = Modifier.size(36.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.White,
                                    )
                                }
                            }

                            // Content
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                var teamNumber by remember { mutableStateOf("") }

                                TextField(
                                    value = teamNumber,
                                    onValueChange = { teamNumber = it.filter { c -> c.isDigit() } },
                                    label = { Text("Team Number") },
                                    placeholder = { Text("e.g. 9180") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done,
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (teamNumber.isNotBlank()) {
                                                confirmWidget(appWidgetId, teamNumber)
                                            }
                                        }
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { confirmWidget(appWidgetId, teamNumber) },
                                    enabled = teamNumber.isNotBlank(),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text("Save")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun confirmWidget(appWidgetId: Int, teamNumber: String) {
        lifecycleScope.launch {
            val manager = GlanceAppWidgetManager(this@TeamTrackingWidgetConfigActivity)
            val glanceId = manager.getGlanceIdBy(appWidgetId)

            updateAppWidgetState(this@TeamTrackingWidgetConfigActivity, glanceId) { prefs ->
                prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] = teamNumber
                prefs[TeamTrackingWidgetKeys.TEAM_KEY] = "frc$teamNumber"
            }

            // Update widget UI to show loading state
            TeamTrackingWidget().update(this@TeamTrackingWidgetConfigActivity, glanceId)

            // Trigger data fetch
            TeamTrackingWorker.enqueuePeriodicRefresh(this@TeamTrackingWidgetConfigActivity)
            WorkManager.getInstance(this@TeamTrackingWidgetConfigActivity)
                .enqueue(OneTimeWorkRequestBuilder<TeamTrackingWorker>().build())

            val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }
}
