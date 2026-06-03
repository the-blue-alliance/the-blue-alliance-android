@file:OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.thebluealliance.android.tv.ui.events

import androidx.annotation.StringRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.thebluealliance.android.tv.R
import com.thebluealliance.android.tv.data.deeplink.WebcastLauncher
import com.thebluealliance.android.tv.data.model.Event
import com.thebluealliance.android.tv.data.model.EventFeed
import com.thebluealliance.android.tv.data.model.Webcast
import com.thebluealliance.android.tv.data.model.WebcastType
import com.thebluealliance.android.tv.ui.common.PositionFocusedItemInLazyLayout
import com.thebluealliance.android.tv.ui.common.RetryButton
import com.thebluealliance.android.tv.ui.common.StatusMessage
import com.thebluealliance.android.tv.ui.common.focusOnInitialVisibility
import com.thebluealliance.android.tv.ui.common.ifElse
import com.thebluealliance.android.tv.ui.common.requestFocusOnFirstGainingVisibility
import com.thebluealliance.android.tv.ui.theme.TbaBlueBright
import com.thebluealliance.android.tv.ui.theme.TbaCardShape
import com.thebluealliance.android.tv.ui.theme.TbaFocusBorderWidth
import com.thebluealliance.android.tv.ui.theme.TbaIconButtonSize
import com.thebluealliance.android.tv.ui.theme.TbaIconSize
import com.thebluealliance.android.tv.ui.theme.TbaListBottomPadding
import com.thebluealliance.android.tv.ui.theme.TbaOverscanTopPadding
import com.thebluealliance.android.tv.ui.theme.TbaScreenHPadding
import com.thebluealliance.android.tv.ui.theme.TwitchOnChip
import com.thebluealliance.android.tv.ui.theme.TwitchPurple
import com.thebluealliance.android.tv.ui.theme.YouTubeOnChip
import com.thebluealliance.android.tv.ui.theme.YouTubeRed
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val MonthDay: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d")
private val DayOnly: DateTimeFormatter = DateTimeFormatter.ofPattern("d")
private val WebcastDay: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")

private fun Event.dateRangeLabel(): String =
    when {
        startDate == endDate -> startDate.format(MonthDay)
        // Same month: don't repeat it — "May 30 – 31" instead of "May 30 – May 31".
        startDate.month == endDate.month && startDate.year == endDate.year ->
            "${startDate.format(MonthDay)} – ${endDate.format(DayOnly)}"
        else -> "${startDate.format(MonthDay)} – ${endDate.format(MonthDay)}"
    }

/** Picker button label; day-specific casts disambiguate with their date, e.g. "Watch on YouTube (Sat, May 30)". */
@Composable
private fun Webcast.pickerLabel(): String =
    date?.let { stringResource(R.string.watch_on_dated, type.label, it.format(WebcastDay)) }
        ?: stringResource(R.string.watch_on, type.label)

private data class Section(
    @StringRes val titleRes: Int,
    val events: List<Event>,
)

private fun EventFeed.rowSections(): List<Section> =
    buildList {
        // "Happening Now" rather than "Live": these events are running today, but we can't confirm a
        // webcast is actually streaming this second, so we don't claim it.
        if (live.isNotEmpty()) add(Section(R.string.section_happening_now, live))
        if (upcoming.isNotEmpty()) add(Section(R.string.section_upcoming, upcoming))
        if (recent.isNotEmpty()) add(Section(R.string.section_recent, recent))
    }

@Composable
fun EventsScreen(
    viewModel: EventsViewModel,
    onAboutClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pickerEvent by remember { mutableStateOf<Event?>(null) }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when (val s = state) {
            EventsUiState.Loading ->
                WithFixedHeader(
                    usingMockData = false,
                    onAboutClick = onAboutClick,
                ) {
                    LoadingSkeleton()
                }
            is EventsUiState.Error ->
                WithFixedHeader(usingMockData = false, onAboutClick = onAboutClick) {
                    CenteredMessage { ErrorContent(s.messageRes, viewModel::refresh) }
                }
            is EventsUiState.Success ->
                if (s.feed.isEmpty) {
                    WithFixedHeader(usingMockData = s.usingMockData, onAboutClick = onAboutClick) {
                        CenteredMessage { EmptyContent() }
                    }
                } else {
                    EventFeedContent(
                        feed = s.feed,
                        usingMockData = s.usingMockData,
                        onAboutClick = onAboutClick,
                        onEventClick = { event ->
                            if (event.webcasts.size ==
                                1
                            ) {
                                WebcastLauncher.launch(context, event.webcasts.first())
                            } else {
                                pickerEvent = event
                            }
                        },
                    )
                }
        }
    }

    pickerEvent?.let { event ->
        WebcastPicker(
            event = event,
            onPick = { webcast ->
                pickerEvent = null
                WebcastLauncher.launch(context, webcast)
            },
            onDismiss = { pickerEvent = null },
        )
    }
}

