package com.thebluealliance.android.ui.teamevent

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
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
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.UserMessageBus
import com.thebluealliance.android.ui.teamevent.TeamEventDetailTab.AWARDS
import com.thebluealliance.android.ui.teamevent.TeamEventDetailTab.MATCHES
import com.thebluealliance.android.ui.teamevent.TeamEventDetailTab.MEDIA
import com.thebluealliance.android.ui.teamevent.TeamEventDetailTab.STATS
import com.thebluealliance.android.ui.teamevent.TeamEventDetailTab.SUMMARY
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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
        private val regionalAdvancementRepository: RegionalAdvancementRepository,
        private val userMessages: UserMessageBus,
    ) : RefreshableViewModel() {
        val teamKey: String = navKey.teamKey
        val eventKey: String = navKey.eventKey
        private val year: Int = eventKey.take(4).toIntOrNull() ?: 0

        private val regionalCmpAdvancementByTeam =
            MutableStateFlow<Map<String, CmpAdvancement>>(emptyMap())

        private val loadOutcomes =
            MutableStateFlow<Map<TeamEventDetailTab, RefreshOutcome>>(emptyMap())

        private val state =
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
            }

        val uiState: StateFlow<TeamEventDetailUiState> =
            state
                .combine(loadOutcomes) { base, outcomes -> base.copy(loadOutcomes = outcomes) }
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    TeamEventDetailUiState(),
                )

        init {
            refreshAll()
        }

        fun refreshAll() {
            refreshTracked(
                ::refreshAll,
                SUMMARY to { teamRepository.refreshTeam(teamKey) },
                SUMMARY to {
                    eventRepository.refreshEvent(eventKey)
                    refreshRegionalCmpAdvancement()
                },
                MATCHES to { matchRepository.refreshEventMatches(eventKey) },
                SUMMARY to { eventRepository.refreshEventRankings(eventKey) },
                AWARDS to { eventRepository.refreshEventAwards(eventKey) },
                STATS to { eventRepository.refreshEventOPRs(eventKey) },
                SUMMARY to { eventRepository.refreshEventAlliances(eventKey) },
                STATS to { eventRepository.refreshEventDistrictPoints(eventKey) },
                STATS to { eventRepository.refreshEventRegionalPoints(eventKey) },
                MEDIA to { teamRepository.refreshTeamMedia(teamKey, year) },
                null to { teamRepository.refreshTeamEventPitLocation(teamKey, eventKey) },
            )
        }

        fun refreshTab(tab: TeamEventDetailTab) {
            val retry = { refreshTab(tab) }
            when (tab) {
                // The summary overview needs most of the event data
                SUMMARY ->
                    refreshTracked(
                        retry,
                        SUMMARY to { teamRepository.refreshTeam(teamKey) },
                        SUMMARY to {
                            eventRepository.refreshEvent(eventKey)
                            refreshRegionalCmpAdvancement(forceRefresh = true)
                        },
                        null to { matchRepository.refreshEventMatches(eventKey) },
                        SUMMARY to { eventRepository.refreshEventRankings(eventKey) },
                        SUMMARY to { eventRepository.refreshEventAlliances(eventKey) },
                        null to { eventRepository.refreshEventAwards(eventKey) },
                        null to { eventRepository.refreshEventDistrictPoints(eventKey) },
                        null to { eventRepository.refreshEventRegionalPoints(eventKey) },
                        null to { teamRepository.refreshTeamEventPitLocation(teamKey, eventKey) },
                    )
                MATCHES ->
                    refreshTracked(
                        retry,
                        MATCHES to { matchRepository.refreshEventMatches(eventKey) },
                    )
                MEDIA ->
                    refreshTracked(
                        retry,
                        MEDIA to { teamRepository.refreshTeamMedia(teamKey, year) },
                    )
                STATS ->
                    refreshTracked(
                        retry,
                        STATS to { eventRepository.refreshEventOPRs(eventKey) },
                    )
                AWARDS ->
                    refreshTracked(
                        retry,
                        AWARDS to { eventRepository.refreshEventAwards(eventKey) },
                    )
            }
        }

        private fun refreshTracked(
            retry: () -> Unit,
            vararg tasks: Pair<TeamEventDetailTab?, suspend () -> Unit>,
        ) {
            refreshTabs(loadOutcomes, { failed -> reportFailedRefresh(failed, retry) }, *tasks)
        }

        // Reads the cache itself: an offline refresh can fail before the screen has collected it.
        // With nothing cached, the failed tab's error screen says it instead.
        private suspend fun reportFailedRefresh(
            failed: Set<TeamEventDetailTab>,
            retry: () -> Unit,
        ) {
            if (failed.isEmpty()) return
            val cached = state.first()
            if (failed.any { cached.hasData(it) }) {
                userMessages.showRetry("Couldn't refresh team at event", retry)
            }
        }

        // Regional advancement is a whole-season payload that only applies to non-district
        // events, so skip it entirely when the event belongs to a district.
        private suspend fun refreshRegionalCmpAdvancement(forceRefresh: Boolean = false) {
            val event = eventRepository.observeEvent(eventKey).first() ?: return
            if (event.district != null) return
            regionalCmpAdvancementByTeam.value =
                regionalAdvancementRepository.getCmpAdvancementByTeam(year, forceRefresh)
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
