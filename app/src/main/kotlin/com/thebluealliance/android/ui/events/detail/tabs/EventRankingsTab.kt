package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox

@Composable
fun EventRankingsTab(rankings: List<Ranking>?, onTeamClick: (String) -> Unit = {}) {
    if (rankings == null) {
        LoadingBox()
        return
    }
    if (rankings.isEmpty()) {
        EmptyBox("No rankings")
        return
    }
    val sorted = rankings.sortedBy { it.rank }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(sorted, key = { "${it.eventKey}_${it.teamKey}" }) { ranking ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTeamClick(ranking.teamKey) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "#${ranking.rank}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = ranking.teamKey.removePrefix("frc"),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(0.25f),
                )
                Text(
                    text = "${ranking.wins}-${ranking.losses}-${ranking.ties}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.25f),
                )
                Text(
                    text = "${ranking.matchesPlayed} played",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.35f),
                )
            }
        }
    }
}

