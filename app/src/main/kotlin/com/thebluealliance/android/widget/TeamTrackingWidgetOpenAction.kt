package com.thebluealliance.android.widget

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
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
        val teamKey = state[TeamTrackingWidgetKeys.TEAM_KEY] ?: return
        val eventKey = state[TeamTrackingWidgetKeys.EVENT_KEY]

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(NotificationBuilder.EXTRA_TEAM_KEY, teamKey)
            if (eventKey != null) {
                putExtra(NotificationBuilder.EXTRA_EVENT_KEY, eventKey)
            } else {
                // No current event — open the Events tab of TeamDetail
                putExtra(EXTRA_INITIAL_TAB, Screen.TeamDetail.TAB_EVENTS)
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
