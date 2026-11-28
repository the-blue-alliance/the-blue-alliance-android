package com.thebluealliance.android.ui.matches

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.thebluealliance.android.domain.AllianceRpBonuses
import com.thebluealliance.android.domain.BREAKDOWN_MISSING
import com.thebluealliance.android.domain.MatchAdvancementMsgs
import com.thebluealliance.android.domain.NO_VALUE
import com.thebluealliance.android.domain.camelCaseToLabel
import com.thebluealliance.android.domain.formatBreakdownValue
import com.thebluealliance.android.domain.getAdvancement
import com.thebluealliance.android.domain.getFullLabel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.domain.model.displayTitle
import com.thebluealliance.android.domain.rpBonuses
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.ErrorBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.common.TouchPullToRefreshBox
import com.thebluealliance.android.ui.common.shareTbaUrl
import com.thebluealliance.android.ui.components.EscapeHandler
import com.thebluealliance.android.ui.components.LocalDetailPaneChrome
import com.thebluealliance.android.ui.components.MediaGridItem
import com.thebluealliance.android.ui.components.MediaGridRow
import com.thebluealliance.android.ui.components.ReadingWidthBox
import com.thebluealliance.android.ui.components.RpDots
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TooltipIconButton
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarTitle
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.components.mediaColumns
import com.thebluealliance.android.ui.components.mediaUrl
import com.thebluealliance.android.ui.components.paddedClickable
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.returnsFocus
import com.thebluealliance.android.ui.components.verticalScrollbar
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.ui.theme.allianceColors
import com.thebluealliance.android.util.teamNumber
import kotlin.math.roundToInt

/** Width breakpoints the match layout uses. */
internal data class MatchDetailWidths(
    val wide: Boolean,
    val expanded: Boolean,
)

