package com.thebluealliance.android.ui.districts

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.UserMessageBus
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = DistrictDetailViewModel.Factory::class)
class DistrictDetailViewModel
    @AssistedInject
    constructor(
        @Assisted val navKey: Screen.DistrictDetail,
        private val districtRepository: DistrictRepository,
        private val eventRepository: EventRepository,
        private val userMessages: UserMessageBus,
    ) : RefreshableViewModel() {
        private val initialDistrictKey: String = navKey.districtKey
        private val districtAbbreviation: String = initialDistrictKey.drop(4)
        private val initialYear: Int = initialDistrictKey.take(4).toIntOrNull() ?: 0

        private val _selectedYear = MutableStateFlow(initialYear)
        val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

        private val _availableYears = MutableStateFlow<List<Int>>(emptyList())
        val availableYears: StateFlow<List<Int>> = _availableYears.asStateFlow()

        private val loadOutcomes =
            MutableStateFlow<Map<DistrictDetailTab, RefreshOutcome>>(emptyMap())

        // Bumped on a year change, so the last season's late fetches don't decide this one's tabs.
        private val seasonGeneration = AtomicInteger(0)

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
                loadOutcomes,
            ) { district, events, rankings, outcomes ->
                fun loaded(tab: DistrictDetailTab) = outcomes[tab] == RefreshOutcome.SUCCEEDED
                DistrictDetailUiState(
                    district = district,
                    eventSections =
                        when {
                            events.isNotEmpty() -> buildEventSections(events)
                            loaded(DistrictDetailTab.EVENTS) -> emptyList()
                            else -> null
                        },
                    rankings =
                        rankings.takeIf {
                            it.isNotEmpty() ||
                                loaded(DistrictDetailTab.RANKINGS)
                        },
                    loadOutcomes = outcomes,
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
            seasonGeneration.incrementAndGet()
            loadOutcomes.value = emptyMap()
            _selectedYear.value = year
            refreshAll()
        }

        fun refreshAll() {
            refreshDistrictTabs(*DistrictDetailTab.entries.toTypedArray())
        }

        fun refreshTab(tab: DistrictDetailTab) {
            refreshDistrictTabs(tab)
        }

        private fun refreshDistrictTabs(vararg tabs: DistrictDetailTab) {
            val key = "${_selectedYear.value}$districtAbbreviation"
            val generation = seasonGeneration.get()
            val tasks =
                tabs.map { tab ->
                    val task: suspend () -> Unit =
                        when (tab) {
                            DistrictDetailTab.EVENTS -> {
                                { eventRepository.refreshDistrictEvents(key) }
                            }
                            DistrictDetailTab.RANKINGS -> {
                                { districtRepository.refreshDistrictRankings(key) }
                            }
                        }
                    tab to task
                }
            refreshTabs(
                loadOutcomes,
                { failed -> reportFailedRefresh(key, failed) },
                *tasks.toTypedArray(),
                isCurrent = { seasonGeneration.get() == generation },
            )
        }

        // With nothing cached, the failed tab's error screen says it instead.
        private suspend fun reportFailedRefresh(
            key: String,
            failed: Set<DistrictDetailTab>,
        ) {
            val cached =
                failed.any { tab ->
                    when (tab) {
                        DistrictDetailTab.EVENTS ->
                            eventRepository.observeDistrictEvents(key).first().isNotEmpty()
                        DistrictDetailTab.RANKINGS ->
                            districtRepository.observeDistrictRankings(key).first().isNotEmpty()
                    }
                }
            if (cached) userMessages.showRetry("Couldn't refresh district", ::refreshAll)
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.DistrictDetail): DistrictDetailViewModel
        }
    }
