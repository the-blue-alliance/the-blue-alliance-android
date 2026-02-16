package com.thebluealliance.android.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey) {
        if (route in state.topLevelRoutes) {
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    fun goBack() {
        // If we're at the base of the current route, go back to the start route stack.
        if (state.currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            state.currentStack.removeLastOrNull()
        }
    }
}
