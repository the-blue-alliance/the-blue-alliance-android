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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.theme.OnBrand
import com.thebluealliance.android.ui.theme.TBATheme
import com.thebluealliance.android.ui.theme.TbaIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TeamTrackingWidgetConfigActivity : ComponentActivity() {
    @Inject lateinit var teamRepository: TeamRepository

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
        val prefilledTeam = intent?.extras?.getString("team")?.let(WidgetTeamInput::normalize)
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
                                    tint = OnBrand,
                                    modifier = Modifier.size(20.dp),
                                )
                                Text(
                                    text = "Team Tracker",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnBrand,
                                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                                )
                                IconButton(
                                    onClick = { finish() },
                                    modifier = Modifier.handCursor().size(36.dp),
                                ) {
                                    Icon(
                                        imageVector = TbaIcons.Close,
                                        contentDescription = "Close",
                                        tint = OnBrand,
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
                                var error by remember { mutableStateOf<String?>(null) }
                                var checking by remember { mutableStateOf(false) }
                                val notATeam = stringResource(R.string.widget_config_not_a_team)
                                val notFoundFormat =
                                    stringResource(R.string.widget_config_team_not_found)

                                // Editing clears the last validation message.
                                LaunchedEffect(teamNumber) { error = null }

                                fun save() {
                                    if (teamNumber.isBlank() || checking) return
                                    checking = true
                                    lifecycleScope.launch {
                                        val result =
                                            WidgetTeamInput.validate(teamNumber) { key ->
                                                teamRepository.refreshTeam(key)
                                            }
                                        checking = false
                                        when (result) {
                                            is TeamValidation.Valid ->
                                                confirmWidget(appWidgetId, result.teamNumber)
                                            TeamValidation.Invalid -> error = notATeam
                                            is TeamValidation.NotFound ->
                                                error = notFoundFormat.format(result.teamNumber)
                                        }
                                    }
                                }

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
                                    // Hold the number still while it's checked, so the result
                                    // can't land on (or save) a different one.
                                    readOnly = checking,
                                    label = { Text("Team Number") },
                                    placeholder = { Text("e.g. 9180") },
                                    isError = error != null,
                                    supportingText = error?.let { message -> { Text(message) } },
                                    keyboardOptions =
                                        KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done,
                                        ),
                                    onKeyboardAction = { save() },
                                    lineLimits = TextFieldLineLimits.SingleLine,
                                    modifier = Modifier.fillMaxWidth(),
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { save() },
                                    enabled = teamNumber.isNotBlank() && !checking,
                                    modifier =
                                        Modifier
                                            .handCursor(enabled = teamNumber.isNotBlank())
                                            .fillMaxWidth(),
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
                WidgetRefreshState.setTeam(prefs, teamNumber)
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
