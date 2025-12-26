package com.afaneca.myfin.ui.main

import androidx.compose.runtime.compositionLocalOf
import com.afaneca.myfin.domain.model.BalanceDigest
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow

object MainContract {

    data class LoadingState(
        val isLoading: Boolean = false,
        val setLoading: (Boolean) -> Unit = {}
    )

    val LocalLoadingState = compositionLocalOf { LoadingState() }

    interface ViewModel {
        val mainState: StateFlow<MainState>
        val effect: SharedFlow<Effect>

        fun triggerEvent(event: Event)
    }

    /**
     * Persistent view state. Single object per logical section (might contain other objects inside it for better organization).
     */
    data class MainState(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val useDynamicTheme: Boolean = true,
        val balanceDigest: BalanceDigest = BalanceDigest(),
    )

    /**
     * UI interactions, such as button clicks, data input, etc...
     */
    sealed interface Event {
        data object Init : Event
        data object NavDrawerIconClicked : Event
        data class UpdateLoading(val isLoading: Boolean) : Event
        data object LogoutClicked : Event
    }

    /**
     * Non-persistent changes to the UI, such as temporary error messages, snackbar/toast messages, navigation, etc...
     */
    sealed interface Effect {
        data object OpenNavigationDrawer : Effect
        data object GoToLogin : Effect
    }
}
