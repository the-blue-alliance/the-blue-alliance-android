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
import java.time.LocalDate
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

    /** Cached state from latest notification update, used for adaptive polling/ticker. */
    @Volatile private var lastState: TrackedTeamState? = null

    /** True when the service is in dormant mode (overnight between event days). */
    private var isDormant = false

    /** Event end date, used to decide dormant vs full stop. Null = single-day/unknown. */
    private var eventEndDate: LocalDate? = null

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
        isDormant = false
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
                Triple(matches, event?.playoffType ?: PlayoffType.OTHER, event?.endDate)
            }.collectLatest { (matches, playoffType, endDate) ->
                latestMatches = matches
                latestPlayoffType = playoffType
                eventEndDate = endDate?.runCatching { LocalDate.parse(this) }?.getOrNull()

                if (isDormant) {
                    // Check if we should exit dormant mode
                    val state = computeTrackedTeamState(
                        matches = matches,
                        teamKey = teamKey,
                        eventKey = eventKey,
                        playoffType = playoffType,
                        currentTimeMillis = System.currentTimeMillis(),
                    )
                    if (state.nextMatch != null || state.isTeamPlaying) {
                        exitDormantMode(teamKey, eventKey)
                    }
                } else {
                    updateNotification(teamKey, eventKey)
                }
            }
        }

        // Re-evaluate state periodically with fresh time (handles "Now" transitions).
        // Interval adapts: faster near match time, slower during gaps.
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (true) {
                val tickDelay = computeNextTickerDelay(lastState)
                Log.d(TAG, "Next ticker in ${tickDelay / 1000}s")
                delay(tickDelay)
                updateNotification(teamKey, eventKey)
            }
        }

        // Poll API as a safety net for missed FCM pushes.
        // Interval adapts: faster near match time, slower during gaps.
        refreshJob?.cancel()
        refreshJob = scope.launch {
            while (true) {
                val pollDelay = if (isDormant) POLL_SLOW_MS else computeNextPollDelay(lastState)
                Log.d(TAG, "Next API poll in ${pollDelay / 1000}s")
                delay(pollDelay)
                Log.d(TAG, "Periodic API refresh for $eventKey")
                matchRepository.refreshEventMatches(eventKey)

                // While dormant, check if the event has ended
                if (isDormant) {
                    val endDate = eventEndDate
                    if (endDate != null && LocalDate.now().isAfter(endDate)) {
                        Log.d(TAG, "Auto-dismissing: event ended while dormant")
                        stopTracking()
                        return@launch
                    }
                }
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
        lastState = state

        // Auto-dismiss if 2h past the last match time — enter dormant if event continues
        if (state.autoDismissAfter != null && now >= state.autoDismissAfter) {
            if (shouldEnterDormant(eventEndDate, LocalDate.now())) {
                enterDormantMode(teamKey, eventKey)
            } else {
                Log.d(TAG, "Auto-dismissing: 2h past last match")
                stopTracking()
            }
            return
        }

        val notification = MatchTrackingNotificationBuilder.build(this, state)
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(MatchTrackingNotificationBuilder.NOTIFICATION_ID, notification)
    }

    private fun enterDormantMode(teamKey: String, eventKey: String) {
        Log.d(TAG, "Entering dormant mode for team=$teamKey event=$eventKey")
        isDormant = true
        tickerJob?.cancel()
        tickerJob = null

        val notification = MatchTrackingNotificationBuilder.buildDormant(this, teamKey)
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(MatchTrackingNotificationBuilder.NOTIFICATION_ID, notification)
    }

    private fun exitDormantMode(teamKey: String, eventKey: String) {
        Log.d(TAG, "Exiting dormant mode for team=$teamKey event=$eventKey")
        isDormant = false

        // Restart ticker
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (true) {
                val tickDelay = computeNextTickerDelay(lastState)
                Log.d(TAG, "Next ticker in ${tickDelay / 1000}s")
                delay(tickDelay)
                updateNotification(teamKey, eventKey)
            }
        }

        updateNotification(teamKey, eventKey)
    }

    private fun stopTracking() {
        observeJob?.cancel()
        tickerJob?.cancel()
        refreshJob?.cancel()
        isDormant = false
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

        // Adaptive API polling tiers
        internal const val POLL_FAST_MS = 5 * 60_000L         // 5 min — match imminent or in progress
        internal const val POLL_MEDIUM_MS = 15 * 60_000L      // 15 min — match within the hour
        internal const val POLL_SLOW_MS = 60 * 60_000L        // 60 min — no match soon / overnight
        private const val POLL_FAST_THRESHOLD_MS = 15 * 60_000L   // < 15 min to next match
        private const val POLL_MEDIUM_THRESHOLD_MS = 60 * 60_000L // < 60 min to next match

        // Adaptive ticker
        internal const val TICKER_DEFAULT_MS = 5 * 60_000L    // 5 min default
        internal const val TICKER_MIN_MS = 60_000L             // 1 min floor
        private const val TICKER_PRE_MATCH_MS = 60_000L        // wake 1 min before match

        const val EXTRA_TEAM_KEY = "team_key"
        const val EXTRA_EVENT_KEY = "event_key"
        const val ACTION_STOP = "com.thebluealliance.android.tracking.STOP"

        // Simple static tracking state — one tracker at a time
        private val _activeTeamKey = MutableStateFlow<String?>(null)
        val activeTeamKey: StateFlow<String?> = _activeTeamKey.asStateFlow()

        var activeEventKey: String? = null
            private set

        val isTracking: Boolean get() = _activeTeamKey.value != null

        /**
         * Whether to enter dormant mode instead of fully stopping.
         * True when the event has a known end date that hasn't passed yet.
         */
        internal fun shouldEnterDormant(eventEndDate: LocalDate?, today: LocalDate): Boolean {
            return eventEndDate != null && !today.isAfter(eventEndDate)
        }

        /**
         * How often to poll the API, based on how soon the team plays next.
         *
         * | Scenario                         | Interval |
         * |----------------------------------|----------|
         * | No state yet / match in progress | 5 min    |
         * | Next match exists, no time info  | 5 min    |
         * | Next match < 15 min away         | 5 min    |
         * | Next match 15–60 min away        | 15 min   |
         * | Next match > 60 min away         | 60 min   |
         * | No next match, has last match    | 15 min   |
         * | No next match, no last match     | 60 min   |
         */
        internal fun computeNextPollDelay(
            state: TrackedTeamState?,
            now: Long = System.currentTimeMillis(),
        ): Long {
            if (state == null) return POLL_FAST_MS
            if (state.currentMatch != null) return POLL_FAST_MS

            val nextMatch = state.nextMatch
                ?: // Could be waiting for elim schedule — poll at medium to catch it.
                return if (state.lastMatch != null) POLL_MEDIUM_MS else POLL_SLOW_MS

            val nextMatchTime = nextMatch.predictedTime ?: nextMatch.time
                ?: // Match exists but no time — poll fast to discover when.
                return POLL_FAST_MS

            val timeToNextMs = nextMatchTime * 1000 - now
            return when {
                timeToNextMs < POLL_FAST_THRESHOLD_MS -> POLL_FAST_MS
                timeToNextMs < POLL_MEDIUM_THRESHOLD_MS -> POLL_MEDIUM_MS
                else -> POLL_SLOW_MS
            }
        }

        /**
         * How often to re-evaluate the notification against wall-clock time.
         *
         * | Scenario                           | Interval               |
         * |------------------------------------|------------------------|
         * | No state yet / no next match       | 5 min                  |
         * | Match in progress                  | 60s                    |
         * | Next match with time               | min(5 min, T−1 min)    |
         *
         * "T−1 min" means wake up 1 minute before the match's estimated start,
         * so the notification transitions to "now" promptly. Floored at 60s.
         */
        internal fun computeNextTickerDelay(
            state: TrackedTeamState?,
            now: Long = System.currentTimeMillis(),
        ): Long {
            if (state == null) return TICKER_DEFAULT_MS
            if (state.currentMatch != null) return TICKER_MIN_MS

            val nextMatchTime = state.nextMatch?.let { it.predictedTime ?: it.time }
                ?: return TICKER_DEFAULT_MS
            val timeToNextMs = nextMatchTime * 1000 - now
            val wakeBeforeMatch = timeToNextMs - TICKER_PRE_MATCH_MS
            return wakeBeforeMatch.coerceIn(TICKER_MIN_MS, TICKER_DEFAULT_MS)
        }

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
