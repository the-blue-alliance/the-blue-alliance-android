package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventAdvancementPoints
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox

internal fun advancementBreakdownRows(
    points: EventAdvancementPoints,
    isDistrictEvent: Boolean,
): List<Pair<String, Int>> = buildList {
    add("Qualification" to points.qualPoints)
    add((if (isDistrictEvent) "Elimination" else "Playoff") to points.elimPoints)
    add("Alliance" to points.alliancePoints)
    add("Awards" to points.awardPoints)
    if (!isDistrictEvent) {
        add("Team Age" to points.rookieBonus)
    }
}

@Composable
fun EventAdvancementTab(
    advancementPoints: List<EventAdvancementPoints>?,
    event: Event?,
    teams: List<Team>?,
    onTeamClick: (String) -> Unit = {},
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (advancementPoints == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }
    if (advancementPoints.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = if (event?.district != null) "No district points" else "No regional points"
        )
        return
    }
    val isDistrictEvent = event?.district != null
    val teamsByKey = remember(teams) { teams?.associateBy { it.key } ?: emptyMap() }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        itemsIndexed(advancementPoints, key = { _, points -> points.teamKey }) { index, points ->
            val rank = index + 1
            val teamName = teamsByKey[points.teamKey]?.nickname
            AdvancementPointsItem(
                rank = rank,
                points = points,
                teamName = teamName,
                isDistrictEvent = isDistrictEvent,
                onTeamClick = onTeamClick,
            )
        }
    }
}

@Composable
private fun AdvancementPointsItem(
    rank: Int,
    points: EventAdvancementPoints,
    teamName: String?,
    isDistrictEvent: Boolean,
    onTeamClick: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "advancement_chevron_rotation",
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 10.dp),
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
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onTeamClick(points.teamKey) },
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
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(rotationAngle),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(start = 56.dp, bottom = 8.dp),
            ) {
                advancementBreakdownRows(points, isDistrictEvent).forEach { (label, value) ->
                    AdvancementBreakdownRow(label, value)
                }
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = points.total.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        HorizontalDivider()
    }
}

@Composable
private fun AdvancementBreakdownRow(label: String, value: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

