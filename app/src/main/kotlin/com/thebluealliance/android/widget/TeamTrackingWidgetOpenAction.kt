package com.thebluealliance.android.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.thebluealliance.android.MainActivity
import com.thebluealliance.android.messaging.NotificationBuilder
import com.thebluealliance.android.navigation.Screen

class TeamTrackingWidgetOpenAction : ActionCallback {
    companion object {
        const val EXTRA_INITIAL_TAB = "initial_tab"
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val state = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
        val teamKey = state[TeamTrackingWidgetKeys.TEAM_KEY]

        if (teamKey == null) {
            // No team configured — open config. Must always call startActivity;
            // returning without one crashes the Glance action trampoline.
            val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
            val intent =
                Intent(context, TeamTrackingWidgetConfigActivity::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            context.startActivity(intent)
            return
        }

        val eventKey = state[TeamTrackingWidgetKeys.EVENT_KEY]
        val intent =
            Intent(context, MainActivity::class.java).apply {
                putExtra(NotificationBuilder.EXTRA_TEAM_KEY, teamKey)
                if (eventKey != null) {
                    putExtra(NotificationBuilder.EXTRA_EVENT_KEY, eventKey)
                } else {
                    putExtra(EXTRA_INITIAL_TAB, Screen.TeamDetail.TAB_EVENTS)
                }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        context.startActivity(intent)
    }
}
