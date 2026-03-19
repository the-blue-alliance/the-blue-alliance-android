package com.thebluealliance.android.wear

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.thebluealliance.android.wear.data.ApiKeyProvider
import com.thebluealliance.android.wear.worker.TeamTrackingComplicationWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WearApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var apiKeyProvider: ApiKeyProvider

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        apiKeyProvider.init()
        TeamTrackingComplicationWorker.enqueuePeriodicRefresh(this)
    }
}
