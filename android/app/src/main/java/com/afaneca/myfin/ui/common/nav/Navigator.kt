package com.afaneca.myfin.ui.common.nav

import androidx.navigation3.runtime.NavKey
import kotlin.collections.last
import kotlin.collections.removeLastOrNull

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
                // If we're at the base of the current route, go back to the start route stack.
                state.topLevelRoute = state.startRoute
                return true
            }

            else -> {
                val result = currentStack.removeLastOrNull()
                return result != null
            }
        }
    }
}