/** Breakpoints from [paneWidth] when shown as a detail pane, else from the window. */
internal fun matchDetailWidths(
    windowSizeClass: WindowSizeClass,
    paneWidth: Dp?,
): MatchDetailWidths =
    if (paneWidth != null) {
        MatchDetailWidths(
            wide = paneWidth.value >= WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
            expanded = paneWidth.value >= WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
        )
    } else {
        MatchDetailWidths(
            wide =
                windowSizeClass.isWidthAtLeastBreakpoint(
                    WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
                ),
            expanded =
                windowSizeClass.isWidthAtLeastBreakpoint(
                    WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
                ),
        )
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToTeamEvent: (teamKey: String, eventKey: String) -> Unit = { _, _ -> },
    onNavigateToEvent: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit,
    viewModel: MatchDetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val refreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    // LoadingBox already shows progress until the match first loads.
    val isRefreshing =
        refreshing && (uiState.match != null || uiState.loadFailed || uiState.notFound)
    val context = LocalContext.current

    // As a detail pane the event's bar is above, so this bar is a surface-colored pane header
    // with Close instead of Back, and no Search.
    val paneChrome = LocalDetailPaneChrome.current
    // Esc deselects the match and closes the pane, like its Close button.
    EscapeHandler(enabled = paneChrome != null) { paneChrome?.onClose?.invoke() }
    val shareAction =
        uiState.match?.let { match ->
            TopBarAction(
                label = "Share",
                icon = TbaIcons.Share,
                onClick = {
                    val eventLabel = uiState.eventName?.let { "${uiState.year} $it - " } ?: ""
                    context.shareTbaUrl(
                        title = "$eventLabel${match.getFullLabel(uiState.playoffType)}",
                        url = "https://www.thebluealliance.com/match/${match.key}",
                    )
                },
            )
        }
    val title: @Composable () -> Unit = {
        TopBarTitle(
            text = uiState.match?.getFullLabel(uiState.playoffType) ?: "Match",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    Scaffold(
        contentWindowInsets =
            if (paneChrome != null) {
                WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.End)
            } else {
                ScaffoldDefaults.contentWindowInsets
            },
        topBar = {
            if (paneChrome != null) {
                TBATopAppBar(
                    windowInsets = WindowInsets(0),
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        ),
                    onRefresh = viewModel::refresh,
                    isRefreshing = isRefreshing,
                    title = title,
                    secondaryActions = listOfNotNull(shareAction),
                    closeAction =
                        TopBarAction(
                            label = "Close match",
                            icon = TbaIcons.Close,
                            onClick = paneChrome.onClose,
                        ),
                )
            } else {
                TBATopAppBar(
                    onRefresh = viewModel::refresh,
                    isRefreshing = isRefreshing,
                    title = title,
                    navigationIcon = {
                        TooltipIconButton("Back", TbaIcons.ArrowBack, onNavigateUp)
                    },
                    secondaryActions =
                        listOfNotNull(
                            TopBarAction(
                                label = "Search",
                                icon = TbaIcons.Search,
                                onClick = onNavigateToSearch,
                            ),
                            shareAction,
                        ),
                )
            }
        },
    ) { innerPadding ->
        TouchPullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier =
                Modifier
                    .fillMaxSize()
                    .pageBackground(),
        ) {
            val match = uiState.match
            if (match == null) {
                if (uiState.notFound) {
                    // A 404 is TBA's answer, not a failed fetch, so Retry would only get it again.
                    EmptyBox(
                        modifier = Modifier.padding(innerPadding),
                        message = "Match not found",
                    )
                } else if (uiState.loadFailed) {
                    ErrorBox(
                        modifier = Modifier.padding(innerPadding),
                        message = "Couldn't load match",
                        onRetry = viewModel::refresh,
                    )
                } else {
                    LoadingBox(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            } else {
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    // Wide windows inset the dividers to the text margin and show video at 16:9. A
                    // detail pane goes by its own width, not the window's.
                    val widths =
                        matchDetailWidths(
                            windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
                            paneWidth = if (paneChrome != null) maxWidth else null,
                        )
                    val wide = widths.wide
                    val dividerModifier =
                        Modifier.padding(horizontal = if (wide) 16.dp else 0.dp, vertical = 8.dp)
                    val mediaItems =
                        uiState.videos
                            .filter { mediaUrl(it.type, it.key) != null }
                            .map { MediaGridItem(type = it.type, foreignKey = it.key) }
                    val videoBesideScore =
                        matchVideoBesideScore(
                            expandedWidth = widths.expanded,
                            videoCount = mediaItems.size,
                        )
                    val scoreSeparatorWidth = scoreSeparatorWidth()
                    val summary: @Composable () -> Unit = {
                        EventInfo(
                            eventName = uiState.eventName,
                            eventKey = uiState.eventKey,
                            formattedTime = uiState.formattedTime,
                            // A pane already sits inside this event.
                            onNavigateToEvent = onNavigateToEvent.takeIf { paneChrome == null },
                        )
                        ScoreSummary(match, uiState.playoffType)
                        HorizontalDivider(modifier = dividerModifier)
                        AllianceTeams(
                            redTeamKeys = match.redTeamKeys,
                            blueTeamKeys = match.blueTeamKeys,
                            redPlayoffAllianceTitle = match.redPlayoffAlliance?.displayTitle,
                            bluePlayoffAllianceTitle = match.bluePlayoffAlliance?.displayTitle,
                            onTeamClick = { teamKey ->
                                val ek = uiState.eventKey
                                if (ek != null) onNavigateToTeamEvent(teamKey, ek)
                            },
                        )
                    }
                    val videoHeader: @Composable (Modifier) -> Unit = { modifier ->
                        Text(
                            text = "Match video",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = modifier.padding(vertical = 8.dp),
                        )
                    }
                    ReadingWidthBox(innerPadding, Modifier.fillMaxSize()) { readingPadding ->
                        val scrollbarState = rememberLazyListState()
                        LazyColumn(
                            state = scrollbarState,
                            modifier =
                                Modifier.fillMaxSize().verticalScrollbar(
                                    scrollbarState,
                                    readingPadding,
                                ),
                            contentPadding = readingPadding,
                        ) {
                            if (videoBesideScore) {
                                // Result, teams and video share the first view on wide windows,
                                // instead of the video falling below the fold.
                                item(key = "summary_and_media") {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) { summary() }
                                        // The left column's 16dp end margin is the gutter.
                                        Column(
                                            modifier = Modifier.weight(1f).padding(end = 16.dp),
                                        ) {
                                            videoHeader(Modifier)
                                            MediaGridRow(
                                                items = mediaItems,
                                                modifier = Modifier.padding(vertical = 4.dp),
                                                columns =
                                                    mediaColumns(
                                                        mediaItems.size,
                                                        MATCH_VIDEO_COLUMNS,
                                                    ),
                                                aspectRatio = 16f / 9f,
                                            )
                                        }
                                    }
                                }
                            } else {
                                item(key = "summary") {
                                    Column { summary() }
                                }
                                if (mediaItems.isNotEmpty()) {
                                    item(key = "media_header") {
                                        HorizontalDivider(modifier = dividerModifier)
                                        videoHeader(Modifier.padding(horizontal = 16.dp))
                                    }
                                    item(key = "media_grid") {
                                        val columns =
                                            mediaColumns(mediaItems.size, MATCH_VIDEO_COLUMNS)
                                        MediaGridRow(
                                            items = mediaItems,
                                            // Wide windows keep the tiles on the 16dp text margin.
                                            modifier =
                                                Modifier.padding(
                                                    horizontal = if (wide) 16.dp else 8.dp,
                                                    vertical = 4.dp,
                                                ),
                                            columns = columns,
                                            // A lone video spans the column at its own 16:9.
                                            aspectRatio =
                                                if (wide || columns == 1) 16f / 9f else 1f,
                                        )
                                    }
                                }
                            }

                            // Score breakdown
                            val breakdown = uiState.scoreBreakdown
                            if (breakdown != null) {
                                // Wide windows keep red and blue values near their scores.
                                val breakdownWidth =
                                    when {
                                        videoBesideScore -> Modifier.fillMaxWidth(0.5f)
                                        wide -> Modifier.breakdownWidth()
                                        else -> Modifier
                                    }
                                item(key = "breakdown_header") {
                                    HorizontalDivider(modifier = dividerModifier)
                                    Text(
                                        text = "Score breakdown",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier =
                                            breakdownWidth
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                                .semantics { heading() },
                                    )
                                }

                                val redAllianceTitle = match.redPlayoffAlliance?.displayTitle
                                val blueAllianceTitle = match.bluePlayoffAlliance?.displayTitle
                                val redBreakdown = breakdown["red"] ?: emptyMap()
                                val blueBreakdown = breakdown["blue"] ?: emptyMap()
                                val orderedFields =
                                    getOrderedBreakdownFields(
                                        year = uiState.year,
                                        redBreakdown = redBreakdown,
                                        blueBreakdown = blueBreakdown,
                                    )

                                items(
                                    orderedFields,
                                    key = { "breakdown_${it.first}" },
                                ) { (apiKey, label) ->
                                    BreakdownRow(
                                        modifier = breakdownWidth,
                                        redPlayoffAllianceTitle = redAllianceTitle,
                                        bluePlayoffAllianceTitle = blueAllianceTitle,
                                        // Beside the video, each value sits under its score.
                                        scoreSeparatorWidth =
                                            scoreSeparatorWidth.takeIf { videoBesideScore },
                                        label = label,
                                        redValue =
                                            formatBreakdownValue(
                                                apiKey,
                                                redBreakdown[apiKey] ?: BREAKDOWN_MISSING,
                                            ),
                                        blueValue =
                                            formatBreakdownValue(
                                                apiKey,
                                                blueBreakdown[apiKey] ?: BREAKDOWN_MISSING,
                                            ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventInfo(
    eventName: String?,
    eventKey: String?,
    formattedTime: String?,
    onNavigateToEvent: ((String) -> Unit)?,
) {
    if (eventName == null && formattedTime == null) return
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
    ) {
        if (eventName != null && onNavigateToEvent == null) {
            Text(
                text = eventName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            )
        } else if (eventName != null && eventKey != null && onNavigateToEvent != null) {
            // Full-width tap target (EventRow idiom) so hover/ripple reads as a row rather
            // than hugging the text; the 16.dp inset keeps the text where it was.
            Text(
                text = eventName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .returnsFocus()
                        .handCursor()
                        .clickable(onClickLabel = "View event", role = Role.Button) {
                            onNavigateToEvent(eventKey)
                        }.padding(horizontal = 16.dp, vertical = 2.dp),
            )
        }
        if (formattedTime != null) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 2.dp),
            )
        }
    }
}

@Composable
private fun RowScope.AllianceScoreSummaryCol(
    match: Match,
    rpBonuses: AllianceRpBonuses?,
    advancementMsgs: MatchAdvancementMsgs?,
    alliance: String,
) {
    val mainColor =
        if (alliance ==
            "red"
        ) {
            MaterialTheme.allianceColors.red
        } else {
            MaterialTheme.allianceColors.blue
        }
    val score = if (alliance == "red") match.redScore else match.blueScore
    val playoffAlliance =
        if (alliance ==
            "red"
        ) {
            match.redPlayoffAlliance
        } else {
            match.bluePlayoffAlliance
        }
    val rpBonus = if (alliance == "red") rpBonuses?.red else rpBonuses?.blue
    val advancementMsg = if (alliance == "red") advancementMsgs?.red else advancementMsgs?.blue

    val scoreState = allianceScoreState(match.winningAlliance, alliance, score)
    // One stop per alliance; the win is otherwise only bold.
    Column(
        modifier =
            Modifier
                .weight(1f)
                .semantics(mergeDescendants = true) {
                    scoreState?.let { stateDescription = it }
                },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = playoffAlliance?.displayTitle ?: alliance.replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.titleSmall,
            color = mainColor,
            modifier =
                allianceLabelDescription(playoffAlliance?.displayTitle, alliance)?.let {
                    Modifier.semantics { contentDescription = it }
                } ?: Modifier,
        )
        Text(
            text = if (score < 0) NO_VALUE else score.toString(),
            style = MaterialTheme.typography.displaySmall,
            fontWeight =
                if (match.winningAlliance ==
                    alliance
                ) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
            color = mainColor,
            modifier = Modifier.semantics { contentDescription = scoreDescription(score) },
        )
        rpBonus?.let {
            RpDots(it, mainColor, Modifier.padding(top = 4.dp), dotSize = 8.dp, gap = 4.dp)
        }
        advancementMsg?.let {
            Box(
                modifier =
                    Modifier
                        .heightIn(min = 36.dp) // Reserve consistent space
                        .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight =
                        if (match.winningAlliance ==
                            alliance
                        ) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        },
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 14.sp, // Tighter line spacing
                    modifier =
                        Modifier
                            .padding(horizontal = 4.dp)
                            .padding(top = 4.dp),
                )
            }
        }
    }
}

/** En dash, as between the two scores of a game. */
private const val SCORE_SEPARATOR = "\u2013"
private val ScoreSeparatorPadding = 16.dp

/** Width of the separator between the two scores in [ScoreSummary], padding included. */
@Composable
private fun scoreSeparatorWidth(): Dp {
    val measurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.displaySmall
    val textWidth =
        remember(measurer, style) { measurer.measure(SCORE_SEPARATOR, style).size.width }
    return with(LocalDensity.current) { textWidth.toDp() } + ScoreSeparatorPadding * 2
}

/**
 * Horizontal centres of the red and blue score columns in a [ScoreSummary] row of [rowWidth], whose
 * two equal columns flank a [separatorWidth] separator inside [padding] on each side.
 */
internal fun scoreColumnCenters(
    rowWidth: Float,
    separatorWidth: Float,
    padding: Float,
): Pair<Float, Float> {
    val columnWidth = ((rowWidth - padding * 2 - separatorWidth) / 2).coerceAtLeast(0f)
    return (padding + columnWidth / 2) to (rowWidth - padding - columnWidth / 2)
}

@Composable
private fun ScoreSummary(
    match: Match,
    playoffType: PlayoffType,
) {
    val rpBonuses = remember(match.scoreBreakdown) { match.rpBonuses() }
    val advancementMsgs =
        remember(
            match.winningAlliance,
            match.compLevel,
            match.setNumber,
            playoffType,
        ) { match.getAdvancement(playoffType) }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AllianceScoreSummaryCol(match, rpBonuses, advancementMsgs, "red")
        Text(
            text = SCORE_SEPARATOR,
            style = MaterialTheme.typography.displaySmall,
            modifier =
                Modifier
                    .padding(horizontal = ScoreSeparatorPadding)
                    .clearAndSetSemantics {},
        )
        AllianceScoreSummaryCol(match, rpBonuses, advancementMsgs, "blue")
    }
}

@Composable
private fun AllianceTeams(
    redTeamKeys: List<String>,
    blueTeamKeys: List<String>,
    redPlayoffAllianceTitle: String?,
    bluePlayoffAllianceTitle: String?,
    onTeamClick: (String) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        AllianceTeamColumn(
            teamKeys = redTeamKeys,
            color = MaterialTheme.allianceColors.red,
            playoffAllianceTitle = redPlayoffAllianceTitle,
            alliance = "red",
            onTeamClick = onTeamClick,
        )

        // Invisible separator to match ScoreSummary alignment
        Text(
            text = SCORE_SEPARATOR,
            style = MaterialTheme.typography.displaySmall,
            color = Color.Transparent,
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .clearAndSetSemantics {},
        )

        AllianceTeamColumn(
            teamKeys = blueTeamKeys,
            color = MaterialTheme.allianceColors.blue,
            playoffAllianceTitle = bluePlayoffAllianceTitle,
            alliance = "blue",
            onTeamClick = onTeamClick,
        )
    }
}

