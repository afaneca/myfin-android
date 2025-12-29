package com.afaneca.myfin.ui.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.afaneca.myfin.R
import com.afaneca.myfin.ui.common.MFScaffold
import com.afaneca.myfin.ui.common.MFScaffoldWithBackNav
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.mainState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    fun onBaseApiUrlChange(value: String) {
        viewModel.triggerEvent(SettingsContract.Event.BaseApiUrlUpdated(value))
    }

    fun onDynamicThemeChange(value: Boolean) {
        viewModel.triggerEvent(SettingsContract.Event.UseDynamicThemeUpdated(value))
    }

    suspend fun showGenericError(@StringRes errorMessage: Int? = null) {
        snackbarHostState.showSnackbar(
            message = context.getString(errorMessage ?: R.string.error_generic),
            withDismissAction = true,
        )
    }

    suspend fun showInvalidUrlError() {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.error_invalid_url_format),
            withDismissAction = true,
        )
    }

    /*BackHandler {
        viewModel.triggerEvent(SettingsContract.Event.BackClicked)
    }*/

    LaunchedEffect(Unit) {
        viewModel.triggerEvent(SettingsContract.Event.Init)
    }

    LaunchedEffect(lifecycleOwner) {
        viewModel.effect
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    SettingsContract.Effect.GoBack -> {
                        dispatcher?.onBackPressed()
                    }
                    is SettingsContract.Effect.ShowGenericError -> showGenericError(effect.errorMessage)
                    SettingsContract.Effect.ShowInvalidUrlError -> showInvalidUrlError()
                }
            }
    }

    MFScaffoldWithBackNav(
        title = stringResource(R.string.settings_toolbar_title),
        onNavigationIconClicked = { viewModel.triggerEvent(SettingsContract.Event.BackClicked) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        SettingsContent(
            uiState = uiState,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            onBaseApiUrlChange = ::onBaseApiUrlChange,
            onDynamicThemeChange = ::onDynamicThemeChange,
        )
    }
}