@Composable
private fun Header(
    usingMockData: Boolean,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Top-align so the lamp shares its top edge with the "The Blue Alliance" wordmark.
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_tba_lamp),
                contentDescription = null,
                modifier = Modifier.size(26.dp),
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (usingMockData) {
            Spacer(Modifier.width(20.dp))
            Chip(
                stringResource(R.string.sample_data),
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.weight(1f))
        AboutButton(onClick = onAboutClick)
    }
}

/** Circular ⓘ in the top-right that opens the About screen (licenses + contributor thanks). */
@Composable
private fun AboutButton(onClick: () -> Unit) {
    val aboutLabel = stringResource(R.string.about)
    Surface(
        onClick = onClick,
        modifier =
            Modifier
                .size(TbaIconButtonSize)
                .semantics { contentDescription = aboutLabel },
        shape = ClickableSurfaceDefaults.shape(CircleShape),
        colors =
            ClickableSurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                focusedContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        // This button hugs the top of the (vertically-clipping) header, so a focus scale or glow
        // would bloom past the clip line and shear off. A border is painted inside the bounds, so
        // it reads as focus without clipping — the same bright-border language as the feed cards.
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
        border =
            ClickableSurfaceDefaults.border(
                focusedBorder =
                    Border(
                        border =
                            BorderStroke(
                                TbaFocusBorderWidth,
                                MaterialTheme.colorScheme.secondary,
                            ),
                        shape = CircleShape,
                    ),
            ),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                modifier = Modifier.size(TbaIconSize),
            )
        }
    }
}

/**
 * Pins the header above the non-scrolling states (loading / error / empty). The feed embeds its own
 * header as the first list item so it scrolls away with the content.
 */
@Composable
private fun WithFixedHeader(
    usingMockData: Boolean,
    onAboutClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    // These states don't scroll, so the overscan-safe top margin lives here as outer padding.
    // (The feed carries the same inset as LazyColumn contentPadding so it can scroll full-bleed.)
    Column(Modifier.fillMaxSize().padding(top = TbaOverscanTopPadding)) {
        Header(
            usingMockData = usingMockData,
            onAboutClick = onAboutClick,
            modifier = Modifier.padding(horizontal = TbaScreenHPadding),
        )
        Spacer(Modifier.height(20.dp))
        content()
    }
}

