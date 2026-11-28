package com.thebluealliance.android.ui.teams

import com.thebluealliance.android.R
import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.QuantityStrings
import com.thebluealliance.android.ui.common.RefreshOutcome

data class TeamDetailUiState(
    val team: Team? = null,
    val events: List<Event>? = null,
    val media: List<Media>? = null,
    val districtStanding: TeamDistrictStanding? = null,
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

/** A team's place in its district's rankings for the selected year, for the Info tab. */
data class TeamDistrictStanding(
    val districtKey: String,
    val districtName: String,
    val rank: Int,
    val points: Int,
)

/** One district's cached rankings, with the district itself if it's loaded. */
data class DistrictRankingsSnapshot(
    val districtKey: String,
    val district: District?,
    val rankings: List<DistrictRanking>,
)

/** The districts whose rankings could hold [year]'s team: those of the team's events that year. */
fun teamDistrictKeys(
    events: List<Event>,
    year: Int,
): List<String> =
    events
        .filter { it.year == year }
        .mapNotNull { it.district }
        .distinct()
        .sorted()

/**
 * The team's standing in the first district whose rankings list it. A district's rankings only
 * hold its own teams, so an event played in another district doesn't match. Null while rankings
 * load, when they fail, before any of the district's events have scored, and when the team
 * isn't ranked in any district.
 */
fun teamDistrictStanding(
    teamKey: String,
    snapshots: List<DistrictRankingsSnapshot>,
): TeamDistrictStanding? =
    snapshots.firstNotNullOfOrNull { snapshot ->
        // Before any event, the rankings hold only rookie bonuses and the rest in team order.
        val scored = snapshot.rankings.any { it.pointTotal > it.rookieBonus }
        if (!scored) return@firstNotNullOfOrNull null
        snapshot.rankings.firstOrNull { it.teamKey == teamKey }?.let { ranking ->
            TeamDistrictStanding(
                districtKey = snapshot.districtKey,
                districtName =
                    snapshot.district?.displayName?.takeIf { it.isNotBlank() }
                        ?: snapshot.districtKey.replace(Regex("^\\d{4}"), "").uppercase(),
                rank = ranking.rank,
                points = ranking.pointTotal.toInt(),
            )
        }
    }

/** What follows the linked district name: "Rank 12 · 187 pts". */
fun TeamDistrictStanding.rankAndPoints(strings: QuantityStrings): String =
    "Rank $rank \u00b7 ${strings.get(R.plurals.points_short, points)}"
