package com.thebluealliance.android.ui.events

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.SeasonSelection
import com.thebluealliance.android.ui.common.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Year

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = EventsViewModel.Factory::class)
class EventsViewModel
    @AssistedInject
    constructor(
        @Assisted navKey: Screen.Events,
        private val eventRepository: EventRepository,
        private val districtRepository: DistrictRepository,
        private val seasons: SeasonRepository,
        private val myTBARepository: MyTBARepository,
        private val authRepository: AuthRepository,
    ) : RefreshableViewModel() {
        private val _maxYear = MutableStateFlow(Year.now().value)
        val maxYear: StateFlow<Int> = _maxYear.asStateFlow()

        // Without a year in the link, the list waits for TBA's current season, like every picker.
        private val selection = SeasonSelection(navKey.year ?: Year.now().value)
        val selectedYear: StateFlow<Int> = selection.year

        private val yearResolved = MutableStateFlow(navKey.year != null)

        private val hasLoaded = MutableStateFlow(false)

        val uiState: StateFlow<UiState<EventsData>> =
            combine(selection.year, yearResolved) { year, resolved -> year.takeIf { resolved } }
                .flatMapLatest { year ->
                    if (year ==
                        null
                    ) {
                        return@flatMapLatest flowOf<UiState<EventsData>>(UiState.Loading)
                    }
                    hasLoaded.value = false
                    combine(
                        eventRepository.observeEventsForYear(year),
                        districtRepository.observeDistrictsForYear(year),
                        hasLoaded,
                        myTBARepository.observeFavorites(),
                    ) { events, districts, hasLoaded, favorites ->
                        val state: UiState<EventsData> =
                            when {
                                events.isEmpty() && !hasLoaded -> UiState.Loading
                                events.isEmpty() -> UiState.Empty
                                else -> {
                                    val districtNames =
                                        buildMap {
                                            districts.forEach {
                                                put(it.key.lowercase(), it.displayName)
                                                put(it.abbreviation.lowercase(), it.displayName)
                                            }
                                        }
                                    val sections =
                                        buildEventSections(events, districtNames = districtNames)
                                    val favoriteEventKeys =
                                        favorites
                                            .filter { it.modelType == ModelType.EVENT }
                                            .map { it.modelKey }
                                            .toSet()
                                    UiState.Success(
                                        EventsData(sections, favoriteEventKeys, districtNames),
                                    )
                                }
                            }
                        state
                    }
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading,
                )

        init {
            fetchMaxYear()
            if (navKey.year == null) openOnDefaultSeason() else refreshEvents()
            refreshFavorites()
        }

        private fun openOnDefaultSeason() {
            viewModelScope.launch {
                val year = seasons.defaultSeason()
                if (!selection.hasUserPick) {
                    selection.offerDefault(year)
                    _maxYear.value = maxOf(_maxYear.value, year)
                    yearResolved.value = true
                    refreshEvents()
                }
            }
        }

        private fun refreshFavorites() {
            viewModelScope.launch {
                if (!authRepository.isSignedIn()) return@launch
                try {
                    myTBARepository.refreshFavorites()
                } catch (_: Exception) {
                }
            }
        }

        private fun fetchMaxYear() {
            viewModelScope.launch {
                // Falls back to the calendar year (already set) without a status.
                seasons.maxSeason()?.let { _maxYear.value = maxOf(it, selection.year.value) }
            }
        }

        fun selectYear(year: Int) {
            selection.pick(year)
            yearResolved.value = true
            refreshEvents()
        }

        fun refreshEvents() {
            refreshing({
                try {
                    eventRepository.refreshEventsForYear(selection.year.value)
                    districtRepository.refreshDistrictsForYear(selection.year.value)
                } finally {
                    hasLoaded.value = true
                }
            })
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.Events): EventsViewModel
        }
    }
