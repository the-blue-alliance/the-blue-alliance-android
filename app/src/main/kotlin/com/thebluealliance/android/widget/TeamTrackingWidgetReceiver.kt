package com.thebluealliance.android.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class TeamTrackingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TeamTrackingWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // Schedule periodic refresh when first widget is added.
        // The config activity handles the first real refresh after the user picks a team.
        TeamTrackingWorker.enqueuePeriodicRefresh(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        // Cancel periodic refresh when last widget is removed
        TeamTrackingWorker.cancelPeriodicRefresh(context)
    }
}
