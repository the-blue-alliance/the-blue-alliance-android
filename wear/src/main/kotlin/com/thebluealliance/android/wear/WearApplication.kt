package com.thebluealliance.android.wear

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.thebluealliance.android.core.network.ApiKeyProvider
import com.thebluealliance.android.wear.tracker.TeamTrackerPreferences
import com.thebluealliance.android.wear.worker.TeamTrackingComplicationWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WearApplication :
    Application(),
    Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var apiKeyProvider: ApiKeyProvider

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .build()

    override fun onCreate() {
        super.onCreate()
        apiKeyProvider.init()
        // Only schedule the periodic refresh when a team is actually tracked;
        // otherwise the worker wakes every 6h to do nothing. Setting a team
        // schedules it from the tracker/config activities.
        if (TeamTrackerPreferences(this).teamNumber.isNotBlank()) {
            TeamTrackingComplicationWorker.enqueuePeriodicRefresh(this)
        }
    }
}
