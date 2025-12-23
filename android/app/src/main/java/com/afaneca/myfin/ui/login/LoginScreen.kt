package com.afaneca.myfin.ui.login

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.afaneca.myfin.R
import com.afaneca.myfin.ui.common.MFScaffold

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    goToHome: () -> Unit,
    goToSettings: () -> Unit,
    showError: (message: String?) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.mainState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    fun onLoginButtonClick() {
        viewModel.triggerEvent(LoginContract.Event.LoginClicked)
    }

    fun onUsernameUpdated(username: String) {
        viewModel.triggerEvent(LoginContract.Event.UsernameUpdated(username))
    }

    fun onPasswordUpdated(password: String) {
        viewModel.triggerEvent(LoginContract.Event.PasswordUpdated(password))
    }

    suspend fun showInvalidCredentialsError() {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.error_invalid_credentials),
            withDismissAction = true
        )
    }

    suspend fun showGenericError() {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.error_generic),
            withDismissAction = true
        )
    }

    LaunchedEffect(Unit) {
        viewModel.triggerEvent(LoginContract.Event.Init)

        viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    LoginContract.Effect.GoToHome -> goToHome()
                    LoginContract.Effect.ShowGenericError -> showGenericError()
                    LoginContract.Effect.ShowInvalidCredentialsError -> showInvalidCredentialsError()
                }
            }
    }

    MFScaffold(
        title = "",
        snackbarHost = { SnackbarHost(snackbarHostState) },
        actions = {
            IconButton(onClick = goToSettings) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_24),
                    contentDescription = stringResource(R.string.settings),
                )
            }
        },
    ) { padding ->
        LoginContent(
            uiState = uiState,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            onUsernameUpdated = ::onUsernameUpdated,
            onPasswordUpdated = ::onPasswordUpdated,
            onLoginButtonClicked = ::onLoginButtonClick,
        )
    }
}
