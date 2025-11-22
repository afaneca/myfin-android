package com.afaneca.myfin.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.usecase.GetSettingsDataUseCase
import com.afaneca.myfin.domain.usecase.SetBaseApiUrlUseCase
import com.afaneca.myfin.domain.usecase.SetUseDynamicThemeUseCase
import com.afaneca.myfin.ui.common.EffectMutableSharedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsDataUseCase: GetSettingsDataUseCase,
    private val setBaseApiUrlUseCase: SetBaseApiUrlUseCase,
    private val setUseDynamicThemeUseCase: SetUseDynamicThemeUseCase,
) : ViewModel(), SettingsContract.ViewModel {
    private val _mainState = MutableStateFlow(SettingsContract.MainState())
    override val mainState = _mainState.asStateFlow()

    private val _effect = EffectMutableSharedFlow<SettingsContract.Effect>()
    override val effect = _effect.asSharedFlow()

    override fun triggerEvent(event: SettingsContract.Event) {
        when (event) {
            SettingsContract.Event.BackClicked -> {
                _effect.tryEmit(SettingsContract.Effect.GoBack)
            }

            is SettingsContract.Event.BaseApiUrlUpdated -> {
                val normalizedUrl = normalizeUrl(event.input)
                setBaseApiUrl(normalizedUrl)
            }

            SettingsContract.Event.Init -> init()
            is SettingsContract.Event.UseDynamicThemeUpdated -> {
                setUseDynamicTheme(event.input)
            }
        }
    }

    private fun setBaseApiUrl(normalizedUrl: String) = viewModelScope.launch {
        _mainState.update { it.copy(isLoading = true) }
        val result = setBaseApiUrlUseCase(SetBaseApiUrlUseCase.Params(normalizedUrl))
        _mainState.update { it.copy(isLoading = false) }
        when (result) {
            is Result.Error -> handleSetBaseApiUrlError(result)
            is Result.Success -> {
                _mainState.update { it.copy(baseApiUrl = normalizedUrl) }
            }
        }
    }

    private fun handleSetBaseApiUrlError(result: Result.Error<SetBaseApiUrlUseCase.FeatureError>) {
        _effect.tryEmit(
            when (result.error) {
                SetBaseApiUrlUseCase.FeatureError.InvalidUrlFormat -> SettingsContract.Effect.ShowInvalidUrlError
                else -> SettingsContract.Effect.ShowGenericError()
            },
        )
    }

    private fun setUseDynamicTheme(useDynamicTheme: Boolean) = viewModelScope.launch {
        _mainState.update {
            it.copy(isLoading = true)
        }
        val result = setUseDynamicThemeUseCase(SetUseDynamicThemeUseCase.Params(useDynamicTheme))
        _mainState.update { it.copy(isLoading = false) }
        when (result) {
            is Result.Error -> _effect.tryEmit(SettingsContract.Effect.ShowGenericError())
            is Result.Success -> {
                _mainState.update { it.copy(useDynamicTheme = useDynamicTheme) }
            }
        }
    }

    private fun init() {
        if (mainState.value.isInitialized) return
        _mainState.update { it.copy(isInitialized = true, isLoading = true) }
        getSettings()
    }

    private fun getSettings() = viewModelScope.launch {
        val result = getSettingsDataUseCase(Unit)
        _mainState.update { it.copy(isLoading = false) }
        when (result) {
            is Result.Error -> {
                _effect.tryEmit(SettingsContract.Effect.ShowGenericError())
            }

            is Result.Success -> {
                _mainState.update {
                    it.copy(
                        baseApiUrl = result.data.baseApiUrl.toString(),
                        useDynamicTheme = result.data.useDynamicTheme,
                    )
                }
            }
        }
    }

    private fun normalizeUrl(url: String): String {
        return (if (url.startsWith("http")) url else "https://$url").removeSuffix("/")
    }
}
