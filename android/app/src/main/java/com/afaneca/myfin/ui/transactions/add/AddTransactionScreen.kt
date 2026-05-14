package com.afaneca.myfin.ui.transactions.add

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.afaneca.myfin.R
import com.afaneca.myfin.domain.model.Category
import com.afaneca.myfin.domain.model.Entity
import com.afaneca.myfin.domain.model.TransactionType
import com.afaneca.myfin.domain.model.UserAccount
import java.math.BigDecimal

@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    goBackToList: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.mainState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    suspend fun showGenericError(@StringRes errorMessage: Int? = null) {
        snackbarHostState.showSnackbar(
            message = context.getString(errorMessage ?: R.string.error_generic),
            withDismissAction = true,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.triggerEvent(AddTransactionContract.Event.Init)
    }

    LaunchedEffect(lifecycleOwner) {
        viewModel.effect
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is AddTransactionContract.Effect.ShowGenericError -> showGenericError(effect.errorMessage)
                    AddTransactionContract.Effect.GoBackToList -> goBackToList()
                }
            }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        AddTransactionContent(
            uiState = uiState,
            onTypeUpdated = { viewModel.triggerEvent(AddTransactionContract.Event.TypeUpdated(it)) },
            onDateUpdated = { viewModel.triggerEvent(AddTransactionContract.Event.DateUpdated(it)) },
            onAmountUpdated = { viewModel.triggerEvent(AddTransactionContract.Event.AmountUpdated(it)) },
            onAccountFromUpdated = { viewModel.triggerEvent(AddTransactionContract.Event.AccountFromUpdated(it)) },
            onAccountToUpdated = { viewModel.triggerEvent(AddTransactionContract.Event.AccountToUpdated(it)) },
            onCategoryUpdated = { viewModel.triggerEvent(AddTransactionContract.Event.CategoryUpdated(it)) },
            onEntityUpdated = { viewModel.triggerEvent(AddTransactionContract.Event.EntityUpdated(it)) },
            onDescriptionUpdated = { viewModel.triggerEvent(AddTransactionContract.Event.DescriptionUpdated(it)) },
            onIsEssentialUpdated = { viewModel.triggerEvent(AddTransactionContract.Event.IsEssentialUpdated(it)) },
            onAutoCategorizeClicked = { viewModel.triggerEvent(AddTransactionContract.Event.AutoCategorizeClicked) },
            onSaveClicked = { viewModel.triggerEvent(AddTransactionContract.Event.SaveClicked) },
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
