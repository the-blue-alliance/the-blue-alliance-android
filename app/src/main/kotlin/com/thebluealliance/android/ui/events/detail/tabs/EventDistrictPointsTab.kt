package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.foundation.layout.Column
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
) {
    if (districtPoints == null) {
        LoadingBox()
        return
    }
    if (districtPoints.isEmpty()) {
        EmptyBox("No district points")
        return
    }
    val teamsByKey = remember(teams) { teams?.associateBy { it.key } ?: emptyMap() }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (event?.district == null) {
            item(key = "district_warning") {
                Text(
                    text = "This event is not part of a district \u2014 these points are purely hypothetical.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
        items(districtPoints, key = { it.teamKey }) { points ->
            val rank = districtPoints.indexOf(points) + 1
            val teamName = teamsByKey[points.teamKey]?.nickname
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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

