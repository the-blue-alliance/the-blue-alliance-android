package com.thebluealliance.android.tv.data.deeplink

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import com.thebluealliance.android.tv.R
import com.thebluealliance.android.tv.data.model.Webcast
import com.thebluealliance.android.tv.data.model.WebcastType

/**
 * Turns a [Webcast] into the native streaming app on Android TV, trying an ordered
 * fallback chain: pinned TV app -> scheme handler -> Play Store. Never launches a web
 * browser (TV-WB); webcast types with no native TV app show an in-app message instead.
 */
object WebcastLauncher {
    private const val YOUTUBE_TV_PKG = "com.google.android.youtube.tv"
    private const val YOUTUBE_PKG = "com.google.android.youtube"
    private const val TWITCH_PKG = "tv.twitch.android.app"

    fun launch(
        context: Context,
        webcast: Webcast,
    ) {
        val launched = candidatesFor(webcast).any { tryStart(context, it) }
        if (!launched) {
            Toast.makeText(context, unavailableMessage(context, webcast), Toast.LENGTH_LONG).show()
        }
    }

    private fun unavailableMessage(
        context: Context,
        webcast: Webcast,
    ): String =
        when (webcast.type) {
            WebcastType.OTHER -> context.getString(R.string.webcast_unsupported)
            else -> context.getString(R.string.webcast_no_app, webcast.type.label)
        }

    private fun candidatesFor(webcast: Webcast): List<Intent> =
        when (webcast.type) {
            WebcastType.YOUTUBE -> {
                val id = webcast.channel
                val watch = "https://www.youtube.com/watch?v=$id".toUri()
                listOf(
                    view(watch).setPackage(YOUTUBE_TV_PKG),
                    view(watch).setPackage(YOUTUBE_PKG),
                    view("vnd.youtube:$id".toUri()),
                    playStore(YOUTUBE_TV_PKG),
                )
            }
            WebcastType.TWITCH -> {
                val channel = webcast.channel
                val web = "https://www.twitch.tv/$channel".toUri()
                listOf(
                    view("twitch://stream/$channel".toUri()).setPackage(TWITCH_PKG),
                    view(web).setPackage(TWITCH_PKG),
                    playStore(TWITCH_PKG),
                )
            }
            // No native TV app and no browser fallback (TV-WB): surface an in-app message instead.
            WebcastType.OTHER -> emptyList()
        }

    private fun view(uri: Uri): Intent =
        Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    private fun playStore(pkg: String): Intent = view("market://details?id=$pkg".toUri())

    private fun tryStart(
        context: Context,
        intent: Intent,
    ): Boolean {
        if (intent.resolveActivity(context.packageManager) == null) return false
        return try {
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}
