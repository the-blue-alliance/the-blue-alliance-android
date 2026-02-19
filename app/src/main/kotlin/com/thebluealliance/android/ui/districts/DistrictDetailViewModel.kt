package com.thebluealliance.android.ui.districts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class DistrictDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val districtRepository: DistrictRepository,
    private val eventRepository: EventRepository,
    private val regionalAdvancementRepository: RegionalAdvancementRepository,
) : ViewModel() {

    private val districtKey: String = savedStateHandle.toRoute<Screen.DistrictDetail>().districtKey

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<DistrictDetailUiState> = districtRepository.observeDistrict(districtKey)
        .flatMapLatest { district ->
            combine(
                flowOf(district),
                eventRepository.observeDistrictEvents(districtKey),
                districtRepository.observeDistrictRankings(districtKey),
                if (district != null && district.year >= 2025) {
                    regionalAdvancementRepository.observeRegionalRankings(district.year)
                } else {
                    flowOf(null)
                },
            ) { d, events, rankings, regionalRankings ->
                DistrictDetailUiState(
                    district = d,
                    events = events,
                    rankings = rankings,
                    regionalRankings = regionalRankings,
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DistrictDetailUiState())

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val eventsJob = launch {
                    try { eventRepository.refreshDistrictEvents(districtKey) } catch (_: Exception) { }
                }
                val rankingsJob = launch {
                    try { districtRepository.refreshDistrictRankings(districtKey) } catch (_: Exception) { }
                }
                eventsJob.join()
                rankingsJob.join()
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
