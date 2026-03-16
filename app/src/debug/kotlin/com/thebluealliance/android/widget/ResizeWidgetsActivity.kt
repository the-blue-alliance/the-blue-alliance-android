package com.thebluealliance.android.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import android.util.SizeF
import androidx.activity.ComponentActivity
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Debug-only activity that resizes all Team Tracking widgets to different size tiers
 * so you can see all responsive layouts on the home screen at once.
 *
 * Usage:
 *   adb shell am start -n com.thebluealliance.androidclient.development/com.thebluealliance.android.widget.ResizeWidgetsActivity
 *
 * Or resize a single widget by index (0-based) to a specific size:
 *   adb shell am start -n ...ResizeWidgetsActivity --ei index 0 --es size 1x1
 *
 * Supported sizes: 1x1, 2x1, 2x2, 4x1, 4x2
 */
class ResizeWidgetsActivity : ComponentActivity() {

    companion object {
        private const val TAG = "ResizeWidgetsActivity"

        // Size tiers in dp matching TeamTrackingWidget.Companion
        private val SIZE_TIERS = listOf(
            "4x2" to Pair(250, 110),
            "4x1" to Pair(250, 60),
            "2x2" to Pair(110, 110),
            "2x1" to Pair(110, 60),
            "1x1" to Pair(60, 60),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val awm = AppWidgetManager.getInstance(this)
        val provider = ComponentName(this, TeamTrackingWidgetReceiver::class.java)
        val widgetIds = awm.getAppWidgetIds(provider)

        val singleIndex = intent?.getIntExtra("index", -1) ?: -1
        val singleSize = intent?.getStringExtra("size")

        if (widgetIds.isEmpty()) {
            Log.d(TAG, "No widgets found")
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val manager = GlanceAppWidgetManager(this@ResizeWidgetsActivity)

            if (singleIndex >= 0 && singleSize != null) {
                // Resize a single widget
                val dims = SIZE_TIERS.find { it.first == singleSize }?.second
                if (dims == null) {
                    Log.e(TAG, "Unknown size: $singleSize. Use: 1x1, 2x1, 2x2, 4x1, 4x2")
                    finish()
                    return@launch
                }
                if (singleIndex >= widgetIds.size) {
                    Log.e(TAG, "Index $singleIndex out of range (have ${widgetIds.size} widgets)")
                    finish()
                    return@launch
                }
                val id = widgetIds[singleIndex]
                resizeWidget(awm, manager, id, dims.first, dims.second, singleSize)
            } else {
                // Assign each widget a different size tier (cycle if more widgets than tiers)
                for ((i, id) in widgetIds.withIndex()) {
                    val (label, dims) = SIZE_TIERS[i % SIZE_TIERS.size]
                    resizeWidget(awm, manager, id, dims.first, dims.second, label)
                }
            }

            finish()
        }
    }

    private suspend fun resizeWidget(
        awm: AppWidgetManager,
        manager: GlanceAppWidgetManager,
        appWidgetId: Int,
        widthDp: Int,
        heightDp: Int,
        label: String,
    ) {
        try {
            val options = Bundle().apply {
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, widthDp)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, heightDp)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, heightDp)
                // Android 12+ uses OPTION_APPWIDGET_SIZES for Glance SizeMode.Responsive
                putParcelableArrayList(
                    AppWidgetManager.OPTION_APPWIDGET_SIZES,
                    arrayListOf(SizeF(widthDp.toFloat(), heightDp.toFloat())),
                )
            }
            awm.updateAppWidgetOptions(appWidgetId, options)

            val glanceId = manager.getGlanceIdBy(appWidgetId)
            TeamTrackingWidget().update(this, glanceId)

            Log.d(TAG, "Resized widget $appWidgetId to $label (${widthDp}x${heightDp}dp)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to resize widget $appWidgetId", e)
        }
    }
}
