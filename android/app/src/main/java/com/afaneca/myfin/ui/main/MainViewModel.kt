package com.afaneca.myfin.ui.main

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.domain.model.BalanceDigest
import com.afaneca.myfin.domain.usecase.HasSessionDataUseCase
import com.afaneca.myfin.domain.usecase.LogoutUseCase
import com.afaneca.myfin.domain.usecase.ObserveAuthenticatedUseCase
import com.afaneca.myfin.domain.usecase.ObserveBalanceDigestUseCase
import com.afaneca.myfin.domain.usecase.ObserveUseDynamicThemeUseCase
import com.afaneca.myfin.ui.common.EffectMutableSharedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeUseDynamicThemeUseCase: ObserveUseDynamicThemeUseCase,
    observeBalanceDigestUseCase: ObserveBalanceDigestUseCase,
    observeAuthenticatedUseCase: ObserveAuthenticatedUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val hasSessionDataUseCase: HasSessionDataUseCase,
) : ViewModel(), MainContract.ViewModel {
    private val useDynamicTheme: StateFlow<Boolean> =
        observeUseDynamicThemeUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = true,
        )
    private val balanceDigest: StateFlow<BalanceDigest> = observeBalanceDigestUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = BalanceDigest(),
    )
    private val isAuthenticated: StateFlow<Boolean> = observeAuthenticatedUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false,
    )

    private val _mainState = MutableStateFlow(MainContract.MainState())
    override val mainState = combine(
        _mainState,
        useDynamicTheme,
        balanceDigest,
    ) { state, dynamicTheme, balanceDigest ->
        state.copy(useDynamicTheme = dynamicTheme, balanceDigest = balanceDigest)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MainContract.MainState(),
    )

    private val _effect = EffectMutableSharedFlow<MainContract.Effect>()
    override val effect = _effect.asSharedFlow()

    init {
        // Observe authentication state changes (triggered by 401 errors)
        viewModelScope.launch {
            isAuthenticated.collect { authenticated ->
                if (!authenticated) {
                    handleUnauthorized()
                }
            }
        }
    }

    override fun triggerEvent(event: MainContract.Event) {
        when (event) {
            MainContract.Event.Init -> init()
            MainContract.Event.NavDrawerIconClicked -> {
                _effect.tryEmit(MainContract.Effect.OpenNavigationDrawer)
            }

            is MainContract.Event.UpdateLoading -> {
                _mainState.update { it.copy(isLoading = event.isLoading) }
            }

            MainContract.Event.LogoutClicked -> logout()
        }
    }

    private fun handleUnauthorized() {
        _effect.tryEmit(MainContract.Effect.ShowUnauthorizedError)
        _effect.tryEmit(MainContract.Effect.GoToLogin)
    }

    private fun logout() = viewModelScope.launch {
        _mainState.update { it.copy(isLoading = true) }
        logoutUseCase(Unit)
        _mainState.update { it.copy(isLoading = false) }
        _effect.tryEmit(MainContract.Effect.GoToLogin)
    }

    private fun init() {
        if (mainState.value.isInitialized) return

        // Check for process death - session data lost
        if (!hasSessionDataUseCase()) {
            _mainState.update { it.copy(isLoading = true) }
            Handler(Looper.getMainLooper()).post {
                _effect.tryEmit(MainContract.Effect.GoToLogin)
                _mainState.update { it.copy(isLoading = false) }
            }
            return
        }

        _mainState.update { it.copy(isInitialized = true) }
    }


}
