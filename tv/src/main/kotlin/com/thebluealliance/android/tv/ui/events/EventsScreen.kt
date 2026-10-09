@file:OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.thebluealliance.android.tv.ui.events

import androidx.annotation.StringRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.thebluealliance.android.tv.R
import com.thebluealliance.android.tv.data.deeplink.WebcastLauncher
import com.thebluealliance.android.tv.data.model.Event
import com.thebluealliance.android.tv.data.model.EventFeed
import com.thebluealliance.android.tv.data.model.RowTitle
import com.thebluealliance.android.tv.data.model.Webcast
import com.thebluealliance.android.tv.data.model.WebcastType
import com.thebluealliance.android.tv.ui.common.PositionFocusedItemInLazyLayout
import com.thebluealliance.android.tv.ui.common.RetryButton
import com.thebluealliance.android.tv.ui.common.StatusMessage
import com.thebluealliance.android.tv.ui.common.focusOnInitialVisibility
import com.thebluealliance.android.tv.ui.common.ifElse
import com.thebluealliance.android.tv.ui.common.requestFocusOnFirstGainingVisibility
import com.thebluealliance.android.tv.ui.theme.TbaArtGradientEnd
import com.thebluealliance.android.tv.ui.theme.TbaArtGradientStart
import com.thebluealliance.android.tv.ui.theme.TbaArtTitleStyle
import com.thebluealliance.android.tv.ui.theme.TbaArtWatermark
import com.thebluealliance.android.tv.ui.theme.TbaBlueBright
import com.thebluealliance.android.tv.ui.theme.TbaCardArtAspectRatio
import com.thebluealliance.android.tv.ui.theme.TbaCardFocusBorderWidth
import com.thebluealliance.android.tv.ui.theme.TbaCardFocusRing
import com.thebluealliance.android.tv.ui.theme.TbaCardShape
import com.thebluealliance.android.tv.ui.theme.TbaCardSpacing
import com.thebluealliance.android.tv.ui.theme.TbaCardTextGap
import com.thebluealliance.android.tv.ui.theme.TbaCardWidth
import com.thebluealliance.android.tv.ui.theme.TbaFocusBorderWidth
import com.thebluealliance.android.tv.ui.theme.TbaIconButtonSize
import com.thebluealliance.android.tv.ui.theme.TbaIconSize
import com.thebluealliance.android.tv.ui.theme.TbaListBottomPadding
import com.thebluealliance.android.tv.ui.theme.TbaOverscanTopPadding
import com.thebluealliance.android.tv.ui.theme.TbaScreenHPadding
import com.thebluealliance.android.tv.ui.theme.TwitchPurple
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

