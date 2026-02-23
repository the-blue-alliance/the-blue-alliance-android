package com.thebluealliance.android.ui.events.detail.tabs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Webcast
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.formatEventDateRange

@Composable
fun EventInfoTab(event: Event?) {
    if (event == null) {
        LoadingBox()
        return
    }
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Text(event.name, style = MaterialTheme.typography.headlineSmall)
        }
        val location = listOfNotNull(event.city, event.state, event.country).joinToString(", ")
        if (location.isNotEmpty()) {
            item {
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        val dateRange = formatEventDateRange(event.startDate, event.endDate)
        if (dateRange != null) {
            item {
                Text(
                    text = dateRange,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        if (event.week != null) {
            item {
                Text(
                    text = "Week ${event.week}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        if (event.district != null) {
            item {
                Text(
                    text = "District: ${event.district}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        if (event.locationName != null) {
            item {
                Text(
                    text = event.locationName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Address (tappable → opens Google Maps)
        if (event.address != null) {
            item {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable {
                            val intent = if (event.gmapsUrl != null) {
                                Intent(Intent.ACTION_VIEW, Uri.parse(event.gmapsUrl))
                            } else {
                                Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(event.address)}"))
                            }
                            context.startActivity(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = event.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // Website (clickable)
        if (event.website != null) {
            item {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(event.website)))
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Outlined.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = event.website,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // Webcasts
        if (event.webcasts.isNotEmpty()) {
            item {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Webcasts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(event.webcasts, key = { "${it.type}_${it.channel}" }) { webcast ->
                val url = webcastUrl(webcast)
                val label = webcastLabel(webcast)
                if (url != null) {
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Outlined.PlayCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

private fun webcastUrl(webcast: Webcast): String? = when (webcast.type) {
    "twitch" -> "https://twitch.tv/${webcast.channel}"
    "youtube" -> "https://youtube.com/watch?v=${webcast.channel}"
    "livestream" -> "https://livestream.com/accounts/${webcast.channel}/events/${webcast.file ?: ""}"
    else -> null
}

private fun webcastLabel(webcast: Webcast): String {
    val base = when (webcast.type) {
        "twitch" -> "Watch on Twitch"
        "youtube" -> "Watch on YouTube"
        "livestream" -> "Watch on Livestream"
        else -> "Watch (${webcast.type})"
    }
    val dateSuffix = webcast.date?.let { formatWebcastDate(it) } ?: ""
    return if (dateSuffix.isNotEmpty()) "$base $dateSuffix" else base
}

private fun formatWebcastDate(dateStr: String): String {
    return try {
        val date = java.time.LocalDate.parse(dateStr)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("(EEE, MMM d)", java.util.Locale.US)
        date.format(formatter)
    } catch (_: Exception) {
        ""
    }
}

