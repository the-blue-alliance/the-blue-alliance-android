package com.thebluealliance.android.ui.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.navigation.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel(assistedFactory = MatchDetailViewModel.Factory::class)
class MatchDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: Screen.MatchDetail,
    private val matchRepository: MatchRepository,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val matchKey: String = navKey.matchKey
    private val year: Int = matchKey.substring(0, 4).toIntOrNull() ?: 0

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    private val timeFormatter = DateTimeFormatter.ofPattern(
        "EEE, MMM d, yyyy 'at' h:mm a", Locale.US
    )

    val uiState: StateFlow<MatchDetailUiState> = combine(
        matchRepository.observeMatch(matchKey),
        eventRepository.observeEvent(matchKey.substringBeforeLast("_")),
    ) { match, event ->
        val breakdown = match?.scoreBreakdown?.let { parseBreakdown(it) }
        val videos = match?.videos?.let { parseVideos(it) } ?: emptyList()
        val timeToDisplay = match?.actualTime ?: match?.predictedTime ?: match?.time
        val isEstimate = match?.actualTime == null && match?.predictedTime != null &&
            match.time != null && kotlin.math.abs(match.predictedTime - match.time) > 60
        val formattedTime = formatMatchTime(timeToDisplay)?.let {
            if (isEstimate) "$it (est.)" else it
        }
        MatchDetailUiState(
            match = match,
            scoreBreakdown = breakdown,
            eventName = event?.name,
            eventKey = event?.key,
            playoffType = event?.playoffType ?: PlayoffType.OTHER,
            formattedTime = formattedTime,
            videos = videos,
            year = year,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MatchDetailUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                matchRepository.refreshMatch(matchKey)
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun formatMatchTime(epochSeconds: Long?): String? {
        if (epochSeconds == null) return null
        val instant = Instant.ofEpochSecond(epochSeconds)
        return timeFormatter.format(instant.atZone(ZoneId.systemDefault()))
    }

    private fun parseVideos(jsonString: String): List<MatchVideo> {
        return try {
            val array = json.decodeFromString<JsonArray>(jsonString)
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

    private fun parseBreakdown(jsonString: String): Map<String, Map<String, String>>? {
        return try {
            val obj = json.decodeFromString<JsonObject>(jsonString)
            val result = mutableMapOf<String, Map<String, String>>()
            for (alliance in listOf("red", "blue")) {
                val allianceObj = obj[alliance] as? JsonObject ?: continue
                result[alliance] = allianceObj.entries
                    .filter { (_, v) -> v is JsonPrimitive }
                    .associate { (k, v) -> k to v.jsonPrimitive.content }
            }
            result.ifEmpty { null }
        } catch (_: Exception) {
            null
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(navKey: Screen.MatchDetail): MatchDetailViewModel
    }
}