@Composable
private fun RowTitle.text(): AnnotatedString {
    // Today's group rows lead with what the row is ("FIRST In Michigan") and demote the shared
    // "Happening Now" marker to a quieter suffix, so a stack of today rows scans by group name.
    val group =
        when (this) {
            RowTitle.HappeningNow -> stringResource(R.string.section_happening_now)
            RowTitle.Upcoming -> stringResource(R.string.section_upcoming)
            RowTitle.Recent -> stringResource(R.string.section_recent)
            RowTitle.Championship -> stringResource(R.string.group_championship)
            is RowTitle.District -> displayName
            RowTitle.Regionals -> stringResource(R.string.group_regionals)
            RowTitle.MoreEvents -> stringResource(R.string.group_more_events)
            RowTitle.Offseason -> stringResource(R.string.group_offseason)
        }
    val isTodayGroup =
        this != RowTitle.HappeningNow && this != RowTitle.Upcoming && this != RowTitle.Recent
    if (!isTodayGroup) return AnnotatedString(group)
    val suffix = stringResource(R.string.section_happening_now)
    val suffixStyle =
        SpanStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal,
        )
    return buildAnnotatedString {
        append(group)
        withStyle(suffixStyle) { append("  ·  $suffix") }
    }
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
    val rows = remember(feed) { feed.rows() }
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
            itemsIndexed(rows, key = { _, row -> row.key }) { rowIndex, row ->
                // Extra air above the first post-today row closes off the today block without a rule.
                Column(Modifier.ifElse(row.startsAfterToday, Modifier.padding(top = 16.dp))) {
                    Text(
                        row.title.text(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier =
                            Modifier
                                .padding(start = TbaScreenHPadding)
                                .semantics { heading() },
                    )
                    Spacer(Modifier.height(12.dp))
                    // Content padding gives the focus-scale "bloom" room so it isn't clipped by
                    // the row viewport. focusRestorer returns focus to the last-focused card when
                    // you move up/down to another row and come back.
                    LazyRow(
                        modifier = Modifier.focusRestorer(),
                        horizontalArrangement = Arrangement.spacedBy(TbaCardSpacing),
                        contentPadding =
                            PaddingValues(
                                horizontal = TbaScreenHPadding,
                                vertical = 12.dp,
                            ),
                    ) {
                        itemsIndexed(row.events, key = { _, e -> e.key }) { eIndex, event ->
                            EventCard(
                                event = event,
                                onClick = { onEventClick(event) },
                                modifier =
                                    if (rowIndex == 0 && eIndex == 0) {
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

// The art tile grows by this much on each side when focused; the text block below slides down by
// the same amount so the bloomed tile never crowds its own title.
private val CardFocusedScale = 1.08f
private val CardFocusBloom = TbaCardWidth / TbaCardArtAspectRatio * (CardFocusedScale - 1f) / 2f

// Same art for every event (no per-district/type colour — product decision): a lit TBA-blue corner
// fading diagonally (top-left to bottom-right) to near the page background.
private val PlaceholderBrush = Brush.linearGradient(listOf(TbaArtGradientStart, TbaArtGradientEnd))

// Bottom scrim, drawn on the placeholder too so badges/progress layered over the lower art later
// look identical whether or not a stream thumbnail has loaded.
private val ArtScrimFraction = 0.45f
private val ArtScrimBrush =
    Brush.verticalGradient(
        listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)),
    )

private val WatermarkAlpha = 0.10f
private val LampAspectRatio = 72f / 112f // ic_tba_lamp's viewport

/** Android TV "standard card": a focusable 16:9 art tile with a left-aligned text block below it. */
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
            append(event.name)
            event.location?.let { append(", ").append(it) }
            append(", ").append(event.dateRangeLabel())
            if (platforms.isNotBlank()) append(", on ").append(platforms)
        }
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val textShift by animateDpAsState(
        targetValue = if (focused) CardFocusBloom else 0.dp,
        label = "card-text-shift",
    )
    // Cards in a row stay the same height whether a title takes one line or two, and whether or not
    // the card is focused. The reservation goes on the whole card (not the title) so spare space falls
    // below the subtitle instead of between title and subtitle. Sized from the styles CardContent
    // gives the slots, plus the focus slide.
    val typography = MaterialTheme.typography
    val minHeight =
        with(LocalDensity.current) {
            TbaCardWidth / TbaCardArtAspectRatio + TbaCardTextGap + CardFocusBloom +
                (typography.titleMedium.lineHeight * 2).toDp() +
                typography.bodySmall.lineHeight.toDp()
        }
    // The slot Texts fill the width because StandardCardContainer centres narrower text under the art.
    // They're cleared from semantics: the card's contentDescription already says all of this.
    val slotModifier = Modifier.fillMaxWidth().clearAndSetSemantics {}
    // StandardCardContainer scales and borders only the art; the text below stays crisp at 1x. Text
    // styles, colours and the subtitle's 0.6 alpha are the library's CardContent defaults.
    StandardCardContainer(
        modifier = modifier.width(TbaCardWidth).heightIn(min = minHeight),
        interactionSource = interactionSource,
        imageCard = { source ->
            Card(
                onClick = onClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(TbaCardArtAspectRatio)
                        .semantics { contentDescription = description },
                interactionSource = source,
                scale = CardDefaults.scale(focusedScale = CardFocusedScale),
                border =
                    CardDefaults.border(
                        focusedBorder =
                            Border(
                                border = BorderStroke(TbaCardFocusBorderWidth, TbaCardFocusRing),
                                shape = TbaCardShape,
                            ),
                    ),
            ) {
                EventArt(event)
            }
        },
        // The full official name, not the short name already on the art: it adds information and
        // still identifies the event once a stream thumbnail covers the art.
        title = {
            Text(
                event.name,
                // The text slides down by the art's focus bloom as real layout (not an offset), so it
                // stays inside the card's reserved height instead of being clipped below it.
                modifier = slotModifier.padding(top = TbaCardTextGap + textShift),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = {
            Text(
                listOfNotNull(event.location, event.dateRangeLabel()).joinToString(" · "),
                modifier = slotModifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}

/**
 * The card's 16:9 art. Today it's always the branded placeholder; stream thumbnails will layer over
 * it (between the placeholder and the scrim) so a failed load still leaves a finished tile.
 */
@Composable
private fun EventArt(
    event: Event,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(PlaceholderBrush)
            .clearAndSetSemantics {},
    ) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(ArtScrimFraction)
                .background(ArtScrimBrush),
        )
        // Brand-tinted lamp watermark, just kissing the bottom-right edge (clipped by the card shape).
        Image(
            painter = painterResource(R.drawable.ic_tba_lamp),
            contentDescription = null,
            alpha = WatermarkAlpha,
            colorFilter = ColorFilter.tint(TbaArtWatermark),
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 12.dp, y = 8.dp)
                    .height(96.dp)
                    .aspectRatio(LampAspectRatio),
        )
        // The short name as the art's "poster" text. Vertically centred: two 30sp lines start ~41dp
        // from the top, clear of the top-left date badge (10–34dp) and the top-right platform label.
        // Shrinks rather than ellipsizing so long names stay whole. Division names break at their
        // " - " so the second line reads "Apollo", not "- Apollo".
        BasicText(
            text = event.displayName.replace(" - ", "\n"),
            style = TbaArtTitleStyle.copy(color = Color.White.copy(alpha = 0.7f)),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            autoSize =
                TextAutoSize.StepBased(
                    minFontSize = 22.sp,
                    maxFontSize = TbaArtTitleStyle.fontSize,
                    stepSize = 1.sp,
                ),
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        )
        // Monochrome on purpose: brand-coloured chips were the loudest thing on screen.
        if (event.streamPlatforms.isNotEmpty()) {
            Text(
                event.streamPlatforms.joinToString(" · ") { it.label },
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 10.dp),
            )
        }
    }
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
    val barShape = RoundedCornerShape(4.dp)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        repeat(2) {
            Column {
                Box(
                    Modifier
                        .padding(start = TbaScreenHPadding)
                        .height(20.dp)
                        .width(160.dp)
                        .background(shimmer, RoundedCornerShape(6.dp)),
                )
                Spacer(Modifier.height(12.dp))
                // Unbounded so the 4th card runs off the right edge as a peek, like the real row.
                Row(
                    horizontalArrangement = Arrangement.spacedBy(TbaCardSpacing),
                    modifier =
                        Modifier
                            .wrapContentWidth(Alignment.Start, unbounded = true)
                            .padding(horizontal = TbaScreenHPadding, vertical = 12.dp),
                ) {
                    repeat(4) {
                        Column(Modifier.width(TbaCardWidth)) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(TbaCardArtAspectRatio)
                                    .background(shimmer, TbaCardShape),
                            )
                            Spacer(Modifier.height(TbaCardTextGap))
                            Box(Modifier.height(16.dp).width(180.dp).background(shimmer, barShape))
                            Spacer(Modifier.height(8.dp))
                            Box(Modifier.height(14.dp).width(120.dp).background(shimmer, barShape))
                        }
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