/**
 * Smallest a team number's tap target gets, in both directions. The default leaves today's
 * targets as they are: a 24dp line plus 4dp above and below.
 */
private val TeamTargetMinSize = 32.dp

/**
 * Space between neighbouring team number targets, in a row or a stack. Targets already carry
 * 12dp side padding, so numbers in a row sit 24dp apart, and stacked targets touch as before.
 */
private val TeamTargetSpacing = 0.dp

@Composable
private fun RowScope.AllianceTeamColumn(
    teamKeys: List<String>,
    color: Color,
    playoffAllianceTitle: String?,
    alliance: String,
    onTeamClick: (String) -> Unit,
    targetMinSize: Dp = TeamTargetMinSize,
    targetSpacing: Dp = TeamTargetSpacing,
) {
    AllianceTeamNumbers(spacing = targetSpacing, modifier = Modifier.weight(1f)) {
        teamKeys.forEach { key ->
            Box(
                modifier =
                    Modifier
                        .sizeIn(minWidth = targetMinSize, minHeight = targetMinSize)
                        .paddedClickable(
                            MaterialTheme.shapes.small,
                            horizontal = 12.dp,
                            vertical = 4.dp,
                            onClickLabel = "View team ${key.teamNumber}",
                        ) { onTeamClick(key) }
                        .semantics {
                            contentDescription =
                                allianceTeamDescription(
                                    playoffAllianceTitle,
                                    alliance,
                                    key.teamNumber,
                                )
                        },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = key.teamNumber,
                    style = MaterialTheme.typography.bodyLarge,
                    color = color,
                )
            }
        }
    }
}

