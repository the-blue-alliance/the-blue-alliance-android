package com.thebluealliance.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.getGroup
import com.thebluealliance.android.domain.getShortLabel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.domain.rpBonuses
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchList(
    matches: List<Match>?,
    playoffType: PlayoffType,
    onNavigateToMatch: (String) -> Unit,
    headerContent: (LazyListScope.() -> Unit)? = null,
    headerItemCount: Int = 0,
) {
    if (matches == null || matches.isEmpty()) {
        LazyColumn(Modifier.fillMaxSize()) {
            headerContent?.invoke(this)
            item {
                Box(
                    Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (matches == null) CircularProgressIndicator()
                    else Text("No matches", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        return
    }

    val grouped = remember(matches) {
        val sorted = matches.sortedWith(
            compareBy({ it.compLevel.order }, { it.setNumber }, { it.matchNumber })
        )
        sorted.groupBy { it.getGroup(playoffType) }
    }

    // Calculate index of first unplayed match for auto-scroll
    val headerOffset = headerItemCount
    val firstUnplayedIndex = run {
        var index = headerOffset
        for ((_, levelMatches) in grouped) {
            index++ // group header
            for (match in levelMatches) {
                if (match.redScore < 0) return@run index
                index++
            }
        }
        -1
    }
    // Scroll so the last few played matches are visible above the first unplayed
    val scrollTarget = if (firstUnplayedIndex > 2) firstUnplayedIndex - 2 else 0
    val listState = rememberLazyListState()
    LaunchedEffect(scrollTarget) {
        if (scrollTarget > 0) {
            listState.scrollToItem(scrollTarget)
        }
    }

    val headerInfos = remember(grouped, headerOffset) {
        buildList {
            var index = headerOffset
            grouped.forEach { (group, levelMatches) ->
                val headerKey = "match_header_${group.label}"
                add(SectionHeaderInfo(headerKey, group.label, index))
                index += 1 + levelMatches.size // header + items
            }
        }
    }

    val headerKeys = remember(headerInfos) { headerInfos.map { it.key }.toSet() }

    val stuckHeaderKey by remember {
        derivedStateOf {
            val stuck = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { item ->
                    val key = item.key as? String
                    key != null && key in headerKeys && item.offset <= 0
                }?.key as? String
            stuck ?: headerInfos.firstOrNull()?.key
        }
    }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        if (headerContent != null) {
            headerContent()
        }
        grouped.forEach { (group, levelMatches) ->
            val headerKey = "match_header_${group.label}"
            stickyHeader(key = headerKey) {
                SectionHeader(
                    label = group.label,
                    isStuck = stuckHeaderKey == headerKey,
                    allHeaders = headerInfos,
                    onHeaderSelected = { info ->
                        coroutineScope.launch {
                            listState.animateScrollToItem(info.itemIndex)
                        }
                    },
                )
            }
            items(levelMatches, key = { it.key }) { match ->
                MatchItem(
                    match = match,
                    playoffType = playoffType,
                    onClick = { onNavigateToMatch(match.key) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun MatchItem(
    match: Match,
    playoffType: PlayoffType,
    onClick: () -> Unit
) {
    val label = match.getShortLabel(playoffType)
    val isPlayed = match.redScore >= 0

    val rpBonuses = remember(match.scoreBreakdown) { match.rpBonuses() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.15f),
        )
        Column(modifier = Modifier.weight(0.35f)) {
            Text(
                text = match.redTeamKeys.joinToString(", ") { it.removePrefix("frc") },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (match.winningAlliance == "red") FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.error,
            )
            Text(
                text = match.blueTeamKeys.joinToString(", ") { it.removePrefix("frc") },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (match.winningAlliance == "blue") FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        if (isPlayed) {
            if (rpBonuses != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    RpDots(rpBonuses.red, MaterialTheme.colorScheme.error)
                    RpDots(rpBonuses.blue, MaterialTheme.colorScheme.primary)
                }
            }
            Column(
                modifier = Modifier.weight(0.15f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = match.redScore.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (match.winningAlliance == "red") FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = match.blueScore.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (match.winningAlliance == "blue") FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        } else {
            Column(
                modifier = Modifier.weight(0.15f),
                horizontalAlignment = Alignment.End,
            ) {
                val displayTime = match.predictedTime ?: match.time
                val isEstimate = match.predictedTime != null && match.time != null &&
                    kotlin.math.abs(match.predictedTime - match.time) > 60
                Text(
                    text = formatMatchTime(displayTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (isEstimate) {
                    Text(
                        text = "(est.)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private val matchTimeFormat = java.time.format.DateTimeFormatter.ofPattern(
    "EEE h:mma", java.util.Locale.US,
)

fun formatMatchTime(epochSeconds: Long?): String {
    if (epochSeconds == null) return "\u2014"
    val instant = java.time.Instant.ofEpochSecond(epochSeconds)
    return matchTimeFormat.format(instant.atZone(java.time.ZoneId.systemDefault()))
        .replace("AM", "a").replace("PM", "p")
}

@Composable
private fun RpDots(bonuses: List<Boolean>, achievedColor: Color) {
    Row(
        modifier = Modifier.padding(end = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        bonuses.forEach { achieved ->
            Canvas(modifier = Modifier.size(6.dp)) {
                if (achieved) {
                    drawCircle(
                        color = achievedColor,
                        radius = size.minDimension / 2,
                    )
                } else {
                    drawCircle(
                        color = Color(0xFF9CA3AF),
                        radius = size.minDimension / 2 - 1.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }
            }
        }
    }
}

