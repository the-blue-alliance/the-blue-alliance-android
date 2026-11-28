package com.thebluealliance.android.ui.teamevent

import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.CmpAdvancement
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventAdvancementPoints
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.RefreshOutcome

data class TeamEventDetailUiState(
    val team: Team? = null,
    val event: Event? = null,
    val ranking: Ranking? = null,
    val matches: List<Match>? = null,
    val awards: List<Award>? = null,
    val oprs: EventOPRs? = null,
    val alliances: List<Alliance>? = null,
    val media: List<Media>? = null,
    val pitLocation: String? = null,
    val advancementPoints: EventAdvancementPoints? = null,
    val cmpAdvancement: CmpAdvancement? = null,
    val loadOutcomes: Map<TeamEventDetailTab, RefreshOutcome> = emptyMap(),
) {
    /** Whether [tab] has anything to show, cached or fetched. */
    fun hasData(tab: TeamEventDetailTab): Boolean =
        when (tab) {
            TeamEventDetailTab.SUMMARY -> team != null || event != null
            TeamEventDetailTab.MATCHES -> !matches.isNullOrEmpty()
            TeamEventDetailTab.MEDIA -> !media.isNullOrEmpty()
            TeamEventDetailTab.STATS -> oprs != null || advancementPoints != null
            TeamEventDetailTab.AWARDS -> !awards.isNullOrEmpty()
        }

    /** Whether [tab]'s fetch failed with nothing cached to show instead. */
    fun loadFailed(tab: TeamEventDetailTab): Boolean =
        loadOutcomes[tab] == RefreshOutcome.FAILED && !hasData(tab)
}