/**
 * Lays an alliance's team targets out in one row centred in this column, under that alliance's
 * score, when they all fit side by side; otherwise stacks them, each centred. Never wraps part of
 * the row.
 */
@Composable
private fun AllianceTeamNumbers(
    spacing: Dp,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val spacingPx = spacing.roundToPx()
        val placeables =
            measurables.map {
                it.measure(
                    constraints.copy(minWidth = 0, minHeight = 0),
                )
            }
        val widths = placeables.map { it.width }
        val gaps = spacingPx * (placeables.size - 1).coerceAtLeast(0)
        val inRow = teamsFitInRow(widths, spacingPx, constraints.maxWidth)
        val contentWidth = if (inRow) widths.sum() + gaps else widths.maxOrNull() ?: 0
        val width =
            if (constraints.hasBoundedWidth) constraints.maxWidth else contentWidth
        if (inRow) {
            val height = placeables.maxOfOrNull { it.height } ?: 0
            layout(width, height) {
                var x = (width - contentWidth) / 2
                placeables.forEach {
                    it.placeRelative(x, (height - it.height) / 2)
                    x += it.width + spacingPx
                }
            }
        } else {
            val height = placeables.sumOf { it.height } + gaps
            layout(width, height) {
                var y = 0
                placeables.forEach {
                    it.place(
                        Alignment.CenterHorizontally.align(it.width, width, layoutDirection),
                        y,
                    )
                    y += it.height + spacingPx
                }
            }
        }
    }
}

