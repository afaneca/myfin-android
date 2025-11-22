package com.afaneca.myfin.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.domain.usecase.LoginUseCase
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.ui.common.EffectMutableSharedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel(), LoginContract.ViewModel {

    private val _mainState = MutableStateFlow(LoginContract.MainState())
    override val mainState = _mainState.asStateFlow()

    private val _effect = EffectMutableSharedFlow<LoginContract.Effect>()
    override val effect = _effect.asSharedFlow()

    override fun triggerEvent(event: LoginContract.Event) {
        when (event) {
            LoginContract.Event.Init -> {
                // TODO - check if there's a saved user
                // TODO - remove mock login data!
                _mainState.update { it.copy(isInitialized = true, isLoading = false, usernameInput = "demo", passwordInput = "demo", isLoginButtonEnabled = true) }
            }

            LoginContract.Event.LoginClicked -> {
                attemptLogin()
            }

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
                    )
                }
            }
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
        val params = LoginUseCase.LoginParams(
            mainState.value.usernameInput ?: "",
            mainState.value.passwordInput ?: "",
        )
        val result = loginUseCase(params)
        _mainState.update { it.copy(isLoading = false) }
        when (result) {
            is Result.Error -> handleLoginError(error = result.error)
            is Result.Success -> {
                _effect.tryEmit(LoginContract.Effect.GoToHome)
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


