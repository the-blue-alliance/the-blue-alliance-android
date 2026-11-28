package com.thebluealliance.android.ui.matches

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.domain.model.withPlayoffAlliances
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.UserMessageBus
import com.thebluealliance.android.ui.common.isNotFound
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel(assistedFactory = MatchDetailViewModel.Factory::class)
class MatchDetailViewModel
    @AssistedInject
    constructor(
        @Assisted val navKey: Screen.MatchDetail,
        private val matchRepository: MatchRepository,
        private val eventRepository: EventRepository,
        private val userMessages: UserMessageBus,
    ) : RefreshableViewModel() {
        private val matchKey: String = navKey.matchKey
        private val eventKey: String = matchKey.substringBeforeLast("_")
        private val year: Int = matchKey.substring(0, 4).toIntOrNull() ?: 0

        private val refreshOutcome = MutableStateFlow(RefreshOutcome.PENDING)

        private val matchMissing = MutableStateFlow(false)

        private val timeFormatter =
            DateTimeFormatter.ofPattern(
                "EEE, MMM d, yyyy 'at' h:mm a",
                Locale.US,
            )

        val uiState: StateFlow<MatchDetailUiState> =
            combine(
                matchRepository.observeMatch(matchKey),
                eventRepository.observeEvent(eventKey),
                eventRepository.observeEventAlliances(eventKey),
                refreshOutcome,
                matchMissing,
            ) { match, event, alliances, outcome, missing ->
                val breakdown = match?.scoreBreakdown?.let { parseScoreBreakdown(it) }
                val videos = match?.videos?.let { parseVideos(it) } ?: emptyList()
                val timeToDisplay = match?.actualTime ?: match?.predictedTime ?: match?.time
                val isEstimate =
                    match?.actualTime == null &&
                        match?.predictedTime != null &&
                        match.time != null &&
                        kotlin.math.abs(match.predictedTime - match.time) > 60
                val formattedTime =
                    formatMatchTime(timeToDisplay)?.let {
                        if (isEstimate) "$it (est.)" else it
                    }
                MatchDetailUiState(
                    match = match?.withPlayoffAlliances(alliances),
                    scoreBreakdown = breakdown,
                    eventName = event?.name,
                    eventKey = event?.key,
                    playoffType = event?.playoffType ?: PlayoffType.OTHER,
                    formattedTime = formattedTime,
                    videos = videos,
                    year = year,
                    // Only give up on an empty cache once a refresh has actually finished.
                    loadFailed = match == null && !missing && outcome != RefreshOutcome.PENDING,
                    matchMissing = missing,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MatchDetailUiState())

        init {
            refresh()
            viewModelScope.launch {
                // Fetch event only if not already cached (e.g. deep-linked to match)
                val cached = eventRepository.observeEvent(eventKey).first()
                if (cached == null) {
                    try {
                        eventRepository.refreshEvent(eventKey)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        fun refresh() {
            refreshingTracked(refreshOutcome, { refreshMatch() })
        }

        // Records whether TBA has the match at all, so a 404 shows "not found", not a load error.
        private suspend fun refreshMatch() {
            try {
                matchRepository.refreshMatch(matchKey)
                matchMissing.value = false
            } catch (e: Exception) {
                if (e.isNotFound()) matchMissing.value = true
                throw e
            }
        }

        private fun formatMatchTime(epochSeconds: Long?): String? {
            if (epochSeconds == null) return null
            val instant = Instant.ofEpochSecond(epochSeconds)
            return timeFormatter.format(instant.atZone(ZoneId.systemDefault()))
        }

        private fun parseVideos(jsonString: String): List<MatchVideo> {
            return try {
                val array = Json.decodeFromString<JsonArray>(jsonString)
                array.mapNotNull { element ->
                    val obj = element as? JsonObject ?: return@mapNotNull null
                    val type = obj["type"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val key = obj["key"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    MatchVideo(type = type, key = key)
                }
            } catch (_: Exception) {
                emptyList()
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.MatchDetail): MatchDetailViewModel
        }
    }

/** Per-alliance breakdown values as strings; null values are left out so they show as missing. */
internal fun parseScoreBreakdown(jsonString: String): Map<String, Map<String, String>>? =
    try {
        val obj = Json.decodeFromString<JsonObject>(jsonString)
        val result = mutableMapOf<String, Map<String, String>>()
        for (alliance in listOf("red", "blue")) {
            val allianceObj = obj[alliance] as? JsonObject ?: continue
            result[alliance] =
                allianceObj.entries
                    .filter { (_, v) -> v is JsonPrimitive && v !is JsonNull }
                    .associate { (k, v) -> k to v.jsonPrimitive.content }
        }
        result.ifEmpty { null }
    } catch (_: Exception) {
        null
    }