/** Whether targets of [widths], [spacing] apart, fit side by side in [availableWidth]. */
internal fun teamsFitInRow(
    widths: List<Int>,
    spacing: Int,
    availableWidth: Int,
): Boolean = widths.sum() + spacing * (widths.size - 1).coerceAtLeast(0) <= availableWidth

/** Widest the score breakdown grows on wide windows. */
private val BreakdownMaxWidth = 560.dp

/** Centers the breakdown at [BreakdownMaxWidth], so its value columns sit near the scores. */
private fun Modifier.breakdownWidth(): Modifier =
    fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
        .widthIn(max = BreakdownMaxWidth)
        .fillMaxWidth()

/**
 * One breakdown line: the red value, the label, then the blue value. With [scoreSeparatorWidth],
 * the row spans a [ScoreSummary]'s width and centres each value under that alliance's score;
 * otherwise it uses fixed shares.
 */
@Composable
private fun BreakdownRow(
    label: String,
    redValue: String,
    blueValue: String,
    modifier: Modifier = Modifier,
    redPlayoffAllianceTitle: String? = null,
    bluePlayoffAllianceTitle: String? = null,
    scoreSeparatorWidth: Dp? = null,
) {
    // One stop per row that says which value is whose.
    val rowModifier =
        modifier.clearAndSetSemantics {
            contentDescription =
                breakdownRowDescription(
                    label,
                    redValue,
                    blueValue,
                    redPlayoffAllianceTitle,
                    bluePlayoffAllianceTitle,
                )
        }
    val red: @Composable (Modifier) -> Unit = { cell ->
        Text(
            text = redValue,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.allianceColors.red,
            modifier = cell,
            textAlign = TextAlign.Center,
        )
    }
    val centre: @Composable (Modifier) -> Unit = { cell ->
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = cell,
            textAlign = TextAlign.Center,
        )
    }
    val blue: @Composable (Modifier) -> Unit = { cell ->
        Text(
            text = blueValue,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.allianceColors.blue,
            modifier = cell,
            textAlign = TextAlign.Center,
        )
    }
    if (scoreSeparatorWidth != null) {
        UnderScoresRow(
            separatorWidth = scoreSeparatorWidth,
            modifier = rowModifier.fillMaxWidth().padding(vertical = 2.dp),
            red = { red(Modifier) },
            label = { centre(Modifier) },
            blue = { blue(Modifier) },
        )
        return
    }
    Row(
        modifier =
            rowModifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        red(Modifier.weight(0.2f))
        centre(Modifier.weight(0.6f))
        blue(Modifier.weight(0.2f))
    }
}

