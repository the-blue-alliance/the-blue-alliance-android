package com.thebluealliance.android.ui.teamevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.CmpAdvancement
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventAdvancementPoints
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.withPlayoffAlliances
import com.thebluealliance.android.navigation.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class TeamEventExtras(
    val awards: List<Award>,
    val oprs: EventOPRs?,
    val alliances: List<Alliance>,
    val media: List<Media>,
    val pitLocation: String?,
    val districtPointsByTeam: Map<String, EventAdvancementPoints>,
    val regionalPointsByTeam: Map<String, EventAdvancementPoints>,
    val regionalCmpAdvancementByTeam: Map<String, CmpAdvancement>,
)

@HiltViewModel(assistedFactory = TeamEventDetailViewModel.Factory::class)
class TeamEventDetailViewModel
    @AssistedInject
    constructor(
        @Assisted val navKey: Screen.TeamEventDetail,
        private val teamRepository: TeamRepository,
        private val eventRepository: EventRepository,
        private val matchRepository: MatchRepository,
    ) : ViewModel() {
        val teamKey: String = navKey.teamKey
        val eventKey: String = navKey.eventKey
        private val year: Int = eventKey.substring(0, 4).toIntOrNull() ?: 0

        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

        private val regionalCmpAdvancementByTeam =
            MutableStateFlow<Map<String, CmpAdvancement>>(emptyMap())

        val uiState: StateFlow<TeamEventDetailUiState> =
            combine(
                teamRepository.observeTeam(teamKey),
                eventRepository.observeEvent(eventKey),
                eventRepository.observeEventRankings(eventKey).map { rankings ->
                    rankings.firstOrNull { it.teamKey == teamKey }
                },
                matchRepository.observeEventMatches(eventKey).map { matches ->
                    matches.filter { m -> teamKey in m.redTeamKeys || teamKey in m.blueTeamKeys }
                },
                combine(
                    combine(
                        eventRepository.observeEventAwards(eventKey).map { awards ->
                            awards.filter { it.teamKey == teamKey }
                        },
                        eventRepository.observeEventOPRs(eventKey),
                        eventRepository.observeEventAlliances(eventKey),
                        teamRepository.observeTeamMedia(teamKey, year),
                        teamRepository.observeTeamEventPitLocation(teamKey, eventKey),
                    ) { awards, oprs, alliances, media, pitLocation ->
                        TeamEventExtras(
                            awards = awards,
                            oprs = oprs,
                            alliances = alliances,
                            media = media,
                            pitLocation = pitLocation,
                            districtPointsByTeam = emptyMap(),
                            regionalPointsByTeam = emptyMap(),
                            regionalCmpAdvancementByTeam = emptyMap(),
                        )
                    },
                    combine(
                        eventRepository.observeEventDistrictPoints(eventKey).map {
                            it.associateBy { points ->
                                points.teamKey
                            }
                        },
                        eventRepository.observeEventRegionalPoints(eventKey).map {
                            it.associateBy { points ->
                                points.teamKey
                            }
                        },
                        regionalCmpAdvancementByTeam,
                    ) { districtPoints, regionalPoints, cmpMap ->
                        Triple(districtPoints, regionalPoints, cmpMap)
                    },
                ) { baseExtras, pointData ->
                    baseExtras.copy(
                        districtPointsByTeam = pointData.first,
                        regionalPointsByTeam = pointData.second,
                        regionalCmpAdvancementByTeam = pointData.third,
                    )
                },
            ) { team, event, ranking, matches, extras ->
                val isDistrictEvent = event?.district != null
                val advancementPoints =
                    if (isDistrictEvent) {
                        extras.districtPointsByTeam[teamKey]
                    } else {
                        extras.regionalPointsByTeam[teamKey]
                    }
                TeamEventDetailUiState(
                    team = team,
                    event = event,
                    ranking = ranking,
                    matches = matches.map { it.withPlayoffAlliances(extras.alliances) },
                    awards = extras.awards,
                    oprs = extras.oprs,
                    alliances = extras.alliances,
                    media = extras.media,
                    pitLocation = extras.pitLocation,
                    advancementPoints = advancementPoints,
                    cmpAdvancement =
                        event?.let {
                            filterRegionalCmpQualificationForEvent(
                                event = it,
                                advancement = extras.regionalCmpAdvancementByTeam[teamKey],
                            )
                        },
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                TeamEventDetailUiState(),
            )

        init {
            refreshAll()
        }

        fun refreshAll() {
            viewModelScope.launch {
                _isRefreshing.value = true
                try {
                    coroutineScope {
                        launch {
                            try {
                                teamRepository.refreshTeam(teamKey)
                            } catch (_: Exception) {
                            }
                        }
                        launch {
                            try {
                                eventRepository.refreshEvent(eventKey)
                            } catch (
                                _: Exception,
                            ) {
                            }
                        }
                        launch {
                            try {
                                matchRepository.refreshEventMatches(eventKey)
                            } catch (
                                _: Exception,
                            ) {
                            }
                        }
                        launch {
                            try {
                                eventRepository.refreshEventRankings(eventKey)
                            } catch (
                                _: Exception,
                            ) {
                            }
                        }
                        launch {
                            try {
                                eventRepository.refreshEventAwards(eventKey)
                            } catch (
                                _: Exception,
                            ) {
                            }
                        }
                        launch {
                            try {
                                eventRepository.refreshEventOPRs(eventKey)
                            } catch (
                                _: Exception,
                            ) {
                            }
                        }
                        launch {
                            try {
                                eventRepository.refreshEventAlliances(eventKey)
                            } catch (
                                _: Exception,
                            ) {
                            }
                        }
                        launch {
                            try {
                                eventRepository.refreshEventDistrictPoints(eventKey)
                            } catch (
                                _: Exception,
                            ) {
                            }
                        }
                        launch {
                            try {
                                eventRepository.refreshEventRegionalPoints(eventKey)
                            } catch (
                                _: Exception,
                            ) {
                            }
                        }
                        launch {
                            try {
                                regionalCmpAdvancementByTeam.value =
                                    eventRepository.fetchRegionalCmpAdvancementByTeam(year)
                            } catch (_: Exception) {
                            }
                        }
                        launch {
                            try {
                                teamRepository.refreshTeamMedia(teamKey, year)
                            } catch (
                                _: Exception,
                            ) {
                            }
                        }
                        launch {
                            try {
                                teamRepository.refreshTeamEventPitLocation(teamKey, eventKey)
                            } catch (
                                _: Exception,
                            ) {
                            }
                        }
                    }
                } finally {
                    _isRefreshing.value = false
                }
            }
        }

        fun refreshTab(tab: Int) {
            viewModelScope.launch {
                _isRefreshing.value = true
                try {
                    coroutineScope {
                        when (tab) {
                            0 -> { // Summary — needs most data for the overview
                                launch {
                                    try {
                                        teamRepository.refreshTeam(teamKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                                launch {
                                    try {
                                        eventRepository.refreshEvent(eventKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                                launch {
                                    try {
                                        matchRepository.refreshEventMatches(eventKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                                launch {
                                    try {
                                        eventRepository.refreshEventRankings(eventKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                                launch {
                                    try {
                                        eventRepository.refreshEventAlliances(eventKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                                launch {
                                    try {
                                        eventRepository.refreshEventAwards(eventKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                                launch {
                                    try {
                                        eventRepository.refreshEventDistrictPoints(eventKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                                launch {
                                    try {
                                        eventRepository.refreshEventRegionalPoints(eventKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                                launch {
                                    try {
                                        regionalCmpAdvancementByTeam.value =
                                            eventRepository.fetchRegionalCmpAdvancementByTeam(year)
                                    } catch (_: Exception) {
                                    }
                                }
                                launch {
                                    try {
                                        teamRepository.refreshTeamEventPitLocation(
                                            teamKey,
                                            eventKey,
                                        )
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                            }
                            1 -> { // Matches
                                launch {
                                    try {
                                        matchRepository.refreshEventMatches(eventKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                            }
                            2 -> { // Media
                                launch {
                                    try {
                                        teamRepository.refreshTeamMedia(teamKey, year)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                            }
                            3 -> { // Stats
                                launch {
                                    try {
                                        eventRepository.refreshEventOPRs(eventKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                            }
                            4 -> { // Awards
                                launch {
                                    try {
                                        eventRepository.refreshEventAwards(eventKey)
                                    } catch (
                                        _: Exception,
                                    ) {
                                    }
                                }
                            }
                        }
                    }
                } finally {
                    _isRefreshing.value = false
                }
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.TeamEventDetail): TeamEventDetailViewModel
        }
    }

private fun filterRegionalCmpQualificationForEvent(
    event: Event,
    advancement: CmpAdvancement?,
): CmpAdvancement? {
    if (event.district != null || advancement == null) return null
    return when (advancement) {
        is CmpAdvancement.EventQualified -> advancement.takeIf { it.eventKey == event.key }
        is CmpAdvancement.PoolQualified -> {
            val eventWeek = event.week
            advancement.takeIf { eventWeek != null && it.week == (eventWeek + 1) }
        }
        is CmpAdvancement.Qualified -> null
    }
}
