package com.thebluealliance.android.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.domain.model.ModelType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val districtRepository: DistrictRepository,
    private val tbaApi: TbaApi,
    private val myTBARepository: MyTBARepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _maxYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val maxYear: StateFlow<Int> = _maxYear.asStateFlow()

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _hasLoaded = MutableStateFlow(false)

    val uiState: StateFlow<EventsUiState> = _selectedYear
        .flatMapLatest { year ->
            _hasLoaded.value = false
            combine(
                eventRepository.observeEventsForYear(year),
                districtRepository.observeDistrictsForYear(year),
                _hasLoaded,
                myTBARepository.observeFavorites(),
            ) { events, districts, hasLoaded, favorites ->
                if (events.isEmpty() && !hasLoaded) {
                    EventsUiState.Loading
                } else {
                    val districtNames = buildMap {
                        districts.forEach {
                            put(it.key.lowercase(), it.displayName)
                            put(it.abbreviation.lowercase(), it.displayName)
                        }
                    }
                    val sections = buildEventSections(events, districtNames = districtNames)
                    val favoriteEventKeys = favorites
                        .filter { it.modelType == ModelType.EVENT }
                        .map { it.modelKey }
                        .toSet()
                    EventsUiState.Success(sections, favoriteEventKeys, districtNames)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EventsUiState.Loading)

    init {
        fetchMaxYear()
        refreshEvents()
        refreshFavorites()
    }

    private fun refreshFavorites() {
        viewModelScope.launch {
            if (!authRepository.isSignedIn()) return@launch
            try { myTBARepository.refreshFavorites() } catch (_: Exception) {}
        }
    }

    private fun fetchMaxYear() {
        viewModelScope.launch {
            try {
                val status = tbaApi.getStatus()
                _maxYear.value = status.maxSeason
                // If current selection is below max, update to max
                if (_selectedYear.value < status.maxSeason) {
                    _selectedYear.value = status.maxSeason
                }
            } catch (_: Exception) {
                // Fall back to calendar year (already set)
            }
        }
    }

    fun selectYear(year: Int) {
        _selectedYear.value = year
        refreshEvents()
        refreshDistricts()
    }

    private fun refreshDistricts() {
        viewModelScope.launch {
            try {
                districtRepository.refreshDistrictsForYear(_selectedYear.value)
            } catch (_: Exception) {}
        }
    }

    fun refreshEvents() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                eventRepository.refreshEventsForYear(_selectedYear.value)
                districtRepository.refreshDistrictsForYear(_selectedYear.value)
            } catch (e: Exception) {
                // Data will come from Room cache if available
            } finally {
                _hasLoaded.value = true
                _isRefreshing.value = false
            }
        }
    }
}
