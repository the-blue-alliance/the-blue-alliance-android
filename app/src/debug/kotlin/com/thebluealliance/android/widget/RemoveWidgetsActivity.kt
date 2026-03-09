package com.thebluealliance.android.widget

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

/**
 * Debug-only activity that removes all Team Tracking widget instances from the home screen.
 * Used for screenshot automation — never included in release builds.
 *
 * Usage:
 *   adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.widget.RemoveWidgetsActivity
 */
class RemoveWidgetsActivity : ComponentActivity() {

    companion object {
        private const val TAG = "RemoveWidgetsActivity"
        // Pixel Launcher's host ID
        private const val LAUNCHER_HOST_ID = 1024
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val awm = AppWidgetManager.getInstance(this)
        val provider = ComponentName(this, TeamTrackingWidgetReceiver::class.java)
        val widgetIds = awm.getAppWidgetIds(provider)

        if (widgetIds.isEmpty()) {
            Log.d(TAG, "No widgets to remove")
        } else {
            val host = AppWidgetHost(this, LAUNCHER_HOST_ID)
            for (id in widgetIds) {
                Log.d(TAG, "Removing widget id=$id")
                host.deleteAppWidgetId(id)
            }
            Log.d(TAG, "Removed ${widgetIds.size} widget(s)")
        }

        finish()
    }
}