@Composable
private fun EventFeedContent(
    feed: EventFeed,
    usingMockData: Boolean,
    onAboutClick: () -> Unit,
    onEventClick: (Event) -> Unit,
) {
    val rows = feed.rowSections()
    // Initial focus lands on the very first card exactly once. The hoisted guard lives here (not on
    // the card) so recycling cards as the user scrolls never re-fires the request — which would snap
    // focus and scroll back to the top of the feed.
    val initialFocusDone = remember { mutableStateOf(false) }

    // Pivot focused cards/rows inward so D-pad focus never sits flush against the viewport
    // edge — there's always a peek of the neighbour (canonical TvMaterialCatalog pattern).
    // 0.3 keeps the scrolling header visible when the top card is focused on launch (it clamps
    // at the start), while focusing a lower row pivots it up and slides the header off-screen.
    PositionFocusedItemInLazyLayout(parentFraction = 0.3f) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(28.dp),
            // The top inset is contentPadding (not outer padding) so the list is full-bleed: cards
            // scroll all the way to the screen's top edge instead of clipping at a line with a dead
            // band above. The top margin is the TV overscan-safe inset for the resting first row.
            contentPadding =
                PaddingValues(
                    top = TbaOverscanTopPadding,
                    bottom = TbaListBottomPadding,
                ),
        ) {
            // Header rides along as item 0 so it scrolls away as the user moves down the feed.
            item(key = "header") {
                Header(
                    usingMockData = usingMockData,
                    onAboutClick = onAboutClick,
                    modifier = Modifier.padding(horizontal = TbaScreenHPadding),
                )
            }
            itemsIndexed(rows, key = { _, s -> s.titleRes }) { sIndex, section ->
                Column {
                    Text(
                        stringResource(section.titleRes),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = TbaScreenHPadding),
                    )
                    Spacer(Modifier.height(8.dp))
                    // Content padding gives the focus-scale "bloom" room so it isn't clipped by
                    // the row viewport. focusRestorer returns focus to the last-focused card when
                    // you move up/down to another row and come back.
                    LazyRow(
                        modifier = Modifier.focusRestorer(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        contentPadding =
                            PaddingValues(
                                horizontal = TbaScreenHPadding,
                                vertical = 12.dp,
                            ),
                    ) {
                        itemsIndexed(section.events, key = { _, e -> e.key }) { eIndex, event ->
                            EventCard(
                                event = event,
                                onClick = { onEventClick(event) },
                                modifier =
                                    if (sIndex == 0 && eIndex == 0) {
                                        Modifier.focusOnInitialVisibility(initialFocusDone)
                                    } else {
                                        Modifier
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val platforms =
        event.webcasts
            .map { it.type.label }
            .distinct()
            .joinToString(" and ")
    val description =
        buildString {
            append(event.displayName)
            event.location?.let { append(", ").append(it) }
            append(", ").append(event.dateRangeLabel())
            if (platforms.isNotBlank()) append(", on ").append(platforms)
        }
    Surface(
        onClick = onClick,
        modifier =
            modifier
                .width(320.dp)
                .height(180.dp) // 16:9 — the canonical Android TV ratio
                .semantics(mergeDescendants = true) { contentDescription = description },
        shape = ClickableSurfaceDefaults.shape(TbaCardShape),
        colors =
            ClickableSurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.08f),
        glow =
            ClickableSurfaceDefaults.glow(
                focusedGlow =
                    Glow(
                        elevationColor = MaterialTheme.colorScheme.secondary,
                        elevation = 10.dp,
                    ),
            ),
        border =
            ClickableSurfaceDefaults.border(
                focusedBorder =
                    Border(
                        border =
                            BorderStroke(
                                TbaFocusBorderWidth,
                                MaterialTheme.colorScheme.secondary,
                            ),
                        shape = TbaCardShape,
                    ),
            ),
    ) {
        Column(Modifier.fillMaxSize().padding(18.dp)) {
            Text(
                event.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                event.webcasts.map { it.type }.distinct().take(2).forEach { type ->
                    PlatformDot(type)
                    Spacer(Modifier.width(6.dp))
                }
            }
            Spacer(Modifier.weight(1f))
            event.location?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                event.dateRangeLabel(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PlatformDot(type: WebcastType) {
    val (background, content) =
        when (type) {
            WebcastType.YOUTUBE -> YouTubeRed.copy(alpha = 0.20f) to YouTubeOnChip
            WebcastType.TWITCH -> TwitchPurple.copy(alpha = 0.20f) to TwitchOnChip
            WebcastType.OTHER ->
                MaterialTheme.colorScheme.surfaceVariant to
                    MaterialTheme.colorScheme.onSurfaceVariant
        }
    Chip(type.label, background, content)
}

@Composable
private fun Chip(
    text: String,
    background: Color,
    content: Color,
) {
    Box(
        Modifier
            .background(background, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = content,
        )
    }
}

@Composable
private fun CenteredMessage(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

/** Shimmering placeholder that mirrors the feed layout while events load (no mobile-material spinner). */
@Composable
private fun LoadingSkeleton() {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "skeleton-alpha",
    )
    val shimmer = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        repeat(2) {
            Column {
                Box(
                    Modifier
                        .padding(start = TbaScreenHPadding)
                        .height(22.dp)
                        .width(160.dp)
                        .background(shimmer, RoundedCornerShape(6.dp)),
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.padding(horizontal = TbaScreenHPadding, vertical = 12.dp),
                ) {
                    repeat(4) {
                        Box(
                            Modifier
                                .width(320.dp)
                                .height(180.dp)
                                .background(shimmer, TbaCardShape),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContent() {
    StatusMessage(
        title = stringResource(R.string.empty_title),
        message = stringResource(R.string.empty_message),
    )
}

@Composable
private fun ErrorContent(
    @StringRes messageRes: Int,
    onRetry: () -> Unit,
) {
    StatusMessage(
        title = stringResource(R.string.events_error_title),
        message = stringResource(messageRes),
    ) {
        Spacer(Modifier.height(20.dp))
        RetryButton(onRetry = onRetry, modifier = Modifier.requestFocusOnFirstGainingVisibility())
    }
}

@Composable
private fun WebcastPicker(
    event: Event,
    onPick: (Webcast) -> Unit,
    onDismiss: () -> Unit,
) {
    val firstButton = remember { FocusRequester() }
    // Bias initial focus to today's stream when a cast is split by day; otherwise the first option.
    val today = remember { LocalDate.now() }
    val focusIndex = event.webcasts.indexOfFirst { it.date == today }.coerceAtLeast(0)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.width(460.dp),
            shape = RoundedCornerShape(18.dp),
            colors =
                SurfaceDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            border =
                Border(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(18.dp),
                ),
        ) {
            Column(Modifier.padding(28.dp)) {
                Text(
                    event.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.choose_webcast),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(20.dp))
                event.webcasts.forEachIndexed { index, webcast ->
                    Button(
                        onClick = { onPick(webcast) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .ifElse(index == focusIndex, Modifier.focusRequester(firstButton)),
                        colors =
                            ButtonDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.secondary,
                                focusedContentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                    ) {
                        Box(
                            Modifier
                                .size(
                                    10.dp,
                                ).background(platformColor(webcast.type), CircleShape),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(webcast.pickerLabel())
                    }
                    if (index != event.webcasts.lastIndex) Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
    androidx.compose.runtime.LaunchedEffect(
        event.key,
    ) { runCatching { firstButton.requestFocus() } }
}

// Platform-identity colours for the webcast dot: brand constants, not theme tokens. OTHER falls
// back to our accent blue.
private fun platformColor(type: WebcastType): Color =
    when (type) {
        WebcastType.YOUTUBE -> YouTubeRed
        WebcastType.TWITCH -> TwitchPurple
        WebcastType.OTHER -> TbaBlueBright
    }
