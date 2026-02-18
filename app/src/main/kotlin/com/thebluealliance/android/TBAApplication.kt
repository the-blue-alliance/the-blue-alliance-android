package com.thebluealliance.android

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.thebluealliance.android.config.ApiKeyProvider
import com.thebluealliance.android.messaging.NotificationChannelManager
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import dagger.hilt.android.HiltAndroidApp
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
    }
}
