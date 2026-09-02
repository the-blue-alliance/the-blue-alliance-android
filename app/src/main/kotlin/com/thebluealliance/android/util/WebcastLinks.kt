package com.thebluealliance.android.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.thebluealliance.android.domain.model.Webcast

/** The web URL for a webcast, or `null` for types we don't know how to link out to. */
fun webcastUrl(webcast: Webcast): String? =
    when (webcast.type) {
        "twitch" -> "https://twitch.tv/${webcast.channel}"
        "youtube" -> "https://youtube.com/watch?v=${webcast.channel}"
        "livestream" ->
            "https://livestream.com/accounts/${webcast.channel}/events/${webcast.file ?: ""}"
        else -> null
    }

/**
 * A native-app deep link for a webcast, or `null` when we have none for the type.
 *
 * Twitch only, for now. Handing the browser `https://twitch.tv/<channel>` shows an "open in the
 * Twitch app" interstitial that drops the channel path and lands the user on the Twitch home
 * screen — verified on a Quest 3, and the same interstitial runs on phones with Twitch installed.
 * `twitch://stream/<channel>` opens the app straight to the channel.
 */
fun webcastAppUri(webcast: Webcast): String? =
    when {
        webcast.type == "twitch" && webcast.channel.isNotBlank() ->
            "twitch://stream/${webcast.channel}"
        else -> null
    }

/**
 * Opens a webcast, preferring its native app (see [webcastAppUri]) and falling back to the web URL
 * when no app handles the deep link.
 *
 * `startActivity` doesn't require package visibility, so catching [ActivityNotFoundException] is
 * enough here — no `<queries>` entry needed.
 */
fun Context.openWebcast(
    webcast: Webcast,
    url: String,
) {
    val appUri = webcastAppUri(webcast)
    if (appUri != null) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, appUri.toUri()))
            return
        } catch (_: ActivityNotFoundException) {
            // No native app installed — fall through to the browser.
        }
    }
    openUrl(url)
}