/**
 * Centres [red] and [blue] at the score column centres of a [ScoreSummary] as wide as this row, and
 * [label] between them. A label too long for the gap pushes the values outward rather than wrap,
 * until the values reach the row's padding.
 */
@Composable
private fun UnderScoresRow(
    separatorWidth: Dp,
    modifier: Modifier,
    red: @Composable () -> Unit,
    label: @Composable () -> Unit,
    blue: @Composable () -> Unit,
) {
    Layout(contents = listOf(red, label, blue), modifier = modifier) { measurables, constraints ->
        val rowWidth = constraints.maxWidth
        val padding = 16.dp.toPx()
        val gap = 8.dp.toPx()
        val valueConstraints =
            Constraints(maxWidth = (rowWidth / 2f - padding - gap).toInt().coerceAtLeast(0))
        val redPlaceable = measurables[0].first().measure(valueConstraints)
        val bluePlaceable = measurables[2].first().measure(valueConstraints)
        val widestValue = maxOf(redPlaceable.width, bluePlaceable.width)
        val labelMax = (rowWidth - (padding + widestValue + gap) * 2).toInt().coerceAtLeast(0)
        val labelPlaceable = measurables[1].first().measure(Constraints(maxWidth = labelMax))
        val (redCentre, blueCentre) =
            breakdownValueCentres(
                scoreCentres =
                    scoreColumnCenters(
                        rowWidth.toFloat(),
                        separatorWidth.toPx(),
                        padding,
                    ),
                rowWidth = rowWidth.toFloat(),
                labelWidth = labelPlaceable.width.toFloat(),
                redWidth = redPlaceable.width.toFloat(),
                blueWidth = bluePlaceable.width.toFloat(),
                gap = gap,
            )
        val rowHeight = maxOf(redPlaceable.height, labelPlaceable.height, bluePlaceable.height)
        layout(rowWidth, rowHeight) {
            fun Placeable.placeCentredAt(x: Float) =
                placeRelative((x - this.width / 2f).roundToInt(), (rowHeight - this.height) / 2)
            redPlaceable.placeCentredAt(redCentre)
            labelPlaceable.placeCentredAt(rowWidth / 2f)
            bluePlaceable.placeCentredAt(blueCentre)
        }
    }
}

/**
 * Where a breakdown row centres its red and blue values: at [scoreCentres], or further out when a
 * [labelWidth] label centred in [rowWidth] would come within [gap] of them.
 */
