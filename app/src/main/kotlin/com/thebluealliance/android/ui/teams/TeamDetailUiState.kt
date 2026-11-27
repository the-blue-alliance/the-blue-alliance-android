package com.thebluealliance.android.ui.teams

import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.Team

data class TeamDetailUiState(
    val team: Team? = null,
    val events: List<Event>? = null,
    val media: List<Media>? = null,
    /** TBA's last answer for the team itself was a 404. */
    val teamMissing: Boolean = false,
) {
    /** TBA has no team with this key, and none is cached. */
    val notFound: Boolean get() = teamMissing && team == null
}
