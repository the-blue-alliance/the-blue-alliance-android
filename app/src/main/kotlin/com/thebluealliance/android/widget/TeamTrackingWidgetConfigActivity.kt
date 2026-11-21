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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.thebluealliance.android.R
import com.thebluealliance.android.ui.theme.TBATheme
import com.thebluealliance.android.ui.theme.TbaIcons
import kotlinx.coroutines.launch

class TeamTrackingWidgetConfigActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If the user backs out, cancel widget placement
        setResult(Activity.RESULT_CANCELED)

        val appWidgetId =
            intent?.extras?.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID,
            ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // If a team number was provided (e.g. from PinWidgetActivity), auto-configure and skip UI
        val prefilledTeam = intent?.extras?.getString("team")
        if (prefilledTeam != null) {
            confirmWidget(appWidgetId, prefilledTeam)
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
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(
                                            start = 16.dp,
                                            end = 4.dp,
                                            top = 4.dp,
                                            bottom = 4.dp,
                                        ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.tba_lamp),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp),
                                )
                                Text(
                                    text = "Team Tracker",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                                )
                                IconButton(
                                    onClick = { finish() },
                                    modifier = Modifier.size(36.dp),
                                ) {
                                    Icon(
                                        imageVector = TbaIcons.Close,
                                        contentDescription = "Close",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }

                            // Content
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                val teamNumberState = rememberTextFieldState()
                                val teamNumber = teamNumberState.text.toString()

                                LaunchedEffect(Unit) {
                                    try {
                                        val manager =
                                            GlanceAppWidgetManager(
                                                this@TeamTrackingWidgetConfigActivity,
                                            )
                                        val glanceId = manager.getGlanceIdBy(appWidgetId)
                                        val state =
                                            getAppWidgetState(
                                                this@TeamTrackingWidgetConfigActivity,
                                                PreferencesGlanceStateDefinition,
                                                glanceId,
                                            )
                                        state[TeamTrackingWidgetKeys.TEAM_NUMBER]?.let {
                                            teamNumberState.setTextAndPlaceCursorAtEnd(it)
                                        }
                                    } catch (_: Exception) {
                                        // New widget — no existing state
                                    }
                                }

                                TextField(
                                    state = teamNumberState,
                                    inputTransformation = DigitsOnly,
                                    label = { Text("Team Number") },
                                    placeholder = { Text("e.g. 9180") },
                                    keyboardOptions =
                                        KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done,
                                        ),
                                    onKeyboardAction = {
                                        if (teamNumber.isNotBlank()) {
                                            confirmWidget(appWidgetId, teamNumber)
                                        }
                                    },
                                    lineLimits = TextFieldLineLimits.SingleLine,
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

    private fun confirmWidget(
        appWidgetId: Int,
        teamNumber: String,
    ) {
        lifecycleScope.launch {
            val manager = GlanceAppWidgetManager(this@TeamTrackingWidgetConfigActivity)
            val glanceId = manager.getGlanceIdBy(appWidgetId)

            updateAppWidgetState(this@TeamTrackingWidgetConfigActivity, glanceId) { prefs ->
                prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] = teamNumber
                prefs[TeamTrackingWidgetKeys.TEAM_KEY] = "frc$teamNumber"
                // Sane default until the first worker run stamps the real state — shrinks the
                // "unknown" widget_state window for analytics.
                prefs[TeamTrackingWidgetKeys.WIDGET_STATE] = WidgetState.NO_UPCOMING.value
            }

            // Update widget UI to show loading state
            TeamTrackingWidget().update(this@TeamTrackingWidgetConfigActivity, glanceId)

            // Trigger data fetch
            TeamTrackingWorker.enqueuePeriodicRefresh(this@TeamTrackingWidgetConfigActivity)
            WorkManager
                .getInstance(this@TeamTrackingWidgetConfigActivity)
                .enqueue(OneTimeWorkRequestBuilder<TeamTrackingWorker>().build())

            val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }
}

/** Team numbers are digits only; rejects any edit that would add anything else. */
private val DigitsOnly =
    InputTransformation {
        if (!asCharSequence().all(Char::isDigit)) revertAllChanges()
    }