internal fun breakdownValueCentres(
    scoreCentres: Pair<Float, Float>,
    rowWidth: Float,
    labelWidth: Float,
    redWidth: Float,
    blueWidth: Float,
    gap: Float,
): Pair<Float, Float> {
    val labelStart = (rowWidth - labelWidth) / 2
    val labelEnd = labelStart + labelWidth
    val red = minOf(scoreCentres.first, labelStart - gap - redWidth / 2)
    val blue = maxOf(scoreCentres.second, labelEnd + gap + blueWidth / 2)
    return red to blue
}

internal fun getOrderedBreakdownFields(
    year: Int,
    redBreakdown: Map<String, String>,
    blueBreakdown: Map<String, String>,
): List<Pair<String, String>> {
    val yearFields = breakdownFieldsByYear[year]
    if (yearFields == null) {
        // Fallback: show all keys with camelCase-to-label conversion
        val allKeys =
            (redBreakdown.keys + blueBreakdown.keys)
                .distinct()
                .filter { it != "totalPoints" }
        return allKeys.map { it to camelCaseToLabel(it) }
    }

    val knownKeys = yearFields.map { it.first }.toSet()
    val result = yearFields.toMutableList()

    // Append any unrecognized keys at the end
    val allKeys = (redBreakdown.keys + blueBreakdown.keys).distinct()
    for (key in allKeys) {
        if (key !in knownKeys) {
            result.add(key to camelCaseToLabel(key))
        }
    }
    return result
}

private val breakdownFields2026 =
    listOf(
        "autoTowerRobot1" to "Auto tower 1",
        "autoTowerRobot2" to "Auto tower 2",
        "autoTowerRobot3" to "Auto tower 3",
        "autoTowerPoints" to "Auto tower",
        "totalAutoPoints" to "Total auto",
        "totalTeleopPoints" to "Total teleop",
        "endGameTowerRobot1" to "Endgame 1",
        "endGameTowerRobot2" to "Endgame 2",
        "endGameTowerRobot3" to "Endgame 3",
        "endGameTowerPoints" to "Endgame tower",
        "totalTowerPoints" to "Total tower",
        "energizedAchieved" to "Energized bonus",
        "superchargedAchieved" to "Supercharged bonus",
        "traversalAchieved" to "Traversal bonus",
        "g206Penalty" to "G206 penalty",
        "minorFoulCount" to "Minor fouls committed",
        "majorFoulCount" to "Major fouls committed",
        "foulPoints" to "Foul points received",
        "adjustPoints" to "Adjust",
        "totalPoints" to "Total",
        "rp" to "RP",
    )

private val breakdownFields2025 =
    listOf(
        "autoLineRobot1" to "Auto leave 1",
        "autoLineRobot2" to "Auto leave 2",
        "autoLineRobot3" to "Auto leave 3",
        "autoMobilityPoints" to "Auto mobility",
        "autoCoralPoints" to "Auto coral",
        "autoPoints" to "Total auto",
        "teleopCoralPoints" to "Teleop coral",
        "wallAlgaeCount" to "Processor algae",
        "netAlgaeCount" to "Net algae",
        "algaePoints" to "Algae points",
        "endGameRobot1" to "Endgame 1",
        "endGameRobot2" to "Endgame 2",
        "endGameRobot3" to "Endgame 3",
        "endGameBargePoints" to "Barge points",
        "teleopPoints" to "Total teleop",
        "coopertitionCriteriaMet" to "Coopertition",
        "autoBonusAchieved" to "Auto bonus",
        "coralBonusAchieved" to "Coral bonus",
        "bargeBonusAchieved" to "Barge bonus",
        "foulCount" to "Fouls committed",
        "techFoulCount" to "Tech fouls committed",
        "foulPoints" to "Foul points received",
        "adjustPoints" to "Adjust",
        "totalPoints" to "Total",
        "rp" to "RP",
    )

