package com.thebluealliance.android.tracking

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import com.thebluealliance.android.data.repository.MatchRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MatchTrackingService : Service() {

    @Inject lateinit var matchRepository: MatchRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var observeJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            Log.d(TAG, "Stop action received")
            stopTracking()
            return START_NOT_STICKY
        }

        val teamKey = intent?.getStringExtra(EXTRA_TEAM_KEY) ?: run {
            Log.w(TAG, "No team key provided")
            stopSelf()
            return START_NOT_STICKY
        }
        val eventKey = intent.getStringExtra(EXTRA_EVENT_KEY) ?: run {
            Log.w(TAG, "No event key provided")
            stopSelf()
            return START_NOT_STICKY
        }

        Log.d(TAG, "Starting tracking: team=$teamKey event=$eventKey")
        activeTeamKey = teamKey
        activeEventKey = eventKey

        // Post an initial notification to start foreground immediately
        val initialState = TrackedTeamState(
            teamKey = teamKey,
            eventKey = eventKey,
            nextMatch = null,
            currentMatch = null,
            lastMatch = null,
            isTeamPlaying = false,
            record = null,
            autoDismissAfter = null,
        )
        val notification = MatchTrackingNotificationBuilder.build(this, initialState)
        if (Build.VERSION.SDK_INT >= 34) {
            ServiceCompat.startForeground(
                this,
                MatchTrackingNotificationBuilder.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(MatchTrackingNotificationBuilder.NOTIFICATION_ID, notification)
        }

        // Start observing match data
        observeJob?.cancel()
        observeJob = scope.launch {
            matchRepository.observeEventMatches(eventKey).collectLatest { matches ->
                val now = System.currentTimeMillis()
                val state = computeTrackedTeamState(
                    matches = matches,
                    teamKey = teamKey,
                    eventKey = eventKey,
                    currentTimeMillis = now,
                )

                // Auto-dismiss if 2h past the last match time
                if (state.autoDismissAfter != null && now >= state.autoDismissAfter) {
                    Log.d(TAG, "Auto-dismissing: 2h past last match")
                    stopTracking()
                    return@collectLatest
                }

                val updatedNotification = MatchTrackingNotificationBuilder.build(
                    this@MatchTrackingService, state,
                )
                val nm = getSystemService(NotificationManager::class.java)
                nm.notify(MatchTrackingNotificationBuilder.NOTIFICATION_ID, updatedNotification)
            }
        }

        return START_STICKY
    }

    private fun stopTracking() {
        observeJob?.cancel()
        activeTeamKey = null
        activeEventKey = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        scope.cancel()
        activeTeamKey = null
        activeEventKey = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MatchTracking"
        const val EXTRA_TEAM_KEY = "team_key"
        const val EXTRA_EVENT_KEY = "event_key"
        const val ACTION_STOP = "com.thebluealliance.android.tracking.STOP"

        // Simple static tracking state — one tracker at a time
        var activeTeamKey: String? = null
            private set
        var activeEventKey: String? = null
            private set

        val isTracking: Boolean get() = activeTeamKey != null

        fun start(context: Context, teamKey: String, eventKey: String) {
            val intent = Intent(context, MatchTrackingService::class.java).apply {
                putExtra(EXTRA_TEAM_KEY, teamKey)
                putExtra(EXTRA_EVENT_KEY, eventKey)
            }
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, MatchTrackingService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
