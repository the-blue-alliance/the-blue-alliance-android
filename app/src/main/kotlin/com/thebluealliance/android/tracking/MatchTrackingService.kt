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
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MatchTrackingService : Service() {

    @Inject lateinit var matchRepository: MatchRepository
    @Inject lateinit var eventRepository: EventRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var observeJob: Job? = null
    private var tickerJob: Job? = null
    private var refreshJob: Job? = null

    /** Latest matches from Room, used by the ticker to re-evaluate state. */
    private var latestMatches: List<Match> = emptyList()
    private var latestPlayoffType: PlayoffType = PlayoffType.OTHER

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
        _activeTeamKey.value = teamKey
        activeEventKey = eventKey

        // Post an initial notification to start foreground immediately
        val initialState = TrackedTeamState(
            teamKey = teamKey,
            eventKey = eventKey,
            playoffType = PlayoffType.OTHER,
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

        // Start observing match data + event playoff type from Room
        observeJob?.cancel()
        observeJob = scope.launch {
            combine(
                matchRepository.observeEventMatches(eventKey),
                eventRepository.observeEvent(eventKey),
            ) { matches, event ->
                matches to (event?.playoffType ?: PlayoffType.OTHER)
            }.collectLatest { (matches, playoffType) ->
                latestMatches = matches
                latestPlayoffType = playoffType
                updateNotification(teamKey, eventKey)
            }
        }

        // Re-evaluate state every 60s with fresh time (handles "Now" transitions)
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (true) {
                delay(TICKER_INTERVAL_MS)
                updateNotification(teamKey, eventKey)
            }
        }

        // Poll API every 5 min as a safety net for missed FCM pushes
        refreshJob?.cancel()
        refreshJob = scope.launch {
            while (true) {
                delay(API_REFRESH_INTERVAL_MS)
                Log.d(TAG, "Periodic API refresh for $eventKey")
                matchRepository.refreshEventMatches(eventKey)
            }
        }

        return START_STICKY
    }

    private fun updateNotification(teamKey: String, eventKey: String) {
        val now = System.currentTimeMillis()
        val state = computeTrackedTeamState(
            matches = latestMatches,
            teamKey = teamKey,
            eventKey = eventKey,
            playoffType = latestPlayoffType,
            currentTimeMillis = now,
        )

        // Auto-dismiss if 2h past the last match time
        if (state.autoDismissAfter != null && now >= state.autoDismissAfter) {
            Log.d(TAG, "Auto-dismissing: 2h past last match")
            stopTracking()
            return
        }

        val notification = MatchTrackingNotificationBuilder.build(this, state)
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(MatchTrackingNotificationBuilder.NOTIFICATION_ID, notification)
    }

    private fun stopTracking() {
        observeJob?.cancel()
        tickerJob?.cancel()
        refreshJob?.cancel()
        _activeTeamKey.value = null
        activeEventKey = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        scope.cancel()
        _activeTeamKey.value = null
        activeEventKey = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MatchTracking"
        private const val TICKER_INTERVAL_MS = 60_000L
        private const val API_REFRESH_INTERVAL_MS = 5 * 60_000L

        const val EXTRA_TEAM_KEY = "team_key"
        const val EXTRA_EVENT_KEY = "event_key"
        const val ACTION_STOP = "com.thebluealliance.android.tracking.STOP"

        // Simple static tracking state — one tracker at a time
        private val _activeTeamKey = MutableStateFlow<String?>(null)
        val activeTeamKey: StateFlow<String?> = _activeTeamKey.asStateFlow()

        var activeEventKey: String? = null
            private set

        val isTracking: Boolean get() = _activeTeamKey.value != null

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
