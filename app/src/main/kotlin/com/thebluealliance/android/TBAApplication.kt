package com.thebluealliance.android

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.thebluealliance.android.config.ApiKeyProvider
import com.thebluealliance.android.messaging.NotificationChannelManager
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.widget.TeamTrackingWidgetReceiver
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class TBAApplication : Application(), Configuration.Provider {

    @Inject lateinit var apiKeyProvider: ApiKeyProvider
    @Inject lateinit var notificationChannelManager: NotificationChannelManager
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var shortcutManager: TBAShortcutManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        apiKeyProvider.init()
        notificationChannelManager.createChannels()
        shortcutManager.beginSyncingShortcuts()

        // Publish generated widget previews for the widget picker (API 35+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            MainScope().launch {
                val component = ComponentName(packageName, TeamTrackingWidgetReceiver::class.java.name)
                val isRegistered = AppWidgetManager.getInstance(this@TBAApplication)
                    .getInstalledProvidersForPackage(packageName, null)
                    .any { it.provider == component }

                if (!isRegistered) {
                    Log.w(
                        "TBAApplication",
                        "Widget provider $component not registered with AppWidgetManager; " +
                            "skipping setWidgetPreviews (API ${Build.VERSION.SDK_INT})"
                    )
                    return@launch
                }

                try {
                    GlanceAppWidgetManager(this@TBAApplication)
                        .setWidgetPreviews(TeamTrackingWidgetReceiver::class)
                } catch (e: IllegalArgumentException) {
                    Log.w("TBAApplication", "Failed to set widget previews", e)
                }
            }
        }
    }
}
