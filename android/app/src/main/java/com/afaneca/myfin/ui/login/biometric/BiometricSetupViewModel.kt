package com.afaneca.myfin.ui.login.biometric

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.usecase.CheckLoginStatusUseCase
import com.afaneca.myfin.domain.usecase.SaveBiometricCredentialsUseCase
import com.afaneca.myfin.ui.common.EffectMutableSharedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricSetupViewModel @Inject constructor(
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val saveBiometricCredentialsUseCase: SaveBiometricCredentialsUseCase,
) : ViewModel(), BiometricSetupContract.ViewModel {

    private val _mainState = MutableStateFlow(BiometricSetupContract.MainState())
    override val mainState = _mainState.asStateFlow()

    private val _effect = EffectMutableSharedFlow<BiometricSetupContract.Effect>()
    override val effect = _effect.asSharedFlow()

    private var storedUsername: String = ""
    private var storedPassword: String = ""

    override fun triggerEvent(event: BiometricSetupContract.Event) {
        when (event) {
            is BiometricSetupContract.Event.Init -> init(event.username, event.password)
            is BiometricSetupContract.Event.EnableBiometricClicked -> performBiometricSetup(event.activity)
            BiometricSetupContract.Event.SkipClicked -> skip()
        }
    }

    private fun init(username: String, password: String) = viewModelScope.launch {
        if (mainState.value.isInitialized) return@launch

        storedUsername = username
        storedPassword = password

        val isBiometricAvailable = when (val statusResult = checkLoginStatusUseCase(Unit)) {
            is Result.Success -> statusResult.data.biometricStatus.canUseBiometric
            is Result.Error -> false
        }

        _mainState.update {
            it.copy(
                isInitialized = true,
                isBiometricAvailable = isBiometricAvailable
            )
        }

        // If biometric is not available, skip to home automatically
        if (!isBiometricAvailable) {
            skip()
        }
    }

    private fun performBiometricSetup(activity: FragmentActivity) = viewModelScope.launch {
        _mainState.update { it.copy(isLoading = true) }

        val params = SaveBiometricCredentialsUseCase.Params(
            username = storedUsername,
            password = storedPassword,
            activity = activity
        )

        val saveResult = when (val useCaseResult = saveBiometricCredentialsUseCase(params)) {
            is Result.Success -> useCaseResult.data
            is Result.Error -> {
                _mainState.update { it.copy(isLoading = false) }
                _effect.tryEmit(BiometricSetupContract.Effect.ShowBiometricError)
                return@launch
            }
        }

        _mainState.update { it.copy(isLoading = false) }

        when (saveResult) {
            SaveBiometricCredentialsUseCase.SaveResult.Success -> {
                _effect.tryEmit(BiometricSetupContract.Effect.GoToHome)
            }
            SaveBiometricCredentialsUseCase.SaveResult.NotAvailable -> {
                _effect.tryEmit(BiometricSetupContract.Effect.ShowBiometricNotAvailableError)
            }
            SaveBiometricCredentialsUseCase.SaveResult.NotEnrolled -> {
                _effect.tryEmit(BiometricSetupContract.Effect.ShowBiometricNotEnrolledError)
            }
            SaveBiometricCredentialsUseCase.SaveResult.Cancelled -> {
                // User cancelled, do nothing and let them retry
            }
            is SaveBiometricCredentialsUseCase.SaveResult.Failure -> {
                _effect.tryEmit(BiometricSetupContract.Effect.ShowBiometricError)
            }
        }
    }

    private fun skip() {
        _effect.tryEmit(BiometricSetupContract.Effect.GoToHome)
    }
}
