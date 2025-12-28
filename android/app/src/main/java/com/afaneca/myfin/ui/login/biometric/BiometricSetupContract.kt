package com.afaneca.myfin.ui.login.biometric

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

object BiometricSetupContract {
    interface ViewModel {
        val mainState: StateFlow<MainState>
        val effect: SharedFlow<Effect>

        fun triggerEvent(event: Event)
    }

    data class MainState(
        val isInitialized: Boolean = false,
        val isBiometricAvailable: Boolean = false,
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        data class Init(val username: String, val password: String) : Event
        data class EnableBiometricClicked(val activity: FragmentActivity) : Event
        data object SkipClicked : Event
    }

    sealed interface Effect {
        data object GoToHome : Effect
        data object ShowBiometricNotAvailableError : Effect
        data object ShowBiometricNotEnrolledError : Effect
        data object ShowBiometricError : Effect
    }
}
