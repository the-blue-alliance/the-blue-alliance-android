package com.thebluealliance.android.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class TeamTrackingWidgetRefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val workRequest = OneTimeWorkRequestBuilder<TeamTrackingWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
