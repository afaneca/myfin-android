package com.afaneca.myfin.ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.afaneca.myfin.ui.common.EffectMutableSharedFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel(), DashboardContract.ViewModel {
    private val _mainState = MutableStateFlow(DashboardContract.MainState())
    override val mainState = _mainState.asStateFlow()

    private val _effect = EffectMutableSharedFlow<DashboardContract.Effect>()
    override val effect = _effect.asSharedFlow()

    override fun triggerEvent(event: DashboardContract.Event) {
        when(event){
            DashboardContract.Event.Init -> {
                // TODO
            }
            DashboardContract.Event.NavigationIconClicked -> {
                _effect.tryEmit(DashboardContract.Effect.OpenNavigationDrawer)
            }
        }
    }
}