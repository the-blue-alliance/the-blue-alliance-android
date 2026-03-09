package com.thebluealliance.android.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Debug-only receiver that configures a newly-pinned widget with a team number.
 * Triggered by the PendingIntent callback from [PinWidgetActivity].
 */
class PinWidgetReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "PinWidgetReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val teamNumber = intent.getStringExtra("team") ?: return
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        )

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.e(TAG, "No valid appWidgetId in pin callback")
            return
        }

        Log.d(TAG, "Configuring widget $appWidgetId with team $teamNumber")

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val manager = GlanceAppWidgetManager(context)
                val glanceId = manager.getGlanceIdBy(appWidgetId)

                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[TeamTrackingWidgetKeys.TEAM_NUMBER] = teamNumber
                    prefs[TeamTrackingWidgetKeys.TEAM_KEY] = "frc$teamNumber"
                }

                TeamTrackingWidget().update(context, glanceId)

                TeamTrackingWorker.enqueuePeriodicRefresh(context)
                WorkManager.getInstance(context)
                    .enqueue(OneTimeWorkRequestBuilder<TeamTrackingWorker>().build())

                Log.d(TAG, "Widget $appWidgetId configured for team $teamNumber")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to configure widget", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
