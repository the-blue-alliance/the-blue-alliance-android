package com.thebluealliance.android.ui.districts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
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

@HiltViewModel(assistedFactory = DistrictDetailViewModel.Factory::class)
class DistrictDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: Screen.DistrictDetail,
    private val districtRepository: DistrictRepository,
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val districtKey: String = navKey.districtKey

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<DistrictDetailUiState> = combine(
        districtRepository.observeDistrict(districtKey),
        eventRepository.observeDistrictEvents(districtKey),
        districtRepository.observeDistrictRankings(districtKey),
    ) { district, events, rankings ->
        DistrictDetailUiState(
            district = district,
            events = events,
            rankings = rankings,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DistrictDetailUiState())

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

    @AssistedFactory
    interface Factory {
        fun create(navKey: Screen.DistrictDetail): DistrictDetailViewModel
    }
}
