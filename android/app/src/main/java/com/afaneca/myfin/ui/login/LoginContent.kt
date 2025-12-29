package com.afaneca.myfin.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afaneca.myfin.R
import com.afaneca.myfin.ui.common.MFPrimaryButton
import com.afaneca.myfin.ui.common.MFSecretTextInput
import com.afaneca.myfin.ui.common.MFTextInput
import com.afaneca.myfin.ui.main.MainContract.LocalLoadingState
import com.afaneca.myfin.ui.theme.AppTheme
import com.afaneca.myfin.ui.theme.LocalSpacing

@Composable
fun LoginContent(
    uiState: LoginContract.MainState,
    modifier: Modifier = Modifier,
    onUsernameUpdated: (String) -> Unit,
    onPasswordUpdated: (String) -> Unit,
    onLoginButtonClicked: () -> Unit,
    onBiometricLoginClicked: () -> Unit = {},
) {
    val loadingState = LocalLoadingState.current

    LaunchedEffect(uiState.isLoading) {
        loadingState.setLoading(uiState.isLoading)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = LocalSpacing.current.large),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painterResource(R.drawable.myfin_logo_transparent),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.height(LocalSpacing.current.larger2))
            MFTextInput(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.usernameInput ?: "",
                onValueChange = { onUsernameUpdated(it) },
                label = stringResource(R.string.prompt_username),
                enabled = uiState.isLoading.not(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            Spacer(modifier = Modifier.height(LocalSpacing.current.default))
            MFSecretTextInput(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.passwordInput ?: "",
                onValueChange = { onPasswordUpdated(it) },
                label = stringResource(R.string.prompt_password),
                enabled = uiState.isLoading.not(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )
            Spacer(modifier = Modifier.height(LocalSpacing.current.default))
            // Biometric login button (if enabled)
            AnimatedVisibility(
                visible = uiState.isBiometricEnabled,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Spacer(modifier = Modifier.height(LocalSpacing.current.default))
                Column {
                    IconButton(
                        onClick = onBiometricLoginClicked,
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.CenterHorizontally),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.fingerprint_24),
                            contentDescription = stringResource(R.string.biometric_login_title),
                            modifier = Modifier.size(48.dp),
                            tint = if (uiState.isLoading)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            else
                                MaterialTheme.colorScheme.primary,
                        )
                    }
                    Spacer(modifier = Modifier.height(LocalSpacing.current.default))
                }
            }

            MFPrimaryButton(
                modifier = Modifier.padding(vertical = LocalSpacing.current.default),
                text = stringResource(R.string.action_sign_in),
                isEnabled = uiState.isLoginButtonEnabled,
            ) { onLoginButtonClicked() }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    AppTheme {
        LoginContent(
            uiState = LoginContract.MainState(isBiometricEnabled = true),
            modifier = Modifier,
            onUsernameUpdated = {},
            onPasswordUpdated = {},
            onLoginButtonClicked = {},
        )
    }
}
