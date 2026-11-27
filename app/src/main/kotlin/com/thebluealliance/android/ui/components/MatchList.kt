package com.thebluealliance.android.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.NO_VALUE
import com.thebluealliance.android.domain.getFullLabel
import com.thebluealliance.android.domain.getGroup
import com.thebluealliance.android.domain.getShortLabel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.domain.model.displayTitleShort
import com.thebluealliance.android.domain.rpBonuses
import com.thebluealliance.android.ui.theme.allianceColors
import com.thebluealliance.android.util.teamNumber
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchList(
    matches: List<Match>?,
    playoffType: PlayoffType,
    onNavigateToMatch: (String) -> Unit,
    headerContent: (LazyListScope.() -> Unit)? = null,
    headerItemCount: Int = 0,
    innerPadding: PaddingValues = PaddingValues.Zero,
    selectedMatchKey: String? = null,
) {
    if (matches == null || matches.isEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        ) {
            headerContent?.invoke(this)
            item {
                Box(
                    Modifier.fillParentMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (matches == null) {
                        CircularProgressIndicator()
                    } else {
                        Text("No matches", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
        return
    }

    val grouped =
        remember(matches) {
            val sorted =
                matches.sortedWith(
                    compareBy({ it.compLevel.order }, { it.setNumber }, { it.matchNumber }),
                )
            sorted.groupBy { it.getGroup(playoffType) }
        }

    // Calculate index of first unplayed match for auto-scroll
    val headerOffset = headerItemCount
    val firstUnplayedIndex =
        run {
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
    // A match already selected on arrival (deep link, resize) is scrolled to instead.
    val arrivalSelection = remember { selectedMatchKey }
    val initialSelectedIndex =
        remember(grouped) {
            arrivalSelection?.let { key -> matchListItemIndex(grouped.values, key, headerOffset) }
        }
    // Scroll so the last few played matches are visible above the first unplayed
    val scrollTarget =
        initialSelectedIndex?.let { (it - 2).coerceAtLeast(0) }
            ?: if (firstUnplayedIndex > 2) firstUnplayedIndex - 2 else 0
    val listState = rememberLazyListState()
    LaunchedEffect(scrollTarget) {
        if (scrollTarget > 0) {
            listState.scrollToItem(scrollTarget)
        }
    }

    val headerInfos =
        remember(grouped, headerOffset) {
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
            val stuck =
                listState.layoutInfo.visibleItemsInfo
                    .firstOrNull { item ->
                        val key = item.key as? String
                        key != null && key in headerKeys && item.offset <= 0
                    }?.key as? String
            stuck ?: headerInfos.firstOrNull()?.key
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val headerFocus = rememberStickyHeaderFocusOrder()

    // When the detail pane closes (Esc, Close or Back), keyboard focus goes back to the match it
    // showed, so a keyboard user keeps their place in the list instead of landing on the top bar.
    // After a mouse click the window is still in touch mode, where nothing can take focus (Esc
    // doesn't leave touch mode), so the row waits for the next key that does, such as Tab.
    val inputModeManager = LocalInputModeManager.current
    val returnFocus = remember { FocusRequester() }
    var previousSelection by remember { mutableStateOf(selectedMatchKey) }
    var focusReturnKey by remember { mutableStateOf<String?>(null) }
    if (previousSelection != selectedMatchKey) {
        focusReturnKey = matchFocusReturnKey(previousSelection, selectedMatchKey)
        previousSelection = selectedMatchKey
    }
    LaunchedEffect(focusReturnKey) {
        val key = focusReturnKey ?: return@LaunchedEffect
        snapshotFlow { inputModeManager.inputMode }.first { it == InputMode.Keyboard }
        val index = matchListItemIndex(grouped.values, key, headerOffset)
        if (index != null && listState.layoutInfo.visibleItemsInfo.none { it.index == index }) {
            listState.scrollToItem(index)
        }
        withFrameNanos {}
        returnFocus.requestFocus()
        focusReturnKey = null
    }

    LazyColumn(
        state = listState,
        modifier =
            Modifier
                .fillMaxSize()
                .stickyHeaderFocusOrder(headerFocus)
                .verticalScrollbar(listState, innerPadding),
        contentPadding = innerPadding,
    ) {
        if (headerContent != null) {
            headerContent()
        }
        grouped.forEach { (group, levelMatches) ->
            val headerKey = "match_header_${group.label}"
            tabOrderedStickyHeader(headerFocus, key = headerKey) {
                Box(modifier = Modifier.tableWidth()) {
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
            }
            items(levelMatches, key = { it.key }) { match ->
                Column(modifier = Modifier.tableWidth()) {
                    MatchItem(
                        match = match,
                        playoffType = playoffType,
                        onClick = { onNavigateToMatch(match.key) },
                        selected = match.key == selectedMatchKey,
                        modifier =
                            if (match.key == focusReturnKey) {
                                Modifier.focusRequester(returnFocus)
                            } else {
                                Modifier
                            },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

/** Lazy list index of [matchKey] among [groups], each led by a header; null if absent. */
internal fun matchListItemIndex(
    groups: Collection<List<Match>>,
    matchKey: String,
    headerOffset: Int = 0,
): Int? {
    var index = headerOffset
    for (group in groups) {
        index++ // group header
        val position = group.indexOfFirst { it.key == matchKey }
        if (position >= 0) return index + position
        index += group.size
    }
    return null
}

/** The match whose row should get focus back: the one just deselected with nothing replacing it. */
internal fun matchFocusReturnKey(
    previous: String?,
    current: String?,
): String? = if (current == null) previous else null

/** Width of the accent bar on the selected row's start edge. */
private val SelectedRowAccentWidth = 4.dp

/**
 * One match row. Its highlight spans [modifier]'s bounds edge to edge and is square, like a list
 * row. Where the row sits inset from its container (a card-sized block, e.g. Team@Event's Last
 * Match), pass `Modifier.clip(MaterialTheme.shapes.medium)` so the highlight is rounded per the
 * highlight-shape rules in AGENTS.md ("UI conventions").
 */
@Composable
fun MatchItem(
    match: Match,
    playoffType: PlayoffType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val label = match.getShortLabel(playoffType)
    val isPlayed = match.redScore >= 0

    val rpBonuses = remember(match.scoreBreakdown) { match.rpBonuses() }
    val displayTime = match.predictedTime ?: match.time
    val isEstimate =
        match.predictedTime != null &&
            match.time != null &&
            kotlin.math.abs(match.predictedTime - match.time) > 60
    val description =
        remember(match, playoffType, rpBonuses) {
            matchRowDescription(
                match = match,
                label = match.getFullLabel(playoffType),
                rpBonuses = rpBonuses,
                scheduledTime = displayTime?.let(::formatMatchTimeSpoken),
                isEstimate = isEstimate,
            )
        }

    val accent = MaterialTheme.colorScheme.primary
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (selected) {
                        // Brand tint plus a start bar, so the alliance colors keep their contrast.
                        Modifier
                            .background(accent.copy(alpha = 0.12f))
                            .drawBehind {
                                val barWidth = SelectedRowAccentWidth.toPx()
                                val rtl = layoutDirection == LayoutDirection.Rtl
                                val startX = if (rtl) size.width - barWidth else 0f
                                drawRect(
                                    color = accent,
                                    topLeft = Offset(startX, 0f),
                                    size = size.copy(width = barWidth),
                                )
                            }
                    } else {
                        Modifier
                    },
                ).returnsFocus()
                .handCursor()
                .clickable(onClick = onClick)
                .clearAndSetSemantics {
                    contentDescription = description
                    role = Role.Button
                    if (selected) this.selected = true
                    onClick(label = "View match details") {
                        onClick()
                        true
                    }
                }.padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.15f),
        )
        Column(modifier = Modifier.weight(0.35f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                match.redPlayoffAlliance?.let { alliance ->
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color =
                            if (match.winningAlliance == "red") {
                                MaterialTheme.allianceColors.red
                            } else {
                                MaterialTheme.allianceColors.redContainer
                            },
                        contentColor =
                            if (match.winningAlliance ==
                                "red"
                            ) {
                                MaterialTheme.allianceColors.onRed
                            } else {
                                MaterialTheme.allianceColors.onRedContainer
                            },
                    ) {
                        Text(
                            text = alliance.displayTitleShort,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp),
                        )
                    }
                }
                Text(
                    text = formatAllianceTeams(match.redTeamKeys, match.redSurrogateTeamKeys),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight =
                        if (match.winningAlliance ==
                            "red"
                        ) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                    color = MaterialTheme.allianceColors.red,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                match.bluePlayoffAlliance?.let { alliance ->
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color =
                            if (match.winningAlliance ==
                                "blue"
                            ) {
                                MaterialTheme.allianceColors.blue
                            } else {
                                MaterialTheme.allianceColors.blueContainer
                            },
                        contentColor =
                            if (match.winningAlliance ==
                                "blue"
                            ) {
                                MaterialTheme.allianceColors.onBlue
                            } else {
                                MaterialTheme.allianceColors.onBlueContainer
                            },
                    ) {
                        Text(
                            text = alliance.displayTitleShort,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp),
                        )
                    }
                }
                Text(
                    text = formatAllianceTeams(match.blueTeamKeys, match.blueSurrogateTeamKeys),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight =
                        if (match.winningAlliance ==
                            "blue"
                        ) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                    color = MaterialTheme.allianceColors.blue,
                )
            }
        }
        if (isPlayed) {
            if (rpBonuses != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    RpDots(
                        rpBonuses.red,
                        MaterialTheme.allianceColors.red,
                        Modifier.padding(end = 3.dp),
                    )
                    RpDots(
                        rpBonuses.blue,
                        MaterialTheme.allianceColors.blue,
                        Modifier.padding(end = 3.dp),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(0.15f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = match.redScore.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight =
                        if (match.winningAlliance ==
                            "red"
                        ) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                    color = MaterialTheme.allianceColors.red,
                )
                Text(
                    text = match.blueScore.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight =
                        if (match.winningAlliance ==
                            "blue"
                        ) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                    color = MaterialTheme.allianceColors.blue,
                )
            }
        } else {
            Column(
                modifier = Modifier.weight(0.15f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = formatMatchTime(displayTime),
                    style = MaterialTheme.typography.bodyMedium,
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

private fun formatAllianceTeams(
    teamKeys: List<String>,
    surrogateTeamKeys: List<String>,
): String =
    teamKeys.joinToString(", ") { key ->
        if (key in surrogateTeamKeys) "${key.teamNumber}*" else key.teamNumber
    }

private val matchTimeFormat =
    java.time.format.DateTimeFormatter.ofPattern(
        "EEE h:mma",
        java.util.Locale.US,
    )

private val spokenMatchTimeFormat =
    java.time.format.DateTimeFormatter.ofPattern(
        "EEEE h:mm a",
        java.util.Locale.US,
    )

private fun formatMatchTimeSpoken(epochSeconds: Long): String =
    spokenMatchTimeFormat.format(
        java.time.Instant
            .ofEpochSecond(epochSeconds)
            .atZone(java.time.ZoneId.systemDefault()),
    )

fun formatMatchTime(epochSeconds: Long?): String {
    if (epochSeconds == null) return NO_VALUE
    val instant = java.time.Instant.ofEpochSecond(epochSeconds)
    return matchTimeFormat
        .format(instant.atZone(java.time.ZoneId.systemDefault()))
        .replace("AM", "a")
        .replace("PM", "p")
}
