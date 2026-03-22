package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.domain.model.calculatePlayoffAlliances
import com.thebluealliance.android.ui.components.MatchList

@Composable
fun EventMatchesTab(
    matches: List<Match>?,
    alliances: List<Alliance>?,
    playoffType: PlayoffType,
    onNavigateToMatch: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    matches?.forEach { it.calculatePlayoffAlliances(alliances) }

    MatchList(
        matches = matches,
        playoffType = playoffType,
        onNavigateToMatch = onNavigateToMatch,
        innerPadding = innerPadding,
    )
}
