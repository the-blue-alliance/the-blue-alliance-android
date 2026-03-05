package com.thebluealliance.android.ui.events.detail.tabs

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Webcast
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.util.openUrl
import com.thebluealliance.android.ui.components.formatEventDateRange
import java.time.LocalDate

@Composable
fun EventInfoTab(
    event: Event?,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (event == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }

    // Optimization: Calculate today's YouTube webcasts outside the LazyColumn
    val todayYouTubeWebcasts = remember(event.webcasts) {
        val today = LocalDate.now().toString()
        event.webcasts.filter { it.type == "youtube" && it.date == today }
    }

    // Optimization: Sort all webcasts chronologically by date
    val sortedWebcasts = remember(event.webcasts) {
        event.webcasts.sortedBy { it.date ?: "" }
    }

    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = innerPadding,
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
                            val url = event.gmapsUrl ?: "geo:0,0?q=${Uri.encode(event.address)}"
                            context.openUrl(url)
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
                            context.openUrl(event.website)
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
        if (sortedWebcasts.isNotEmpty()) {
            item {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Webcasts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(sortedWebcasts, key = { "${it.type}_${it.channel}_${it.date ?: ""}" }) { webcast ->
                val url = webcastUrl(webcast)
                val label = webcastLabel(webcast)
                if (url != null) {
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable {
                                context.openUrl(url)
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

            // Embedded YouTube players if there are matching webcasts for today
            if (todayYouTubeWebcasts.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Today's Webcast",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                items(todayYouTubeWebcasts, key = { "preview_${it.channel}" }) { webcast ->
                    Spacer(Modifier.height(8.dp))
                    YouTubeEmbed(videoId = webcast.channel)
                }
            }
        }
    }
}

@Composable
private fun YouTubeEmbed(videoId: String, modifier: Modifier = Modifier) {
    var showPlayer by rememberSaveable(videoId) { mutableStateOf(false) }
    var customView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }

    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    val thumbnailUrl = remember(videoId) { "https://img.youtube.com/vi/$videoId/hqdefault.jpg" }

    // Fullscreen handling
    if (customView != null) {
        BackHandler {
            customViewCallback?.onCustomViewHidden()
        }

        DisposableEffect(customView) {
            val window = activity?.window
            val decorView = window?.decorView as? ViewGroup
            val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            val view = customView!!
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            view.setBackgroundColor(android.graphics.Color.BLACK)
            decorView?.addView(view)

            onDispose {
                decorView?.removeView(view)
                activity?.requestedOrientation = originalOrientation
                if (window != null) {
                    val controller = WindowCompat.getInsetsController(window, window.decorView)
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(MaterialTheme.shapes.medium)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (showPlayer) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webViewClient = WebViewClient()
                        webChromeClient = object : WebChromeClient() {
                            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                customView = view
                                customViewCallback = callback
                            }

                            override fun onHideCustomView() {
                                customView = null
                                customViewCallback = null
                            }
                        }

                        // Error 153 is often fixed by providing a valid Referer and origin
                        val headers = mapOf("Referer" to "https://www.thebluealliance.com")
                        loadUrl("https://www.youtube.com/embed/$videoId?autoplay=1&origin=https://www.thebluealliance.com", headers)
                    }
                },
                onRelease = { webView ->
                    webView.destroy()
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder/Thumbnail
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "YouTube Thumbnail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Play button overlay
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .clickable { showPlayer = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.PlayCircle,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
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
        val date = LocalDate.parse(dateStr)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("(EEE, MMM d)", java.util.Locale.US)
        date.format(formatter)
    } catch (_: Exception) {
        ""
    }
}
