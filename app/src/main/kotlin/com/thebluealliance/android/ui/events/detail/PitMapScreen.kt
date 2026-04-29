package com.thebluealliance.android.ui.events.detail

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.util.buildNexusPitMapUrl
import com.thebluealliance.android.util.buildTbaPitMapUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

private enum class PitMapAvailability {
    AVAILABLE,
    NOT_FOUND,
    UNKNOWN,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PitMapScreen(
    onNavigateUp: () -> Unit,
    viewModel: PitMapViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pitMapUrl = remember(viewModel.eventKey, viewModel.highlightedTeamKeys) {
        buildTbaPitMapUrl(viewModel.eventKey, viewModel.highlightedTeamKeys)
    }

    val availability by produceState<PitMapAvailability>(initialValue = PitMapAvailability.UNKNOWN, pitMapUrl) {
        value = withContext(Dispatchers.IO) { fetchPitMapAvailability(pitMapUrl) }
    }

    // When TBA has no pit map, fall back to the Nexus map. Nexus event codes are case-insensitive,
    // so the lowercase nexusEventCode is fine. We defer until the event has loaded so we have the
    // correct code (especially important for CMP divisions whose TBA key ≠ FIRST code).
    val nexusPitMapUrl = remember(uiState.nexusEventCode, viewModel.highlightedTeamKeys) {
        buildNexusPitMapUrl(uiState.nexusEventCode, viewModel.highlightedTeamKeys.firstOrNull())
    }

    // CSS selector for the SVG element to center on after load.
    // TBA SVG uses data-team-key on pit <g> elements and data-label-key on division labels.
    // Nexus is a JS SPA — onPageFinished fires before content renders, so skip centering there.
    val tbaCenterSelector = remember(viewModel.eventKey, viewModel.highlightedTeamKeys) {
        if (viewModel.highlightedTeamKeys.isNotEmpty()) {
            "[data-team-key=\"${viewModel.highlightedTeamKeys.first()}\"]"
        } else {
            "[data-label-key=\"${viewModel.eventKey}\"]"
        }
    }

    Scaffold(
        topBar = {
            TBATopAppBar(
                title = {
                    Text(
                        if (uiState.eventTitle.isNotEmpty()) "${uiState.eventTitle} Pit Map"
                        else "Pit Map"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        val showingFallback = availability == PitMapAvailability.NOT_FOUND && uiState.isLoaded
        when {
            availability == PitMapAvailability.UNKNOWN ||
                (availability == PitMapAvailability.NOT_FOUND && !uiState.isLoaded) -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            showingFallback -> {
                PitMapWebView(
                    pitMapUrl = nexusPitMapUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    centerSelector = null, // Nexus is a JS SPA; content renders after onPageFinished
                )
            }
            else -> {
                PitMapWebView(
                    pitMapUrl = pitMapUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    centerSelector = tbaCenterSelector,
                )
            }
        }
    }
}

/**
 * @param pitMapUrl URL to load in the WebView.
 * @param centerSelector Optional CSS selector for the element to center the viewport on after
 *   the page loads. For TBA SVGs this is either `[data-team-key="frcXXX"]` (team pit) or
 *   `[data-label-key="eventKey"]` (division label). Null skips centering.
 */
@Composable
private fun PitMapWebView(
    pitMapUrl: String,
    modifier: Modifier = Modifier,
    centerSelector: String? = null,
) {
    // Array refs so the static WebViewClient can always read the latest values without the
    // AndroidView being recreated (which would flash and reset scroll position).
    val latestSelector = remember { arrayOf(centerSelector) }
    latestSelector[0] = centerSelector
    val webViewRef = remember { arrayOf<WebView?>(null) }
    // Track the current page scale via onScaleChanged (avoids deprecated WebView.scale).
    val currentScale = remember { floatArrayOf(1f) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewRef[0] = this
                // Hide until centering is done to avoid showing the map at the wrong position.
                alpha = 0f

                webViewClient = object : WebViewClient() {
                    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
                        currentScale[0] = newScale
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        val wv = webViewRef[0] ?: run { showWebView(view); return }
                        val selector = latestSelector[0] ?: run { showWebView(wv); return }

                        // Mirror the iOS implementation: wait 400 ms for layout to settle,
                        // then ask JS for the element's document-space centre coordinates,
                        // scale by the current zoom and scroll natively — identical math to
                        // https://github.com/the-blue-alliance/the-blue-alliance-ios/pull/1094
                        wv.postDelayed({
                            // Single-quoted JS string so selector's double-quotes are safe.
                            val js = """
                                (() => {
                                  const el = document.querySelector('$selector');
                                  if (!el) return null;
                                  const rect = el.getBoundingClientRect();
                                  return [
                                    rect.left + window.scrollX + rect.width  / 2,
                                    rect.top  + window.scrollY + rect.height / 2,
                                  ];
                                })()
                            """.trimIndent()
                            wv.evaluateJavascript(js) { result ->
                                try {
                                    if (result != null && result != "null") {
                                        // result arrives as a JSON array string, e.g. "[1234.5,678.9]"
                                        val coords = result.trim('[', ']')
                                            .split(',')
                                            .map { it.trim().toDouble() }
                                        if (coords.size >= 2) {
                                            val zoom = currentScale[0]
                                            val targetX = (coords[0] * zoom - wv.width  / 2)
                                                .toInt().coerceAtLeast(0)
                                            val targetY = (coords[1] * zoom - wv.height / 2)
                                                .toInt().coerceAtLeast(0)
                                            wv.scrollTo(targetX, targetY)
                                        }
                                    }
                                } catch (_: Exception) {
                                    // Ignore parse errors; the map is still usable without centering.
                                } finally {
                                    showWebView(wv)
                                }
                            }
                        }, 400L)
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?,
                    ) {
                        // Silently ignore sub-resource errors; the SVG is self-contained.
                        // Also ensure WebView becomes visible if we never reach a page finish.
                        showWebView(view)
                    }
                }
                settings.apply {
                    // JavaScript is required for evaluateJavascript centering injection.
                    // This WebView only loads trusted TBA and Nexus URLs.
                    @Suppress("SetJavaScriptEnabled")
                    javaScriptEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    builtInZoomControls = true
                    displayZoomControls = false
                    setSupportZoom(true)
                }
                loadUrl(pitMapUrl)
            }
        },
        update = { webView ->
            webViewRef[0] = webView
            if (webView.url != pitMapUrl) {
                webView.alpha = 0f
                webView.loadUrl(pitMapUrl)
            }
        },
        modifier = modifier,
    )
}

/** Fades the WebView in, matching the iOS 150 ms fade from the same PR. */
private fun showWebView(view: WebView?) {
    view?.animate()?.alpha(1f)?.setDuration(150)?.start()
}

private fun fetchPitMapAvailability(url: String): PitMapAvailability {
    return try {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        connection.instanceFollowRedirects = true
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        connection.connect()

        val responseCode = connection.responseCode
        connection.disconnect()

        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            PitMapAvailability.NOT_FOUND
        } else {
            PitMapAvailability.AVAILABLE
        }
    } catch (_: Exception) {
        PitMapAvailability.UNKNOWN
    }
}
