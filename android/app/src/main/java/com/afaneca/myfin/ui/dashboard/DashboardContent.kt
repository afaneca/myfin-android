package com.afaneca.myfin.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afaneca.myfin.R
import com.afaneca.myfin.ui.common.MFChip
import com.afaneca.myfin.ui.main.MainContract.LocalLoadingState
import com.afaneca.myfin.ui.theme.AppTheme
import com.afaneca.myfin.ui.theme.LocalSpacing

@Composable
fun DashboardContent(
    uiState: DashboardContract.MainState,
    modifier: Modifier = Modifier,
    onAddTransactionShortcutClicked: () -> Unit = {},
    onAccountsShortcutClicked: () -> Unit = {},
    onBudgetsShortcutClicked: () -> Unit = {},
) {
    val loadingState = LocalLoadingState.current

    LaunchedEffect(uiState.isLoading) {
        loadingState.setLoading(uiState.isLoading)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
    ) {
        HeaderShortcuts(
            onAddTransactionClicked = onAddTransactionShortcutClicked,
            onAccountsClicked = onAccountsShortcutClicked,
            onBudgetsClicked = onBudgetsShortcutClicked,
        )
        Spacer(modifier = Modifier.height(LocalSpacing.current.large))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = LocalSpacing.current.default),
            verticalArrangement = Arrangement.Top,
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = MaterialTheme.shapes.medium,
                ) {
                Column(
                    modifier = Modifier.padding(horizontal = LocalSpacing.current.medium, vertical = LocalSpacing.current.large),
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = "€1 976.98",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.alignByBaseline()
                        )
                        Spacer(modifier = Modifier.width(LocalSpacing.current.extraSmall))
                        Text(
                            text = "left in monthly expenses",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                    Spacer(modifier = Modifier.height(LocalSpacing.current.default))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        progress = { 0.7F },
                        drawStopIndicator = {},
                    )
                    Spacer(modifier = Modifier.height(LocalSpacing.current.default))
                    Text(
                        text = "€0.00 of €1,976.98 spent",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }


            }

        }
    }
}

@Composable
private fun HeaderShortcuts(
    onAddTransactionClicked: () -> Unit = {},
    onAccountsClicked: () -> Unit = {},
    onBudgetsClicked: () -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = LocalSpacing.current.default)
            .horizontalScroll(scrollState),
        horizontalArrangement = spacedBy(LocalSpacing.current.default),
    ) {
        MFChip(
            onClick = onAddTransactionClicked,
            label = stringResource(R.string.shortcut_add_transaction),
        )
        MFChip(
            onClick = {},
            label = stringResource(R.string.shortcut_budgets),
        )
        MFChip(
            onClick = {},
            label = stringResource(R.string.shortcut_accounts),
        )
    }
}

@Preview(device = Devices.PIXEL_6A)
@Composable
fun DashboardPreview() {
    AppTheme {
        DashboardContent(
            uiState = DashboardContract.MainState(),
            modifier = Modifier,
        )
    }
}
