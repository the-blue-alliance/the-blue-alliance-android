package com.thebluealliance.android.ui.events

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Webcast
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.theme.SurfaceFocusRing
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.util.openWebcast
import com.thebluealliance.android.util.webcastUrl
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * [event]'s webcasts that are online now, from [online] (by event key). A webcast with a `date`
 * (YouTube streams carry one per day) only counts on that day in the event's time zone, falling
 * back to the device's. Only webcasts [canOpen] can open are kept, once each.
 */
fun liveWebcastsFor(
    event: Event,
    online: Map<String, List<Webcast>>,
    now: Instant,
    canOpen: (Webcast) -> Boolean = { webcastUrl(it) != null },
): List<Webcast> {
    val webcasts = online[event.key] ?: return emptyList()
    val zone =
        event.timezone?.let { runCatching { ZoneId.of(it) }.getOrNull() } ?: ZoneId.systemDefault()
    val today = now.atZone(zone).toLocalDate()
    return webcasts
        .filter { webcast ->
            val date = webcast.date ?: return@filter true
            runCatching { LocalDate.parse(date) }.getOrNull() == today
        }.filter(canOpen)
        .distinctBy { Triple(it.type, it.channel, it.file) }
}

/** "firstinspires1 (Twitch)": how the picker names a stream. */
fun webcastChannelLabel(webcast: Webcast): String {
    val platform =
        when (webcast.type) {
            "twitch" -> "Twitch"
            "youtube" -> "YouTube"
            "livestream" -> "Livestream"
            else -> webcast.type
        }
    return "${webcast.channel} ($platform)"
}

/**
 * The trailing Watch button on a live event's row. One stream opens straight away; several open a
 * menu of channels. It's its own focus stop and click target, so the row's click still opens the
 * event.
 */
@Composable
fun LiveWebcastButton(
    event: Event,
    webcasts: List<Webcast>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val open = { webcast: Webcast -> webcastUrl(webcast)?.let { context.openWebcast(webcast, it) } }
    Box(modifier) {
        IconButton(
            onClick = {
                if (webcasts.size == 1) open(webcasts.single()) else expanded = true
            },
            modifier = Modifier.handCursor(),
        ) {
            Icon(
                TbaIcons.PlayCircle,
                contentDescription = "Watch ${event.shortName?.takeIf {
                    it.isNotBlank()
                } ?: event.name} live",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SurfaceFocusRing {
                webcasts.forEach { webcast ->
                    DropdownMenuItem(
                        text = { Text(webcastChannelLabel(webcast)) },
                        modifier = Modifier.handCursor(),
                        onClick = {
                            expanded = false
                            open(webcast)
                        },
                    )
                }
            }
        }
    }
}
