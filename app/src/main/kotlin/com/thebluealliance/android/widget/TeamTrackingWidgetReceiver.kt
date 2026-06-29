package com.thebluealliance.android.widget

import android.appwidget.AppWidgetManager
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
        WidgetMetricsWorker.enqueueDaily(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // Self-heal: widgets installed before metrics shipped already fired onEnabled and won't
        // re-fire it on app update, so schedule here too. KEEP makes it idempotent and it only
        // runs when a widget actually exists (never wakes widget-less devices).
        WidgetMetricsWorker.enqueueDaily(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        // Cancel periodic work when last widget is removed
        TeamTrackingWorker.cancelPeriodicRefresh(context)
        WidgetMetricsWorker.cancel(context)
    }
}
