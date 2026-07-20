package com.thebluealliance.android.wear.complication

import android.app.Activity
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.thebluealliance.android.wear.BuildConfig
import com.thebluealliance.android.wear.R
import com.thebluealliance.android.wear.tracker.TeamTrackerPreferences
import com.thebluealliance.android.wear.ui.TbaWearTheme
import com.thebluealliance.android.wear.worker.TeamTrackingComplicationWorker

class TeamTrackingComplicationConfigActivity : ComponentActivity() {
    private var complicationId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        complicationId = intent?.getIntExtra(
            "android.support.wearable.complications.EXTRA_CONFIG_COMPLICATION_ID",
            -1,
        ) ?: -1

        // Support passing team number directly via intent (debug-only, for ADB configuration).
        // Never honored in release builds: this activity is exported, so any app could otherwise
        // silently repoint the tracked team. Normalize to digits so the intent path cannot bypass
        // the validation the on-screen field applies.
        if (BuildConfig.DEBUG) {
            val intentTeam = intent?.getStringExtra("team_number")?.let(::normalizeTeamNumber)
            if (!intentTeam.isNullOrBlank()) {
                saveAndFinish(intentTeam)
                return
            }
        }

        val existingTeam = TeamTrackerPreferences(this).teamNumber

        setContent {
            TbaWearTheme {
                AppScaffold {
                    ScreenScaffold {
                        ConfigScreen(
                            initialTeam = existingTeam,
                            onSave = { teamNumber -> saveAndFinish(teamNumber) },
                        )
                    }
                }
            }
        }
    }

    private fun saveAndFinish(teamNumber: String) {
        Log.d(TAG, "saveAndFinish: team=$teamNumber, complicationId=$complicationId")
        if (teamNumber.isNotBlank()) {
            val prefs = TeamTrackerPreferences(this)
            val teamChanged = prefs.teamNumber != teamNumber
            if (teamChanged) {
                prefs.clearCachedData()
            }
            // Single source of truth for tracked team
            prefs.teamNumber = teamNumber

            if (complicationId >= 0) {
                TeamTrackingComplicationPreferences.addComplicationId(this, complicationId)
            }

            TeamTrackingComplicationWorker.enqueueImmediateRefresh(this)
            TeamTrackingComplicationWorker.enqueuePeriodicRefresh(this)

            ComplicationDataSourceUpdateRequester
                .create(
                    this,
                    ComponentName(this, TeamTrackingComplicationService::class.java),
                ).requestUpdateAll()
        }
        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {
        private const val TAG = "ComplicationConfig"
    }
}

/** Strip everything but digits, matching what the on-screen team-number field accepts. */
private fun normalizeTeamNumber(raw: String): String = raw.filter { it.isDigit() }

@Composable
private fun ConfigScreen(
    initialTeam: String,
    onSave: (String) -> Unit,
) {
    val textFieldState = rememberTextFieldState()

    fun save() {
        val value = normalizeTeamNumber(textFieldState.text.toString())
        onSave(value.ifBlank { initialTeam })
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text =
                if (initialTeam.isNotBlank()) {
                    stringResource(R.string.change_team)
                } else {
                    stringResource(R.string.team_number)
                },
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        BasicTextField(
            state = textFieldState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = MaterialTheme.shapes.medium,
                    ).padding(horizontal = 12.dp, vertical = 8.dp),
            textStyle =
                TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            lineLimits = androidx.compose.foundation.text.input.TextFieldLineLimits.SingleLine,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
            decorator = { innerTextField ->
                if (textFieldState.text.isEmpty() && initialTeam.isNotBlank()) {
                    Text(
                        text = initialTeam,
                        style =
                            TextStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                            ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                innerTextField()
            },
        )
        FilledTonalButton(
            onClick = { save() },
            label = { Text(stringResource(R.string.save)) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
        )
    }
}
