package com.afaneca.myfin.ui.login.biometric

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.afaneca.myfin.R
import com.afaneca.myfin.ui.common.MFPrimaryButton
import com.afaneca.myfin.ui.common.MFTextButton
import com.afaneca.myfin.ui.main.MainContract.LocalLoadingState

@Composable
fun BiometricSetupContent(
    uiState: BiometricSetupContract.MainState,
    modifier: Modifier = Modifier,
    onEnableClicked: () -> Unit,
    onSkipClicked: () -> Unit,
) {
    val loadingState = LocalLoadingState.current

    LaunchedEffect(uiState.isLoading) {
        loadingState.setLoading(uiState.isLoading)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Fingerprint icon
            Icon(
                painter = painterResource(id = R.drawable.fingerprint_24),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            // Title
            Text(
                text = stringResource(R.string.biometric_setup_prompt_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )

            // Description
            Text(
                text = stringResource(R.string.biometric_setup_prompt_description),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Enable button
            MFPrimaryButton(
                text = stringResource(R.string.biometric_enable),
                modifier = Modifier.fillMaxWidth(),
                isEnabled = !uiState.isLoading && uiState.isBiometricAvailable,
                onClick = onEnableClicked,
            )

            // Skip button
            MFTextButton(
                text = stringResource(R.string.biometric_skip),
                modifier = Modifier.fillMaxWidth(),
                isEnabled = !uiState.isLoading,
                onClick = onSkipClicked,
            )
        }
    }
}
