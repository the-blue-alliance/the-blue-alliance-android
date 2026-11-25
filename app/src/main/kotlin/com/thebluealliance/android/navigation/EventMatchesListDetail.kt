package com.thebluealliance.android.navigation

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import com.thebluealliance.android.ui.components.DetailPaneChrome
import com.thebluealliance.android.ui.components.LocalDetailPaneChrome
import com.thebluealliance.android.ui.components.LocalMatchDetailPaneHost
import com.thebluealliance.android.ui.components.MatchDetailPaneHost

/** Entry metadata key: an event whose Matches tab can hold a match detail pane. Value: event key. */
internal const val EVENT_MATCHES_LIST_PANE = "tba.eventMatchesListPane"

/** Entry metadata key: a match that can show in its event's detail pane. Value: match key. */
internal const val MATCH_DETAIL_PANE = "tba.matchDetailPane"

fun eventMatchesListPane(eventKey: String): Map<String, Any> =
    mapOf(
        EVENT_MATCHES_LIST_PANE to eventKey,
    )

fun matchDetailPane(matchKey: String): Map<String, Any> = mapOf(MATCH_DETAIL_PANE to matchKey)

/**
 * Two panes from the expanded width class (840dp): the first breakpoint where, beside the rail,
 * the list and the match each get at least the phone width they are built for. An 800dp tablet
 * in portrait would leave the match 359dp, so medium widths keep one pane.
 */
fun showsEventMatchesTwoPane(windowSizeClass: WindowSizeClass): Boolean =
    windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

/** Which back stack entries make up the event-matches scene; [matchKey] null means no selection. */
internal data class EventMatchesPanes(
    val eventIndex: Int,
    val matchKey: String?,
)

/**
 * Finds the event-matches scene in a back stack's entry [metadata]: an event on top, or a match
 * of that same event on top of it. A match opened from anywhere else stays full screen.
 */
internal fun eventMatchesPanes(metadata: List<Map<String, Any>>): EventMatchesPanes? {
    val top = metadata.lastOrNull() ?: return null
    if (top[EVENT_MATCHES_LIST_PANE] is String) return EventMatchesPanes(metadata.lastIndex, null)
    val matchKey = top[MATCH_DETAIL_PANE] as? String ?: return null
    val eventIndex = metadata.lastIndex - 1
    val eventKey = metadata.getOrNull(eventIndex)?.get(EVENT_MATCHES_LIST_PANE) as? String
    return if (eventKey != null && matchKey.substringBefore('_') == eventKey) {
        EventMatchesPanes(eventIndex, matchKey)
    } else {
        null
    }
}

/**
 * Opens [route] in the detail pane: replaces the selected match rather than stacking matches, so
 * back clears the selection instead of walking through every match viewed.
 */
internal fun MutableList<NavKey>.selectMatch(route: Screen.MatchDetail) {
    when (lastOrNull()) {
        route -> Unit
        is Screen.MatchDetail -> set(lastIndex, route)
        else -> add(route)
    }
}

/**
 * An event with its Matches tab as the list pane. The event entry draws the panes itself, since the
 * list is one tab of a tabbed hub; this scene hands it the match entry through
 * [LocalMatchDetailPaneHost]. Its key is the event's alone, so selecting, changing or clearing the
 * match recomposes in place instead of sliding the whole event.
 */
internal data class EventMatchesScene<T : Any>(
    override val previousEntries: List<NavEntry<T>>,
    private val eventEntry: NavEntry<T>,
    private val matchEntry: NavEntry<T>?,
    private val matchKey: String?,
    private val onClearSelection: () -> Unit,
) : Scene<T> {
    override val key: Any = EventMatchesScene::class to eventEntry.contentKey
    override val entries: List<NavEntry<T>> = listOfNotNull(eventEntry, matchEntry)
    override val content: @Composable () -> Unit = {
        val chrome = remember(onClearSelection) { DetailPaneChrome(onClose = onClearSelection) }
        val host =
            MatchDetailPaneHost(
                selectedMatchKey = matchKey,
                onClearSelection = onClearSelection,
                detail =
                    matchEntry?.let { entry ->
                        {
                            CompositionLocalProvider(LocalDetailPaneChrome provides chrome) {
                                entry.Content()
                            }
                        }
                    },
            )
        CompositionLocalProvider(LocalMatchDetailPaneHost provides host) { eventEntry.Content() }
    }
}

/** Puts an event's Matches tab and the selected match side by side on expanded-width windows. */
internal class EventMatchesSceneStrategy<T : Any>(
    private val twoPane: Boolean,
) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!twoPane) return null
        val panes = eventMatchesPanes(entries.map { it.metadata }) ?: return null
        return EventMatchesScene(
            previousEntries = entries.dropLast(1),
            eventEntry = entries[panes.eventIndex],
            matchEntry = if (panes.matchKey != null) entries.last() else null,
            matchKey = panes.matchKey,
            onClearSelection = onBack,
        )
    }
}

@Composable
internal fun <T : Any> rememberEventMatchesSceneStrategy(): EventMatchesSceneStrategy<T> {
    val twoPane = showsEventMatchesTwoPane(currentWindowAdaptiveInfo().windowSizeClass)
    return remember(twoPane) { EventMatchesSceneStrategy(twoPane) }
}
