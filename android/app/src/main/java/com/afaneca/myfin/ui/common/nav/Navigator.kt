package com.afaneca.myfin.ui.common.nav

import androidx.navigation3.runtime.NavKey

/**
 * Handles navigation events (forward and back) by updating the navigation state.
 */
class Navigator(val state: NavigationState) {
    fun navigate(
        route: NavKey,
        removeCurrentFromBackStack: Boolean = false,
        forceAdd: Boolean = false
    ) {
        if (removeCurrentFromBackStack) {
            state.backStacks[state.topLevelRoute]?.removeLastOrNull()
        }

        if (!forceAdd && route in state.backStacks.keys) {
            // This is a top level route, just switch to it.
            if(state.backStacks[route]?.isEmpty() == true){
                state.backStacks[route]?.add(route)
            }

            // Update top-level history if switching to a different top-level route
            if (state.topLevelRoute != route) {
                if (removeCurrentFromBackStack) {
                    // Replace current top-level route in history with the new one
                    state.topLevelHistory = state.topLevelHistory.dropLast(1) + route
                } else {
                    // Add new top-level route to history
                    state.topLevelHistory += route
                }
            }

            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    /**
     * Tries to go back in the backstack. If it can't (already at the start route), it returns false.
     * Returns true otherwise.
     */
    fun goBack(): Boolean {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        return when (currentRoute) {
            state.startRoute -> {
                // If we're already at the start route, then we can't go back.
                false
            }

            state.topLevelRoute -> {
                // If we're at the base of the current top-level route, check top-level history
                if (state.topLevelHistory.size > 1) {
                    // Remove current from history and navigate to previous top-level route
                    val newHistory = state.topLevelHistory.dropLast(1)
                    val previousTopLevel = newHistory.last()
                    state.topLevelHistory = newHistory
                    state.topLevelRoute = previousTopLevel
                    return true
                } else {
                    // No top-level history, can't go back
                    return false
                }
            }

            else -> {
                val result = currentStack.removeLastOrNull()
                return result != null
            }
        }
    }
}
