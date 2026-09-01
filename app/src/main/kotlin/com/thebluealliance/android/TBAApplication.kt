package com.thebluealliance.android

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.thebluealliance.android.core.network.ApiKeyProvider
import com.thebluealliance.android.messaging.NotificationChannelManager
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.widget.WidgetRefresher
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class TBAApplication :
    Application(),
    Configuration.Provider,
    SingletonImageLoader.Factory {
    @Inject lateinit var apiKeyProvider: ApiKeyProvider

    @Inject lateinit var notificationChannelManager: NotificationChannelManager

    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var shortcutManager: TBAShortcutManager

    @Inject lateinit var widgetRefresher: WidgetRefresher

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .build()

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader
            .Builder(context)
            .build()

    override fun onCreate() {
        super.onCreate()
        apiKeyProvider.init()
        notificationChannelManager.createChannels()
        shortcutManager.beginSyncingShortcuts()

        MainScope().launch(Dispatchers.Default) { widgetRefresher.refresh() }
    }
}
