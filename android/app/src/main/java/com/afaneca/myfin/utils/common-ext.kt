package com.afaneca.myfin.utils

import androidx.navigation.NavController
import androidx.navigation.NavDirections

/**
 * Avoids [IllegalArgumentException] on quick double tap by only trying to perform the navigation
 * if the destination is known by the current destination
 */
fun NavController.safeNavigate(navDirections: NavDirections) {
    currentDestination?.getAction(navDirections.actionId)?.let { navigate(navDirections) }
}