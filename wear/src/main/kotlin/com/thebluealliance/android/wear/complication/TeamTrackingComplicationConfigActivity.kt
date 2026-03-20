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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.thebluealliance.android.wear.worker.TeamTrackingComplicationWorker

class TeamTrackingComplicationConfigActivity : ComponentActivity() {

    private var complicationId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        complicationId = intent?.getIntExtra(
            "android.support.wearable.complications.EXTRA_CONFIG_COMPLICATION_ID",
            -1
        ) ?: -1

        // Support passing team number directly via intent (for ADB configuration)
        val intentTeam = intent?.getStringExtra("team_number")
        if (intentTeam != null && complicationId >= 0) {
            saveAndFinish(intentTeam)
            return
        }

        val existingTeam = if (complicationId >= 0) {
            TeamTrackingComplicationPreferences(this, complicationId).teamNumber
        } else ""

        setContent {
            MaterialTheme {
                ConfigScreen(
                    initialTeam = existingTeam,
                    onSave = { teamNumber -> saveAndFinish(teamNumber) },
                )
            }
        }
    }

    private fun saveAndFinish(teamNumber: String) {
        Log.d(TAG, "saveAndFinish: team=$teamNumber, complicationId=$complicationId")
        if (complicationId >= 0 && teamNumber.isNotBlank()) {
            val prefs = TeamTrackingComplicationPreferences(this, complicationId)
            prefs.teamNumber = teamNumber
            TeamTrackingComplicationPreferences.addComplicationId(this, complicationId)

            TeamTrackingComplicationWorker.enqueueImmediateRefresh(this)

            ComplicationDataSourceUpdateRequester.create(
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

@Composable
private fun ConfigScreen(
    initialTeam: String,
    onSave: (String) -> Unit,
) {
    val textFieldState = rememberTextFieldState()

    fun save() {
        val value = textFieldState.text.toString().filter { it.isDigit() }
        onSave(value.ifBlank { initialTeam })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (initialTeam.isNotBlank()) "Change Team" else "Team Number",
            style = MaterialTheme.typography.title3,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        BasicTextField(
            state = textFieldState,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = MaterialTheme.shapes.small,
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            textStyle = TextStyle(
                color = MaterialTheme.colors.onSurface,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            ),
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            lineLimits = androidx.compose.foundation.text.input.TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            decorator = { innerTextField ->
                if (textFieldState.text.isEmpty() && initialTeam.isNotBlank()) {
                    Text(
                        text = initialTeam,
                        style = TextStyle(
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                innerTextField()
            },
        )
        Button(
            onClick = { save() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        ) {
            Text("Save")
        }
    }
}
