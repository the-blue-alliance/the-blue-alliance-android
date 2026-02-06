package com.thebluealliance.android

import android.app.Application
import com.thebluealliance.android.config.ApiKeyProvider
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TBAApplication : Application() {

    @Inject lateinit var apiKeyProvider: ApiKeyProvider

    override fun onCreate() {
        super.onCreate()
        apiKeyProvider.init()
    }
}
