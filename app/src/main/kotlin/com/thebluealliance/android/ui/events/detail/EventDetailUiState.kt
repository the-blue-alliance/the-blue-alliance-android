package com.thebluealliance.android.ui.events.detail

import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.CmpAdvancement
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventAdvancementPoints
import com.thebluealliance.android.domain.model.EventCOPRs
import com.thebluealliance.android.domain.model.EventInsights
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.RankingSortOrder
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.RefreshOutcome

data class EventDetailUiState(
    val event: Event? = null,
    val teams: List<Team>? = null,
    val matches: List<Match>? = null,
    val rankings: List<Ranking>? = null,
    val rankingSortOrders: List<RankingSortOrder>? = null,
    val rankingExtraStatsInfo: List<RankingSortOrder>? = null,
    val alliances: List<Alliance>? = null,
    val awards: List<Award>? = null,
    val advancementPoints: List<EventAdvancementPoints>? = null,
    val oprs: EventOPRs? = null,
    val coprs: EventCOPRs? = null,
    val insights: EventInsights? = null,
    val regionalCmpAdvancementByTeam: Map<String, CmpAdvancement> = emptyMap(),
    val districtDisplayName: String? = null,
    val pitLocations: Map<String, String> = emptyMap(),
    val favoriteTeamKeysAtEvent: List<String> = emptyList(),
    val loadOutcomes: Map<EventDetailTab, RefreshOutcome> = emptyMap(),
    /** TBA's last answer for the event itself was a 404. */
    val eventMissing: Boolean = false,
) {
    /** Whether [tab] has anything to show, cached or fetched. */
    fun hasData(tab: EventDetailTab): Boolean =
        when (tab) {
            EventDetailTab.INFO -> event != null
            EventDetailTab.TEAMS -> !teams.isNullOrEmpty()
            EventDetailTab.RANKINGS -> !rankings.isNullOrEmpty()
            EventDetailTab.MATCHES -> !matches.isNullOrEmpty()
            EventDetailTab.ALLIANCES -> !alliances.isNullOrEmpty()
            EventDetailTab.INSIGHTS ->
                !oprs?.oprs.isNullOrEmpty() ||
                    !coprs?.coprs.isNullOrEmpty() ||
                    insights?.qual != null ||
                    insights?.playoff != null
            EventDetailTab.ADVANCEMENT_POINTS -> !advancementPoints.isNullOrEmpty()
            EventDetailTab.AWARDS -> !awards.isNullOrEmpty()
        }

    /** TBA has no event with this key, and none is cached. */
    val notFound: Boolean get() = eventMissing && event == null

    /** The tab's last fetch failed and there is nothing cached to show instead. */
    fun loadFailed(tab: EventDetailTab): Boolean =
        loadOutcomes[tab] == RefreshOutcome.FAILED && !hasData(tab)

    /** The Teams tab badge: hidden until there are teams, or a fetch has said there are none. */
    val teamCount: Int?
        get() =
            teams
                ?.takeIf {
                    it.isNotEmpty() ||
                        loadOutcomes[EventDetailTab.TEAMS] == RefreshOutcome.SUCCEEDED
                }?.size
}
