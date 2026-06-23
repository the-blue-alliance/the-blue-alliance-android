package com.thebluealliance.android.ui.districts

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.events.buildEventSections
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = DistrictDetailViewModel.Factory::class)
class DistrictDetailViewModel
    @AssistedInject
    constructor(
        @Assisted val navKey: Screen.DistrictDetail,
        private val districtRepository: DistrictRepository,
        private val eventRepository: EventRepository,
    ) : RefreshableViewModel() {
        private val initialDistrictKey: String = navKey.districtKey
        private val districtAbbreviation: String = initialDistrictKey.drop(4)
        private val initialYear: Int = initialDistrictKey.take(4).toIntOrNull() ?: 0

        private val _selectedYear = MutableStateFlow(initialYear)
        val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

        private val _availableYears = MutableStateFlow<List<Int>>(emptyList())
        val availableYears: StateFlow<List<Int>> = _availableYears.asStateFlow()

        val uiState: StateFlow<DistrictDetailUiState> =
            combine(
                _selectedYear.flatMapLatest { year ->
                    districtRepository.observeDistrict("$year$districtAbbreviation")
                },
                _selectedYear.flatMapLatest { year ->
                    eventRepository.observeDistrictEvents("$year$districtAbbreviation")
                },
                _selectedYear.flatMapLatest { year ->
                    districtRepository.observeDistrictRankings("$year$districtAbbreviation")
                },
            ) { district, events, rankings ->
                DistrictDetailUiState(
                    district = district,
                    eventSections = if (events.isEmpty()) null else buildEventSections(events),
                    rankings = rankings,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DistrictDetailUiState())

        init {
            fetchAvailableYears()
            refreshAll()
        }

        private fun fetchAvailableYears() {
            viewModelScope.launch {
                try {
                    val years = districtRepository.getDistrictHistory(districtAbbreviation)
                    _availableYears.value = years
                } catch (_: Exception) {
                }
            }
        }

        fun selectYear(year: Int) {
            if (_selectedYear.value == year) return
            _selectedYear.value = year
            refreshAll()
        }

        fun refreshAll() {
            val key = "${_selectedYear.value}$districtAbbreviation"
            refreshing(
                { eventRepository.refreshDistrictEvents(key) },
                { districtRepository.refreshDistrictRankings(key) },
            )
        }

        fun refreshTab(tab: DistrictDetailTab) {
            val key = "${_selectedYear.value}$districtAbbreviation"
            when (tab) {
                DistrictDetailTab.EVENTS ->
                    refreshing({ eventRepository.refreshDistrictEvents(key) })
                DistrictDetailTab.RANKINGS ->
                    refreshing({ districtRepository.refreshDistrictRankings(key) })
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.DistrictDetail): DistrictDetailViewModel
        }
    }
