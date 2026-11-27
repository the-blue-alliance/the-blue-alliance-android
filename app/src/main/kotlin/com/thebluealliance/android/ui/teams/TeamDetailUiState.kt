package com.thebluealliance.android.ui.teams

import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.RefreshOutcome

data class TeamDetailUiState(
    val team: Team? = null,
    val events: List<Event>? = null,
    val media: List<Media>? = null,
    /** TBA's last answer for the team itself was a 404. */
    val teamMissing: Boolean = false,
    val loadOutcomes: Map<TeamDetailTab, RefreshOutcome> = emptyMap(),
) {
    /** Whether [tab] has anything to show, cached or fetched. */
    fun hasData(tab: TeamDetailTab): Boolean =
        when (tab) {
            TeamDetailTab.INFO -> team != null
            TeamDetailTab.EVENTS -> !events.isNullOrEmpty()
            // The Media tab leaves out the avatar, which Info shows.
            TeamDetailTab.MEDIA -> media.orEmpty().any { it.type != "avatar" }
        }

    /** TBA has no team with this key, and none is cached. */
    val notFound: Boolean get() = teamMissing && team == null

    /** The tab's last fetch failed and there is nothing cached to show instead. */
    fun loadFailed(tab: TeamDetailTab): Boolean =
        loadOutcomes[tab] == RefreshOutcome.FAILED && !hasData(tab)
}
