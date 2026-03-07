package com.thebluealliance.android.tracking

import android.content.Context
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

sealed class TrackingResult {
    data class Started(val teamKey: String, val eventKey: String) : TrackingResult()
    data class NotCompeting(val teamNumber: String) : TrackingResult()
    data class Error(val message: String) : TrackingResult()
}

@Singleton
class MatchTrackingManager @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val eventRepository: EventRepository,
    private val matchRepository: MatchRepository,
) {
    /**
     * Finds the team's current event and starts the tracking service.
     * Returns a [TrackingResult] indicating what happened.
     */
    suspend fun startTracking(teamKey: String): TrackingResult {
        return try {
            val year = LocalDate.now().year
            eventRepository.refreshTeamEvents(teamKey, year)

            val today = LocalDate.now()
            val events = eventRepository.observeTeamEvents(teamKey, year).first()
            val currentEvents = events.filter { event ->
                val start = event.startDate?.let { LocalDate.parse(it) }
                val end = event.endDate?.let { LocalDate.parse(it) }
                if (start != null && end != null) {
                    !today.isBefore(start) && !today.isAfter(end.plusDays(1))
                } else false
            }

            when {
                currentEvents.isEmpty() -> {
                    TrackingResult.NotCompeting(teamKey.removePrefix("frc"))
                }
                else -> {
                    // Multiple events — for now, pick the first one
                    // TODO: Show picker dialog for multiple events
                    val event = currentEvents.first()
                    matchRepository.refreshEventMatches(event.key)
                    MatchTrackingService.start(appContext, teamKey, event.key)
                    TrackingResult.Started(teamKey, event.key)
                }
            }
        } catch (e: Exception) {
            TrackingResult.Error(e.message ?: "Unknown error")
        }
    }

    fun stopTracking() {
        MatchTrackingService.stop(appContext)
    }
}
