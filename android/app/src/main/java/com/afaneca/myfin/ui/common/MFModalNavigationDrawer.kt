package com.afaneca.myfin.ui.common

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afaneca.myfin.R
import com.afaneca.myfin.domain.model.BalanceDigest
import com.afaneca.myfin.ui.theme.AppTheme
import com.afaneca.myfin.ui.theme.LocalSpacing
import com.afaneca.myfin.ui.utils.formatAsCurrency

@Composable
fun MFModalNavigationDrawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    gesturesEnabled: Boolean = true,
    balanceDigest: BalanceDigest,
    itemContent: @Composable (() -> Unit),
    content: @Composable (() -> Unit),
) {
    val scrollState = rememberScrollState()

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            // TODO - replace mock values with real data
            ModalDrawerSheet {
                DrawerHeader(
                    netWorthFormatted = balanceDigest.netWorth.formatAsCurrency(balanceDigest.currency),
                    opFundsFormatted = balanceDigest.operatingFunds.formatAsCurrency(balanceDigest.currency),
                    investingFormatted = balanceDigest.investing.formatAsCurrency(balanceDigest.currency),
                    debtFormatted = balanceDigest.debt.formatAsCurrency(balanceDigest.currency),
                )
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.default),
                ) {
                    Spacer(modifier = Modifier.height(LocalSpacing.current.default))
                    itemContent()
                    Spacer(modifier = Modifier.height(LocalSpacing.current.default))
                }

            }
        },
    ) {
        content()
    }
}

@Composable
fun DrawerHeader(
    netWorthFormatted: String,
    opFundsFormatted: String,
    investingFormatted: String,
    debtFormatted: String,
) {
    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = LocalSpacing.current.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                SummaryCard(
                    modifier = Modifier.fillMaxWidth(0.5F),
                    label = stringResource(R.string.net_worth),
                    value = netWorthFormatted,
                )
                Spacer(modifier = Modifier.width(LocalSpacing.current.default))
                SummaryCard(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.operating_funds_short),
                    value = opFundsFormatted,
                )
            }
            Spacer(modifier = Modifier.height(LocalSpacing.current.default))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                SummaryCard(
                    modifier = Modifier.fillMaxWidth(0.5F),
                    label = stringResource(R.string.investing),
                    value = investingFormatted,
                )
                Spacer(modifier = Modifier.width(LocalSpacing.current.default))
                SummaryCard(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.debt),
                    value = debtFormatted,
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalSpacing.current.default),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MFModalNavigationDrawerPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    AppTheme() {
        MFModalNavigationDrawer(
            drawerState = drawerState,
            itemContent = {
                // Example drawer items
                ListItem(
                    headlineContent = { Text("Home") },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.dashboard_24),
                            contentDescription = "Home",
                        )
                    },
                    supportingContent = null,
                    trailingContent = null,
                )
                ListItem(
                    headlineContent = { Text("Transactions") },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.list_alt_24),
                            contentDescription = "Transactions",
                        )
                    },
                    supportingContent = null,
                    trailingContent = null,
                )
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Main Content Area")
                }
            },
            balanceDigest = BalanceDigest(
                netWorth = 1000.toBigDecimal(),
                operatingFunds = 500.toBigDecimal(),
                investing = 200.toBigDecimal(),
                debt = 300.toBigDecimal(),
                currency = "USD",
            ),
        )
    }
}
