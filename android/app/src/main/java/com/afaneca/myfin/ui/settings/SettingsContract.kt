package com.afaneca.myfin.ui.settings


import androidx.annotation.StringRes
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow

object SettingsContract {
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
        val baseApiUrl: String = "",
        val useDynamicTheme: Boolean = false,
    )

    /**
     * UI interactions, such as button clicks, data input, etc...
     */
    sealed interface Event {
        data object Init : Event
        data object BackClicked : Event
        data class BaseApiUrlUpdated(val input: String) : Event
        data class UseDynamicThemeUpdated(val input: Boolean) : Event
    }

    /**
     * Non-persistent changes to the UI, such as temporary error messages, snackbar/toast messages, navigation, etc...
     */
    sealed interface Effect {
        data object GoBack : Effect
        data object ShowInvalidUrlError : Effect
        data class ShowGenericError(@StringRes val errorMessage: Int? = null) : Effect
    }
}
