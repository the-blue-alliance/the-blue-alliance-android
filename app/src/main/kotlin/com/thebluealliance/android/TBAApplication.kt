package com.thebluealliance.android

import android.app.Application
import android.os.Build
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
                GlanceAppWidgetManager(this@TBAApplication)
                    .setWidgetPreviews(TeamTrackingWidgetReceiver::class)
            }
        }
    }
}
