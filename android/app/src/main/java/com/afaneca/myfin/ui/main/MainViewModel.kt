package com.afaneca.myfin.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.data.repository.SettingsRepository
import com.afaneca.myfin.domain.model.BalanceDigest
import com.afaneca.myfin.domain.usecase.ObserveBalanceDigestUseCase
import com.afaneca.myfin.domain.usecase.ObserveUseDynamicThemeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import com.afaneca.myfin.ui.common.EffectMutableSharedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeUseDynamicThemeUseCase: ObserveUseDynamicThemeUseCase,
    observeBalanceDigestUseCase: ObserveBalanceDigestUseCase,
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

    override fun triggerEvent(event: MainContract.Event) {
        when (event) {
            MainContract.Event.Init -> init()
            MainContract.Event.NavDrawerIconClicked -> {
                _effect.tryEmit(MainContract.Effect.OpenNavigationDrawer)
            }

            is MainContract.Event.UpdateLoading -> {
                _mainState.update { it.copy(isLoading = event.isLoading) }
            }
        }
    }

    private fun init() {
        if (mainState.value.isInitialized) return
        _mainState.update { it.copy(isInitialized = true) }
    }
}
