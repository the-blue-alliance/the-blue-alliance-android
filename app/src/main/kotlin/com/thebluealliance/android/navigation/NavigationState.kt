package com.thebluealliance.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer

@Composable
fun rememberNavigationState(
    launchBackStack: List<NavKey>,
    isNewTask: Boolean,
    topLevelRoutes: List<NavKey>,
    startTopLevelRoute: NavKey,
): NavigationState {
    val launch =
        remember(launchBackStack, isNewTask, topLevelRoutes, startTopLevelRoute) {
            launchStacks(launchBackStack, isNewTask, topLevelRoutes, startTopLevelRoute)
        }

    val topLevelRoute =
        rememberSerializable(
            launchBackStack,
            topLevelRoutes,
            serializer = MutableStateSerializer(NavKeySerializer()),
        ) {
            mutableStateOf(launch.tab)
        }

    val backStacks =
        topLevelRoutes.associateWith { key ->
            if (key == launch.tab) {
                rememberNavBackStack(*launch.tabStack.toTypedArray())
            } else {
                rememberNavBackStack(key)
            }
        }

    return remember(launch, topLevelRoutes) {
        NavigationState(
            startRoute = launch.homeTab,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
        )
    }
}

/**
 * How a launch seeds the tab back stacks.
 *
 * @property tab the top-level tab shown first.
 * @property tabStack the initial back stack of [tab].
 * @property homeTab the tab that system back returns to from the root of any other tab.
 */
internal data class LaunchStacks(
    val tab: NavKey,
    val tabStack: List<NavKey>,
    val homeTab: NavKey,
)

/**
 * Seeds the tab back stacks from [launchBackStack], the synthetic back stack for the launch
 * destination (its last key) — e.g. `[Events, EventDetail, MatchDetail]` from a deep link, or a
 * lone key from a notification. Its root picks the tab; keys whose root isn't a tab live under
 * [startTopLevelRoute].
 *
 * If [buildFullPath] is true the whole stack is used, so system back walks up the hierarchy and
 * then to [startTopLevelRoute] before leaving the app. Generally speaking, [buildFullPath] is true
 * when the intent has [android.content.Intent.FLAG_ACTIVITY_NEW_TASK]: the Activity is the root of
 * its own Task, so a full synthetic back stack is required for proper, expected back behavior.
 *
 * Otherwise the Activity was started inside the Task of the app that triggered the link, which is
 * assumed to have its own screens to go back to, so only the destination is shown and back from it
 * leaves the app.
 *
 * See https://developer.android.com/guide/navigation/principles#deep-link
 */
internal fun launchStacks(
    launchBackStack: List<NavKey>,
    buildFullPath: Boolean,
    topLevelRoutes: List<NavKey>,
    startTopLevelRoute: NavKey,
): LaunchStacks {
    val root = launchBackStack.first()
    val tab = launchTab(root, topLevelRoutes, startTopLevelRoute)
    if (!buildFullPath) return LaunchStacks(tab, listOf(launchBackStack.last()), homeTab = tab)
    val tabStack = if (tab.isSameTab(root)) launchBackStack else listOf(tab) + launchBackStack
    return LaunchStacks(tab, tabStack, homeTab = startTopLevelRoute)
}

/** The tab a launch back stack rooted at [root] opens in; non-tab roots go to [startTopLevelRoute]. */
internal fun launchTab(
    root: NavKey,
    topLevelRoutes: Collection<NavKey>,
    startTopLevelRoute: NavKey,
): NavKey = topLevelRoutes.firstOrNull { it.isSameTab(root) } ?: startTopLevelRoute

class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    val topLevelRoutes = backStacks.keys
    var topLevelRoute: NavKey by topLevelRoute
    val stacksInUse: List<NavKey>
        get() =
            if (topLevelRoute == startRoute) {
                listOf(startRoute)
            } else {
                listOf(startRoute, topLevelRoute)
            }

    val currentStack: NavBackStack<NavKey>
        get() = backStacks[topLevelRoute] ?: error("Stack for $topLevelRoute not found")
    val currentRoute: NavKey
        get() = currentStack.last()
}

@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries =
        backStacks.mapValues { (_, stack) ->
            val decorators =
                listOf(
                    rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
                    rememberViewModelStoreNavEntryDecorator<NavKey>(),
                )
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = decorators,
                entryProvider = entryProvider,
            )
        }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
