package com.afaneca.myfin.ui.login.biometric

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.afaneca.myfin.R
import com.afaneca.myfin.ui.common.MFScaffold
import com.afaneca.myfin.ui.login.biometric.BiometricSetupViewModel

@Composable
fun BiometricSetupScreen(
    username: String,
    password: String,
    viewModel: BiometricSetupViewModel = hiltViewModel(),
    goToHome: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.mainState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    suspend fun showBiometricNotAvailableError() {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.biometric_not_available),
            withDismissAction = true
        )
    }

    suspend fun showBiometricNotEnrolledError() {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.biometric_not_enrolled),
            withDismissAction = true
        )
    }

    suspend fun showBiometricError() {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.biometric_error),
            withDismissAction = true
        )
    }

    fun onEnableClicked() {
        if (context is FragmentActivity) {
            viewModel.triggerEvent(
                BiometricSetupContract.Event.EnableBiometricClicked(context)
            )
        }
    }

    fun onSkipClicked() {
        viewModel.triggerEvent(BiometricSetupContract.Event.SkipClicked)
    }

    LaunchedEffect(Unit) {
        viewModel.triggerEvent(BiometricSetupContract.Event.Init(username, password))
    }

    LaunchedEffect(lifecycleOwner) {
        viewModel.effect
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    BiometricSetupContract.Effect.GoToHome -> goToHome()
                    BiometricSetupContract.Effect.ShowBiometricNotAvailableError ->
                        showBiometricNotAvailableError()
                    BiometricSetupContract.Effect.ShowBiometricNotEnrolledError ->
                        showBiometricNotEnrolledError()
                    BiometricSetupContract.Effect.ShowBiometricError ->
                        showBiometricError()
                }
            }
    }

    MFScaffold(
        title = "",
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        BiometricSetupContent(
            uiState = uiState,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            onEnableClicked = ::onEnableClicked,
            onSkipClicked = ::onSkipClicked,
        )
    }
}
