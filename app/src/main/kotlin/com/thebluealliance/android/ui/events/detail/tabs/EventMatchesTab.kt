package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.ui.components.LocalMatchDetailPaneHost
import com.thebluealliance.android.ui.components.MatchList
import com.thebluealliance.android.ui.components.MatchListDetailPanes

@Composable
fun EventMatchesTab(
    matches: List<Match>?,
    playoffType: PlayoffType,
    onNavigateToMatch: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    val host = LocalMatchDetailPaneHost.current
    val list: @Composable () -> Unit = {
        MatchList(
            matches = matches,
            playoffType = playoffType,
            onNavigateToMatch = onNavigateToMatch,
            innerPadding = innerPadding,
            selectedMatchKey = host?.selectedMatchKey,
        )
    }
    // Loading and "No matches" keep the full width; there is nothing to select.
    if (host == null || matches.isNullOrEmpty()) list() else MatchListDetailPanes(host, list)
}
