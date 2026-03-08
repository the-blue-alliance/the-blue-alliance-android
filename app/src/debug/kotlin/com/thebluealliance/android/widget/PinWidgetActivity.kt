package com.thebluealliance.android.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.launch

/**
 * Debug-only activity that programmatically requests pinning the Team Tracking widget,
 * optionally pre-configured with a team number.
 * Used for screenshot automation — never included in release builds.
 *
 * Usage:
 *   # Pin unconfigured widget:
 *   adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.widget.PinWidgetActivity
 *
 *   # Pin and configure with team 604:
 *   adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.widget.PinWidgetActivity --es team 604
 */
class PinWidgetActivity : ComponentActivity() {

    companion object {
        private const val TAG = "PinWidgetActivity"
        private const val EXTRA_TEAM = "team"
        private const val ACTION_WIDGET_PINNED = "com.thebluealliance.android.widget.WIDGET_PINNED"
        private const val REQUEST_PIN = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val teamNumber = intent?.getStringExtra(EXTRA_TEAM)

        val awm = AppWidgetManager.getInstance(this)
        val provider = ComponentName(this, TeamTrackingWidgetReceiver::class.java)

        if (!awm.isRequestPinAppWidgetSupported) {
            Log.e(TAG, "Pin widget not supported on this launcher")
            finish()
            return
        }

        // Pass team number through extras so the config activity can auto-fill
        val extras = if (teamNumber != null) {
            Bundle().apply { putString(EXTRA_TEAM, teamNumber) }
        } else {
            null
        }

        if (teamNumber != null) {
            // Create a callback PendingIntent so we get the appWidgetId after pinning
            val callbackIntent = Intent(ACTION_WIDGET_PINNED).setPackage(packageName)
            callbackIntent.putExtra(EXTRA_TEAM, teamNumber)
            val callbackPending = PendingIntent.getBroadcast(
                this, REQUEST_PIN, callbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )
            awm.requestPinAppWidget(provider, extras, callbackPending)
        } else {
            awm.requestPinAppWidget(provider, extras, null)
        }

        finish()
    }
}
