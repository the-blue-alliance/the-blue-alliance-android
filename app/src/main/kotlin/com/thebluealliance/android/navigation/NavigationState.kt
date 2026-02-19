package com.thebluealliance.android.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
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
    startRoute: NavKey,
    isNewTask: Boolean,
    topLevelRoutes: List<NavKey>,
    startTopLevelRoute: NavKey,
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) {
        val startTopLevel = when (startRoute) {
            in topLevelRoutes -> startRoute
            else -> startTopLevelRoute
        }
        mutableStateOf(startTopLevel)
    }

    val backStacks = topLevelRoutes.associateWith { key ->
        if (key == startTopLevelRoute && startRoute != startTopLevelRoute) {
            val syntheticStack = buildBackStack(
                startKey = startRoute,
                parentRoute = startTopLevelRoute,
                buildFullPath = isNewTask
            )
            rememberNavBackStack(*syntheticStack.toTypedArray())
        } else {
            rememberNavBackStack(key)
        }
    }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks
        )
    }
}


/**
 * A function that build a synthetic backStack.
 *
 * This helper returns one of two possible backStacks:
 *
 * 1. a backStack with only the deeplinked key if [buildFullPath] is false.
 * 2. a backStack containing the deeplinked key and its hierarchical parent keys
 * if [buildFullPath] is true.
 *
 * Generally speaking, [buildFullPath] is true if the deeplink intent has the
 * [android.content.Intent.FLAG_ACTIVITY_NEW_TASK] and [android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK]
 * flags.
 * These flags indicate that the deeplinked Activity was started as the root Activity of a new Task, in which case
 * a full synthetic backStack is required in order to support the proper, expected back button behavior.
 *
 * If those flags were not present, it means the deeplinked Activity was started
 * in the app that originally triggered the deeplink. In this case, that original app is assumed to
 * already have existing screens that users can system back into, therefore a synthetic backstack
 * is OPTIONAL.
 *
 */
internal fun buildBackStack(
    startKey: NavKey,
    parentRoute: NavKey,
    buildFullPath: Boolean
): List<NavKey> {
    if (!buildFullPath) return listOf(startKey)
    /**
     * iterate up the parents of the startKey until it reaches the root key (a key without a parent)
     */
    return listOf(
        // In the future each NavKey could define its own parent if we need a more complex nav graph
        parentRoute,
        startKey
    )
}

class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    val topLevelRoutes = backStacks.keys
    var topLevelRoute: NavKey by topLevelRoute
    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == startRoute) {
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
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator<NavKey>(),
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
