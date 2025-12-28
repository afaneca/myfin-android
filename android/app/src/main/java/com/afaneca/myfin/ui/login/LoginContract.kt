package com.afaneca.myfin.ui.login


import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow

object LoginContract {
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
        val usernameInput: String? = null,
        val passwordInput: String? = null,
        val isLoginButtonEnabled: Boolean = false,
        val isBiometricEnabled: Boolean = false, // Current user has biometric credentials saved
        val isFirstLogin: Boolean = false,
    )

    /**
     * UI interactions, such as button clicks, data input, etc...
     */
    sealed interface Event {
        data object Init : Event
        data object LoginClicked : Event
        data class BiometricLoginClicked(val activity: FragmentActivity) : Event
        data class UsernameUpdated(val input: String) : Event
        data class PasswordUpdated(val input: String) : Event
    }

    /**
     * Non-persistent changes to the UI, such as temporary error messages, snackbar/toast messages, navigation, etc...
     */
    sealed interface Effect {
        data object GoToHome : Effect
        data class GoToBiometricSetup(val username: String, val password: String) : Effect
        data object ShowGenericError : Effect
        data object ShowInvalidCredentialsError : Effect
        data object ShowBiometricError : Effect
        data object ShowBiometricKeyInvalidatedError : Effect
    }
}
