package com.afaneca.myfin.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.afaneca.myfin.ui.theme.AppTheme
import com.afaneca.myfin.R
import com.afaneca.myfin.ui.common.MFTextInput
import com.afaneca.myfin.ui.theme.LocalSpacing

@Composable
fun SettingsContent(
    uiState: SettingsContract.MainState,
    onBaseApiUrlChange: (String) -> Unit,
    onDynamicThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LocalSpacing.current.medium),
    ) {
        // API URL Setting
        var isEditing by remember { mutableStateOf(false) }
        var tempApiUrl by remember { mutableStateOf(uiState.baseApiUrl) }

        LaunchedEffect(uiState.baseApiUrl) {
            tempApiUrl = uiState.baseApiUrl
        }


        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalSpacing.current.medium),
            ) {
                Text(
                    text = stringResource(R.string.settings_base_api_url_label),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(LocalSpacing.current.small))
                if (isEditing) {
                    MFTextInput(
                        modifier = Modifier.fillMaxWidth(),
                        value = tempApiUrl,
                        onValueChange = { tempApiUrl = it },
                        label = stringResource(R.string.settings_base_api_url_label),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = { isEditing = false }) {
                            Text(stringResource(R.string.cancel))
                        }
                        TextButton(
                            onClick = {
                                onBaseApiUrlChange(tempApiUrl)
                                isEditing = false
                            },
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    }
                } else {
                    Text(text = uiState.baseApiUrl)
                    TextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { isEditing = true },
                    ) {
                        Text(stringResource(R.string.edit))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(LocalSpacing.current.medium))

        // Dynamic Color Setting
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalSpacing.current.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.settings_use_dynamic_theme_label),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.settings_use_dynamic_theme_description),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Switch(
                    checked = uiState.useDynamicTheme,
                    onCheckedChange = onDynamicThemeChange,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsContentPreview() {
    AppTheme {
        SettingsContent(
            uiState = SettingsContract.MainState(
                baseApiUrl = "https://api.example.com",
                useDynamicTheme = true,
            ),
            onBaseApiUrlChange = {},
            onDynamicThemeChange = {},
        )
    }
}