private val breakdownFields2024 =
    listOf(
        "autoLineRobot1" to "Auto leave 1",
        "autoLineRobot2" to "Auto leave 2",
        "autoLineRobot3" to "Auto leave 3",
        "autoLeavePoints" to "Auto leave points",
        "autoAmpNoteCount" to "Auto amp notes",
        "autoSpeakerNoteCount" to "Auto speaker notes",
        "autoTotalNotePoints" to "Auto note points",
        "autoPoints" to "Total auto",
        "teleopAmpNoteCount" to "Teleop amp notes",
        "teleopSpeakerNoteCount" to "Teleop speaker notes",
        "teleopSpeakerNoteAmplifiedCount" to "Amplified speaker",
        "teleopTotalNotePoints" to "Teleop note points",
        "endGameRobot1" to "Endgame 1",
        "endGameRobot2" to "Endgame 2",
        "endGameRobot3" to "Endgame 3",
        "endGameHarmonyPoints" to "Harmony",
        "endGameNoteInTrapPoints" to "Trap",
        "endGameOnStagePoints" to "On stage",
        "endGameParkPoints" to "Park",
        "teleopPoints" to "Total teleop",
        "coopertitionBonusAchieved" to "Coopertition",
        "melodyBonusAchieved" to "Melody bonus",
        "ensembleBonusAchieved" to "Ensemble bonus",
        "foulCount" to "Fouls committed",
        "techFoulCount" to "Tech fouls committed",
        "foulPoints" to "Foul points received",
        "adjustPoints" to "Adjust",
        "totalPoints" to "Total",
        "rp" to "RP",
    )

private val breakdownFields2023 =
    listOf(
        "autoLineRobot1" to "Auto line 1",
        "autoLineRobot2" to "Auto line 2",
        "autoLineRobot3" to "Auto line 3",
        "autoMobilityPoints" to "Auto mobility",
        "autoGamePieceCount" to "Auto game pieces",
        "autoGamePiecePoints" to "Auto game piece points",
        "autoChargeStationPoints" to "Auto charge station",
        "autoPoints" to "Total auto",
        "teleopGamePieceCount" to "Teleop game pieces",
        "teleopGamePiecePoints" to "Teleop game piece points",
        "endGameRobot1" to "Endgame 1",
        "endGameRobot2" to "Endgame 2",
        "endGameRobot3" to "Endgame 3",
        "endGameChargeStationPoints" to "Endgame charge station",
        "endGameParkPoints" to "Endgame park",
        "teleopPoints" to "Total teleop",
        "activationBonusAchieved" to "Activation bonus",
        "sustainabilityBonusAchieved" to "Sustainability bonus",
        "coopertitionCriteriaMet" to "Coopertition",
        "foulCount" to "Fouls committed",
        "techFoulCount" to "Tech fouls committed",
        "foulPoints" to "Foul points received",
        "adjustPoints" to "Adjust",
        "totalPoints" to "Total",
        "rp" to "RP",
    )

private val breakdownFields2015 =
    listOf(
        "robot_set" to "Robot set",
        "container_set" to "Container set",
        "tote_set" to "Tote set",
        "tote_stack" to "Stacked tote set",
        "auto_points" to "Total auto",
        "tote_count_far" to "Totes (far)",
        "tote_count_near" to "Totes (near)",
        "tote_points" to "Tote points",
        "container_count_level1" to "Containers level 1",
        "container_count_level2" to "Containers level 2",
        "container_count_level3" to "Containers level 3",
        "container_count_level4" to "Containers level 4",
        "container_count_level5" to "Containers level 5",
        "container_count_level6" to "Containers level 6",
        "container_points" to "Container points",
        "litter_count_container" to "Litter in containers",
        "litter_count_landfill" to "Litter in landfill",
        "litter_count_unprocessed" to "Unprocessed litter",
        "litter_points" to "Litter points",
        "teleop_points" to "Total teleop",
        "foul_count" to "Fouls committed",
        // In 2015 fouls were deducted from the offending alliance's own score.
        "foul_points" to "Foul points deducted",
        "adjust_points" to "Adjust",
        "total_points" to "Total",
    )

private val breakdownFieldsByYear =
    mapOf(
        2026 to breakdownFields2026,
        2025 to breakdownFields2025,
        2024 to breakdownFields2024,
        2023 to breakdownFields2023,
        2015 to breakdownFields2015,
    )

/** Expanded-width windows put match video beside the score, so both fit in the first view. */
internal fun matchVideoBesideScore(
    expandedWidth: Boolean,
    videoCount: Int,
): Boolean = expandedWidth && videoCount > 0

/** Match videos pair up; [mediaColumns] lets a lone video span the column. */
private const val MATCH_VIDEO_COLUMNS = 2
