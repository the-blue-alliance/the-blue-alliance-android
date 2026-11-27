package com.thebluealliance.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Set while a screen draws as a detail pane; [onClose] clears the selection. */
@Immutable
class DetailPaneChrome(
    val onClose: () -> Unit,
)

/** Non-null while a screen is a detail pane rather than full screen. */
val LocalDetailPaneChrome = compositionLocalOf<DetailPaneChrome?> { null }

/**
 * Set while an event's Matches tab is the list pane of a two-pane layout: [detail] draws the
 * selected match, null while nothing is selected.
 */
@Immutable
class MatchDetailPaneHost(
    val selectedMatchKey: String?,
    val onClearSelection: () -> Unit,
    val detail: (@Composable () -> Unit)?,
)

val LocalMatchDetailPaneHost = compositionLocalOf<MatchDetailPaneHost?> { null }

/** Narrowest a match list or match detail pane gets: the compact phone width both are built for. */
internal val MatchPaneMinWidth = 360.dp
private val MatchListPaneMaxWidth = 400.dp

/** List pane width in [available]: 40%, held to 360–400dp so rows keep their phone layout. */
internal fun matchListPaneWidth(available: Dp): Dp =
    (available * 0.4f).coerceIn(MatchPaneMinWidth, MatchListPaneMaxWidth)

/** Detail pane width in [available]: what the list pane and the divider between them leave. */
internal fun matchDetailPaneWidth(available: Dp): Dp =
    available - matchListPaneWidth(available) - DividerDefaults.Thickness

/** Lays [list] out beside the selected match, or a placeholder until one is picked. */
@Composable
fun MatchListDetailPanes(
    host: MatchDetailPaneHost,
    list: @Composable () -> Unit,
) {
    // Cover the sheet and margins the event pager paints for one centred column, which would
    // otherwise cut across the list. The list pane is narrower than that column, so it takes the
    // plain page colour as on a phone; a match in the detail pane paints its own page.
    BoxWithConstraints(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val listWidth = matchListPaneWidth(maxWidth)
        Row(Modifier.fillMaxSize()) {
            // Each pane is its own focus region, so Tab finishes the list before the match.
            Box(Modifier.width(listWidth).fillMaxHeight().focusRegion()) { list() }
            VerticalDivider()
            Box(Modifier.weight(1f).fillMaxHeight().focusRegion()) {
                host.detail?.invoke() ?: MatchDetailPlaceholder()
            }
        }
    }
}

@Composable
private fun MatchDetailPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Select a match",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Its result, teams, videos and score breakdown show here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
