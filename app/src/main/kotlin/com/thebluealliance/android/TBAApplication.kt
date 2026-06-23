package com.thebluealliance.android

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.thebluealliance.android.config.ApiKeyProvider
import com.thebluealliance.android.messaging.NotificationChannelManager
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.widget.TeamTrackingWidget
import com.thebluealliance.android.widget.TeamTrackingWidgetReceiver
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

        MainScope().launch(Dispatchers.Default) {
            val widgetManager = GlanceAppWidgetManager(this@TBAApplication)

            // Refresh bound widgets so PendingIntents stay fresh after app updates
            try {
                if (widgetManager.getGlanceIds(TeamTrackingWidget::class.java).isNotEmpty()) {
                    TeamTrackingWidget().updateAll(this@TBAApplication)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to refresh widgets", e)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                publishWidgetPreviewsForNewVersion(widgetManager)
            }
        }
    }

    // Previews only change when new code ships, so skip the expensive
    // compose-and-Binder round trip on routine process starts (FCM, WorkManager).
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private suspend fun publishWidgetPreviewsForNewVersion(widgetManager: GlanceAppWidgetManager) {
        val prefs = getSharedPreferences(WIDGET_PREVIEW_PREFS, MODE_PRIVATE)
        if (prefs.getInt(KEY_PREVIEW_VERSION_CODE, -1) == BuildConfig.VERSION_CODE) return
        try {
            val result = widgetManager.setWidgetPreviews(TeamTrackingWidgetReceiver::class)
            if (result == GlanceAppWidgetManager.SET_WIDGET_PREVIEWS_RESULT_SUCCESS) {
                prefs.edit { putInt(KEY_PREVIEW_VERSION_CODE, BuildConfig.VERSION_CODE) }
            }
        } catch (e: IllegalArgumentException) {
            // Some devices (notably Android 16/17 betas) don't have the provider
            // registered in AppWidgetServiceImpl during background cold starts.
            Log.w(TAG, "Failed to set widget previews", e)
        }
    }

    companion object {
        private const val TAG = "TBAApplication"
        private const val WIDGET_PREVIEW_PREFS = "widget_previews"
        private const val KEY_PREVIEW_VERSION_CODE = "last_published_version_code"
    }
}
