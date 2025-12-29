package com.afaneca.myfin.ui.login

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.data.biometric.BiometricAuthManager
import com.afaneca.myfin.domain.usecase.BiometricLoginUseCase
import com.afaneca.myfin.domain.usecase.CheckLoginStatusUseCase
import com.afaneca.myfin.domain.usecase.LoginUseCase
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.ui.common.EffectMutableSharedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val KEY_MAIN_STATE = "mainState"
private const val KEY_SAVED_USERNAME = "savedUsername"
private const val KEY_CAN_USE_BIOMETRIC = "canUseBiometric"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val loginUseCase: LoginUseCase,
    private val checkLoginStatusUseCase: CheckLoginStatusUseCase,
    private val biometricLoginUseCase: BiometricLoginUseCase,
) : ViewModel(), LoginContract.ViewModel {

    private val _mainState: MutableStateFlow<LoginContract.MainState> =
        savedStateHandle.getMutableStateFlow(
            KEY_MAIN_STATE, LoginContract.MainState(),
        )
    override val mainState = _mainState.asStateFlow()

    private val _effect = EffectMutableSharedFlow<LoginContract.Effect>()
    override val effect = _effect.asSharedFlow()

    private var savedUsername: String?
        get() = savedStateHandle[KEY_SAVED_USERNAME]
        set(value) {
            savedStateHandle[KEY_SAVED_USERNAME] = value
        }

    private var canUseBiometric: Boolean
        get() = savedStateHandle[KEY_CAN_USE_BIOMETRIC] ?: false
        set(value) {
            savedStateHandle[KEY_CAN_USE_BIOMETRIC] = value
        }

    override fun triggerEvent(event: LoginContract.Event) {
        when (event) {
            LoginContract.Event.Init -> init()

            LoginContract.Event.LoginClicked -> attemptLogin()

            is LoginContract.Event.BiometricLoginClicked -> performBiometricLogin(event.activity)

            is LoginContract.Event.PasswordUpdated -> {
                _mainState.update {
                    it.copy(
                        passwordInput = event.input,
                        isLoginButtonEnabled = isInputValid(
                            passwordInput = event.input,
                        ),
                    )
                }
            }

            is LoginContract.Event.UsernameUpdated -> {
                _mainState.update {
                    it.copy(
                        usernameInput = event.input,
                        isLoginButtonEnabled = isInputValid(
                            usernameInput = event.input,
                        ),
                        // biometrics can never be enabled if current user input is not the saved username
                        isBiometricEnabled = if (!it.isBiometricEnabled) false else event.input == savedUsername,
                    )
                }
            }
        }
    }

    private fun init() = viewModelScope.launch {
        val statusResult = checkLoginStatusUseCase(Unit)
        val (savedUsername, canUseBiometric, isBiometricEnabled) = when (statusResult) {
            is Result.Success -> {
                Triple(
                    statusResult.data.savedUsername,
                    statusResult.data.biometricStatus.canUseBiometric,
                    statusResult.data.biometricStatus.isEnabled,
                )
            }

            is Result.Error -> Triple(null, false, false)
        }
        this@LoginViewModel.savedUsername = savedUsername
        this@LoginViewModel.canUseBiometric = canUseBiometric

        _mainState.update {
            it.copy(
                isInitialized = true,
                isLoading = false,
                isBiometricEnabled = isBiometricEnabled,
                usernameInput = savedUsername,
            )
        }
    }

    private fun isInputValid(
        usernameInput: String? = mainState.value.usernameInput,
        passwordInput: String? = mainState.value.passwordInput,
    ): Boolean {
        return usernameInput?.isNotBlank() == true && passwordInput?.isNotBlank() == true
    }

    private fun attemptLogin() = viewModelScope.launch {
        _mainState.update { it.copy(isLoading = true) }
        val username = mainState.value.usernameInput ?: ""
        val password = mainState.value.passwordInput ?: ""
        val params = LoginUseCase.LoginParams(username, password)
        val result = loginUseCase(params)
        _mainState.update { it.copy(isLoading = false) }
        when (result) {
            is Result.Error -> handleLoginError(error = result.error)
            is Result.Success -> {
                // Check if this is first login (biometric not enabled yet)
                // Use state value which reflects whether current username has biometric enabled
                val isBiometricEnabled = mainState.value.isBiometricEnabled

                if (!isBiometricEnabled && canUseBiometric) {
                    // First login - redirect to biometric setup
                    _mainState.update { it.copy(isFirstLogin = true) }
                    _effect.tryEmit(LoginContract.Effect.GoToBiometricSetup(username, password))
                } else {
                    // Not first login or biometric not available - go directly to home
                    _effect.tryEmit(LoginContract.Effect.GoToHome)
                }
            }
        }
    }

    private fun performBiometricLogin(activity: FragmentActivity) = viewModelScope.launch {
        _mainState.update { it.copy(isLoading = true) }

        val result = biometricLoginUseCase(BiometricLoginUseCase.Params(activity))

        _mainState.update { it.copy(isLoading = false) }

        when (result) {
            is Result.Success -> {
                _effect.tryEmit(LoginContract.Effect.GoToHome)
            }

            is Result.Error -> {
                when (result.error) {
                    BiometricLoginUseCase.FeatureError.Cancelled -> {
                        // User cancelled, do nothing
                    }

                    is BiometricLoginUseCase.FeatureError.AuthenticationFailed -> {
                        // Check if key was invalidated
                        if (result.error.errorCode == BiometricAuthManager.ERROR_KEY_INVALIDATED) {
                            _effect.tryEmit(LoginContract.Effect.ShowBiometricKeyInvalidatedError)
                        } else {
                            _effect.tryEmit(LoginContract.Effect.ShowBiometricError)
                        }
                    }

                    BiometricLoginUseCase.FeatureError.InvalidCredentials -> {
                        _effect.tryEmit(LoginContract.Effect.ShowInvalidCredentialsError)
                    }

                    BiometricLoginUseCase.FeatureError.CredentialsNotFound,
                    BiometricLoginUseCase.FeatureError.LoginFailed,
                    BiometricLoginUseCase.FeatureError.UnexpectedError,
                        -> {
                        _effect.tryEmit(LoginContract.Effect.ShowBiometricError)
                    }
                }
            }
        }
    }

    private fun handleLoginError(error: LoginUseCase.FeatureError) {
        _effect.tryEmit(
            when (error) {
                LoginUseCase.FeatureError.GenericError -> LoginContract.Effect.ShowGenericError
                LoginUseCase.FeatureError.InvalidCredentials -> LoginContract.Effect.ShowInvalidCredentialsError
            },
        )
    }
}


