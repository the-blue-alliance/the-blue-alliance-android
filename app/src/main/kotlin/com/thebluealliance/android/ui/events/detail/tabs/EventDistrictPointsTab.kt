package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventDistrictPoints
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox

@Composable
fun EventDistrictPointsTab(
    districtPoints: List<EventDistrictPoints>?,
    event: Event?,
    teams: List<Team>?,
    onTeamClick: (String) -> Unit = {},
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (districtPoints == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }
    if (districtPoints.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = if (event?.district != null) "No district points" else "No regional points"
        )
        return
    }
    val teamsByKey = remember(teams) { teams?.associateBy { it.key } ?: emptyMap() }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        items(districtPoints, key = { it.teamKey }) { points ->
            val rank = districtPoints.indexOf(points) + 1
            val teamName = teamsByKey[points.teamKey]?.nickname
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTeamClick(points.teamKey) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(48.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = points.teamKey.removePrefix("frc"),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    if (teamName != null) {
                        Text(
                            text = teamName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                }
                Text(
                    text = "${points.total} pts",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            HorizontalDivider()
        }
    }
}

