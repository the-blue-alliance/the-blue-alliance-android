package com.thebluealliance.android.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Debug-only activity that reconfigures all widgets to a given team.
 *
 * Usage:
 *   adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.widget.ReconfigureWidgetsActivity --es team 125
 */
class ReconfigureWidgetsActivity : ComponentActivity() {

    companion object {
        private const val TAG = "ReconfigureWidgets"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val teamNumber = intent?.getStringExtra("team")
        if (teamNumber == null) {
            Log.e(TAG, "Missing --es team <number>")
            finish()
            return
        }

        val awm = AppWidgetManager.getInstance(this)
        val provider = ComponentName(this, TeamTrackingWidgetReceiver::class.java)
        val widgetIds = awm.getAppWidgetIds(provider)

        if (widgetIds.isEmpty()) {
            Log.d(TAG, "No widgets found")
            finish()
            return
        }

        Log.d(TAG, "Reconfiguring ${widgetIds.size} widget(s) to team $teamNumber")

        CoroutineScope(Dispatchers.IO).launch {
            val manager = GlanceAppWidgetManager(this@ReconfigureWidgetsActivity)

            for (id in widgetIds) {
                try {
                    val glanceId = manager.getGlanceIdBy(id)
                    updateAppWidgetState(this@ReconfigureWidgetsActivity, glanceId) { prefs ->
                        prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] = teamNumber
                        prefs[TeamTrackingWidgetKeys.TEAM_KEY] = "frc$teamNumber"
                        // Clear stale data so the worker fetches fresh
                        prefs.remove(TeamTrackingWidgetKeys.TEAM_NICKNAME)
                        prefs.remove(TeamTrackingWidgetKeys.AVATAR_BASE64)
                        prefs.remove(TeamTrackingWidgetKeys.EVENT_NAME)
                        prefs.remove(TeamTrackingWidgetKeys.RECORD)
                        prefs.remove(TeamTrackingWidgetKeys.UPCOMING_EVENTS)
                        for (k in TeamTrackingWidgetKeys.ALL_LAST_MATCH_KEYS) prefs.remove(k)
                        for (k in TeamTrackingWidgetKeys.ALL_NEXT_MATCH_KEYS) prefs.remove(k)
                    }
                    TeamTrackingWidget().update(this@ReconfigureWidgetsActivity, glanceId)
                    Log.d(TAG, "Widget $id reconfigured to team $teamNumber")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reconfigure widget $id", e)
                }
            }

            // Trigger a data refresh
            WorkManager.getInstance(this@ReconfigureWidgetsActivity)
                .enqueue(OneTimeWorkRequestBuilder<TeamTrackingWorker>().build())

            Log.d(TAG, "Done. Triggered data refresh.")
            finish()
        }
    }
}
