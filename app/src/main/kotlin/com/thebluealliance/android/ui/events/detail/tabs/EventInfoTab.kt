package com.thebluealliance.android.ui.events.detail.tabs

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.R
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Webcast
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.ReadingWidthBox
import com.thebluealliance.android.ui.components.formatEventDateRange
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.events.weekLabel
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.util.openUrl
import com.thebluealliance.android.util.openWebcast
import com.thebluealliance.android.util.webcastUrl

/**
 * The tab's horizontal text margin. It lives on the individual items rather than on the
 * `LazyColumn`, so that [EventInfoLinkRow]'s hover highlight can span the full panel width while
 * its label still lines up with everything else on the tab.
 */
private val CONTENT_INSET = 16.dp

/** The size of each [EventInfoLinkRow] icon. */
private val LINK_ICON_SIZE = 18.dp

@Composable
fun EventInfoTab(
    event: Event?,
    districtDisplayName: String? = null,
    pitLocations: Map<String, String> = emptyMap(),
    favoriteTeamKeys: List<String> = emptyList(),
    innerPadding: PaddingValues = PaddingValues.Zero,
    onNavigateToDistrict: (String) -> Unit = {},
    onNavigateToPitMap: (eventKey: String, highlightedTeamKeys: List<String>) -> Unit = { _, _ -> },
) {
    if (event == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding),
        )
        return
    }
    val context = LocalContext.current
    ReadingWidthBox(innerPadding, Modifier.fillMaxSize()) { readingPadding ->
        LazyColumn(
            // Only the vertical margin is on the list: the horizontal one is applied per item (see
            // CONTENT_INSET) so the link rows can bleed to the panel's edges.
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(vertical = CONTENT_INSET),
            contentPadding = readingPadding,
        ) {
            item {
                Text(
                    event.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = CONTENT_INSET),
                )
            }
            val location = listOfNotNull(event.city, event.state, event.country).joinToString(", ")
            if (location.isNotEmpty()) {
                item {
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = CONTENT_INSET).padding(top = 8.dp),
                    )
                }
            }
            val dateRange = formatEventDateRange(event.startDate, event.endDate)
            if (dateRange != null) {
                item {
                    Text(
                        text = dateRange,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = CONTENT_INSET).padding(top = 8.dp),
                    )
                }
            }
            val week = event.week
            if (week != null) {
                item {
                    Text(
                        text = weekLabel(event.year, week),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = CONTENT_INSET).padding(top = 4.dp),
                    )
                }
            }
            if (event.district != null) {
                item {
                    val districtLabel = districtDisplayName ?: event.district
                    EventInfoLinkRow(
                        text = "District: $districtLabel",
                        onClickLabel = "View district",
                        onClick = { onNavigateToDistrict(event.district) },
                    )
                }
            }

            val venueLines = eventVenueLines(event.locationName, event.address)
            if (venueLines.venue != null) {
                item {
                    Text(
                        text = venueLines.venue,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = CONTENT_INSET).padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Address (tappable → opens Google Maps)
            if (venueLines.address != null) {
                item {
                    EventInfoLinkRow(
                        text = venueLines.address,
                        onClickLabel = "Open in Maps",
                        onClick = {
                            val url =
                                event.gmapsUrl ?: "geo:0,0?q=${Uri.encode(venueLines.address)}"
                            context.openUrl(url)
                        },
                        icon = {
                            Icon(
                                TbaIcons.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(LINK_ICON_SIZE),
                            )
                        },
                    )
                }
            }

            // Website (clickable)
            if (event.website != null) {
                item {
                    EventInfoLinkRow(
                        text = event.website,
                        onClickLabel = "Open in browser",
                        onClick = { context.openUrl(event.website) },
                        icon = {
                            Icon(
                                TbaIcons.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(LINK_ICON_SIZE),
                            )
                        },
                    )
                }
            }

            // Pit Map link (shown when any team has a pit location)
            if (pitLocations.isNotEmpty()) {
                item {
                    EventInfoLinkRow(
                        text = "Pit map",
                        onClickLabel = "View pit map",
                        onClick = { onNavigateToPitMap(event.key, favoriteTeamKeys) },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_nexus_logo),
                                contentDescription = "FRC Nexus",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(LINK_ICON_SIZE),
                            )
                        },
                    )
                }
            }

            // Webcasts
            if (event.webcasts.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Webcasts",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(horizontal = CONTENT_INSET),
                    )
                }
                itemsIndexed(event.webcasts, key = {
                    index,
                    it,
                    ->
                    "${it.type}_${it.channel}_$index"
                }) { _, webcast ->
                    val url = webcastUrl(webcast)
                    val label = webcastLabel(webcast)
                    if (url != null) {
                        EventInfoLinkRow(
                            text = label,
                            onClickLabel = "Watch webcast",
                            onClick = { context.openWebcast(webcast, url) },
                            icon = {
                                Icon(
                                    TbaIcons.PlayCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(LINK_ICON_SIZE),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

/** The venue and address lines the Info tab prints; a null line is not shown. */
internal data class EventVenueLines(
    val venue: String?,
    val address: String?,
)

/**
 * Many events' addresses open with the venue name (most up to 2019, fewer since), so the venue
 * line is dropped when it is the address's first line, ignoring case, surrounding spaces and
 * apostrophes (2003az has "Veteran’s" in one field and "Veterans" in the other). A venue that only
 * starts that line ("Hartford" vs "Hartford, CT") or appears further on is kept. Blank fields, which
 * some older and offseason events have, are not shown.
 */
internal fun eventVenueLines(
    locationName: String?,
    address: String?,
): EventVenueLines {
    val shownAddress = address?.takeIf { it.isNotBlank() }
    val firstAddressLine = shownAddress?.lineSequence()?.first()
    val shownVenue =
        locationName
            ?.takeIf { it.isNotBlank() }
            ?.takeUnless { firstAddressLine != null && venueKey(it) == venueKey(firstAddressLine) }
    return EventVenueLines(venue = shownVenue, address = shownAddress)
}

private const val APOSTROPHES = "'\u2018\u2019"

private fun venueKey(line: String): String = line.filterNot { it in APOSTROPHES }.trim().lowercase()

/**
 * A tappable link row on the Event Info tab.
 *
 * The padding lives *inside* the clickable (and the row fills the available width) so the hover
 * highlight and ripple cover the whole row with a little breathing room, instead of hugging the
 * wrap-content text. The horizontal half of that padding is the tab's own [CONTENT_INSET], which
 * the surrounding `LazyColumn` deliberately does not apply: the highlight therefore runs edge to
 * edge like a list row, while the label still lines up with the plain text above and below it.
 *
 * These rows are primarily informational and there are five of them, so the 4dp of vertical
 * padding keeps each one at about the height it has always had rather than growing it to a 48dp
 * minimum target.
 */
@Composable
private fun EventInfoLinkRow(
    text: String,
    onClickLabel: String,
    onClick: () -> Unit,
    icon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .handCursor()
                .clickable(onClickLabel = onClickLabel, onClick = onClick)
                .padding(horizontal = CONTENT_INSET, vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        val textStyle = MaterialTheme.typography.bodyMedium
        if (icon != null) {
            // Centre the icon on the first line, so it stays there when the text wraps.
            val iconTop =
                with(LocalDensity.current) {
                    ((textStyle.lineHeight.toDp() - LINK_ICON_SIZE) / 2).coerceAtLeast(0.dp)
                }
            Box(Modifier.padding(top = iconTop)) { icon() }
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = textStyle,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

private fun webcastLabel(webcast: Webcast): String {
    val base =
        when (webcast.type) {
            "twitch" -> "Watch on Twitch"
            "youtube" -> "Watch on YouTube"
            "livestream" -> "Watch on Livestream"
            else -> "Watch (${webcast.type})"
        }
    val dateSuffix = webcast.date?.let { formatWebcastDate(it) } ?: ""
    return if (dateSuffix.isNotEmpty()) "$base $dateSuffix" else base
}

private fun formatWebcastDate(dateStr: String): String =
    try {
        val date = java.time.LocalDate.parse(dateStr)
        val formatter =
            java.time.format.DateTimeFormatter.ofPattern(
                "(EEE, MMM d)",
                java.util.Locale.US,
            )
        date.format(formatter)
    } catch (_: Exception) {
        ""
    }
