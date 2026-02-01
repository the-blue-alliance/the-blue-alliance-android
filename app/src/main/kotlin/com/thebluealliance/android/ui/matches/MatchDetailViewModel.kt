package com.thebluealliance.android.ui.matches

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val matchRepository: MatchRepository,
) : ViewModel() {

    private val matchKey: String = savedStateHandle.toRoute<Screen.MatchDetail>().matchKey

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    val uiState: StateFlow<MatchDetailUiState> = matchRepository.observeMatch(matchKey)
        .map { match ->
            val breakdown = match?.scoreBreakdown?.let { parseBreakdown(it) }
            MatchDetailUiState(match = match, scoreBreakdown = breakdown)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MatchDetailUiState())

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

    private fun parseBreakdown(jsonString: String): Map<String, Map<String, String>>? {
        return try {
            val obj = json.decodeFromString<JsonObject>(jsonString)
            val result = mutableMapOf<String, Map<String, String>>()
            for (alliance in listOf("red", "blue")) {
                val allianceObj = obj[alliance] as? JsonObject ?: continue
                result[alliance] = allianceObj.mapValues { (_, v) ->
                    v.jsonPrimitive.content
                }
            }
            result.ifEmpty { null }
        } catch (_: Exception) {
            null
        }
    }
}
