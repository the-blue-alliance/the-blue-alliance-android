package com.thebluealliance.android

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.thebluealliance.android.config.ApiKeyProvider
import com.thebluealliance.android.messaging.NotificationChannelManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TBAApplication : Application(), Configuration.Provider {

    @Inject lateinit var apiKeyProvider: ApiKeyProvider
    @Inject lateinit var notificationChannelManager: NotificationChannelManager
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        apiKeyProvider.init()
        notificationChannelManager.createChannels()
    }
}
