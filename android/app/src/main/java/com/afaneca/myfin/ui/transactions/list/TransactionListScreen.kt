package com.afaneca.myfin.ui.transactions.list

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.afaneca.myfin.domain.model.Transaction
import com.afaneca.myfin.ui.common.MFScaffoldWithNavDrawer
import com.afaneca.myfin.ui.transactions.details.TransactionDetailsBottomSheet
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = hiltViewModel(),
    openNavigationDrawer: () -> Unit,
    goToSettings: () -> Unit,
    goToAddTransaction: () -> Unit,
    goToEditTransaction: (Transaction) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.mainState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    suspend fun showGenericError(@StringRes errorMessage: Int? = null) {
        snackbarHostState.showSnackbar(
            message = context.getString(errorMessage ?: R.string.error_generic),
            withDismissAction = true,
        )
    }

    fun onNavigationIconClicked() {
        viewModel.triggerEvent(TransactionListContract.Event.NavigationIconClicked)
    }

    fun onToolbarSettingsClicked() {
        viewModel.triggerEvent(TransactionListContract.Event.ToolbarSettingsClicked)
    }

    fun onQueryUpdated(query: String) {
        viewModel.triggerEvent(TransactionListContract.Event.QueryInputUpdated(query))
    }

    fun onEndOfListReached() {
        viewModel.triggerEvent(TransactionListContract.Event.EndOfListReached)
    }

    fun onClearQueryClicked() {
        viewModel.triggerEvent(TransactionListContract.Event.ClearQueryClicked)
    }

    fun onTransactionClicked(transaction: Transaction) {
        viewModel.triggerEvent(TransactionListContract.Event.TransactionClicked(transaction))
    }

    fun onDismissTransactionDetails() {
        viewModel.triggerEvent(TransactionListContract.Event.DismissTransactionDetails)
    }

    fun onAddTransactionClicked() {
        viewModel.triggerEvent(TransactionListContract.Event.AddTransactionClicked)
    }

    fun onRefresh() {
        viewModel.triggerEvent(TransactionListContract.Event.Refresh)
    }

    LaunchedEffect(Unit) {
        viewModel.triggerEvent(TransactionListContract.Event.Init)
    }

    LaunchedEffect(lifecycleOwner) {
        viewModel.effect
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    TransactionListContract.Effect.OpenNavigationDrawer -> openNavigationDrawer()
                    is TransactionListContract.Effect.ShowGenericError -> showGenericError(effect.errorMessage)
                    TransactionListContract.Effect.GoToSettings -> goToSettings()
                    TransactionListContract.Effect.GoToAddTransaction -> goToAddTransaction()
                    is TransactionListContract.Effect.GoToEditTransaction -> goToEditTransaction(
                        effect.transaction,
                    )
                }
            }
    }

    LaunchedEffect(uiState.selectedTransaction) {
        scope.launch {
            if (uiState.selectedTransaction != null) {
                sheetState.show()
            } else {
                sheetState.hide()
            }
        }
    }

    MFScaffoldWithNavDrawer(
        title = stringResource(R.string.app_name),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        onNavigationIconClicked = { onNavigationIconClicked() },
        actions = {
            IconButton(onClick = { onToolbarSettingsClicked() }) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_24),
                    contentDescription = stringResource(R.string.settings),
                )
            }
        },
    ) { padding ->
        TransactionListContent(
            uiState = uiState,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            onQueryUpdated = ::onQueryUpdated,
            onEndOfListReached = ::onEndOfListReached,
            onClearQueryClicked = ::onClearQueryClicked,
            onTransactionClicked = ::onTransactionClicked,
            onAddTransactionClicked = ::onAddTransactionClicked,
            onRefresh = ::onRefresh,
        )

        uiState.selectedTransaction?.let {
            TransactionDetailsBottomSheet(
                transaction = it,
                sheetState = sheetState,
                onDismiss = ::onDismissTransactionDetails,
                onEditClicked = { /* TODO */ },
                onRemoveClicked = { /* TODO */ },
            )
        }
    }
}
