package com.afaneca.myfin.ui.common

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.defaultPopTransitionSpec
import androidx.navigation3.ui.defaultPredictivePopTransitionSpec
import androidx.navigation3.ui.defaultTransitionSpec
import com.afaneca.myfin.ui.common.nav.Navigator

@Composable
fun <T : Any> MFNavDisplay(
    entries: List<NavEntry<T>>,
    onBack: () -> Unit,
    transitionSpec: AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
        // Slide in from right when navigating forward
        slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
    },
    popTransitionSpec: AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
        // Slide in from left when navigating back
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
    },
    predictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform = {
        // Slide in from left when navigating back
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
    },
) {
    NavDisplay(
        entries = entries,
        onBack = { onBack() },
        /*fadeIn(tween(300)) togetherWith fadeOut(tween(300))*/
        /*transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },*/
        transitionSpec = transitionSpec,
        popTransitionSpec = popTransitionSpec,
        predictivePopTransitionSpec = predictivePopTransitionSpec,
    )
}