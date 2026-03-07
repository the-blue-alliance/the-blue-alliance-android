package com.thebluealliance.android.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class TeamTrackingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TeamTrackingWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // Schedule periodic refresh when first widget is added
        TeamTrackingWorker.enqueuePeriodicRefresh(context)
        // Also trigger an immediate refresh
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<TeamTrackingWorker>().build())
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        // Cancel periodic refresh when last widget is removed
        TeamTrackingWorker.cancelPeriodicRefresh(context)
    }
}
