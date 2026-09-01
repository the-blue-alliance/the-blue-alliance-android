package com.thebluealliance.android.widget

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import com.thebluealliance.android.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlanceWidgetRefresher
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : WidgetRefresher {
        override suspend fun refresh() {
            val widgetManager = GlanceAppWidgetManager(context)

            // Refresh bound widgets so PendingIntents stay fresh after app updates
            try {
                if (widgetManager.getGlanceIds(TeamTrackingWidget::class.java).isNotEmpty()) {
                    TeamTrackingWidget().updateAll(context)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to refresh widgets", e)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                publishPreviewsForNewVersion(widgetManager)
            }
        }

        // Previews only change when new code ships, so skip the expensive
        // compose-and-Binder round trip on routine process starts (FCM, WorkManager).
        @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
        private suspend fun publishPreviewsForNewVersion(widgetManager: GlanceAppWidgetManager) {
            val prefs = context.getSharedPreferences(WIDGET_PREVIEW_PREFS, Context.MODE_PRIVATE)
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

        private companion object {
            const val TAG = "WidgetRefresher"
            const val WIDGET_PREVIEW_PREFS = "widget_previews"
            const val KEY_PREVIEW_VERSION_CODE = "last_published_version_code"
        }
    }
