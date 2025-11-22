package com.afaneca.myfin.ui.dashboard

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
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
import com.afaneca.myfin.ui.common.MFScaffoldWithNavDrawer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    openNavigationDrawer: () -> Unit,
    goToSettings: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.mainState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    fun onNavigationIconClicked() {
        viewModel.triggerEvent(DashboardContract.Event.NavigationIconClicked)
    }

    suspend fun showGenericError(@StringRes errorMessage: Int? = null) {
        snackbarHostState.showSnackbar(
            message = context.getString(errorMessage ?: R.string.error_generic),
            withDismissAction = true,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.triggerEvent(DashboardContract.Event.Init)

        viewModel.effect.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { effect ->
                when (effect) {
                    is DashboardContract.Effect.ShowGenericError -> showGenericError(effect.errorMessage)
                    DashboardContract.Effect.OpenNavigationDrawer -> openNavigationDrawer()
                }
            }.launchIn(this)
    }

    MFScaffoldWithNavDrawer(title = stringResource(R.string.app_name),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        onNavigationIconClicked = { onNavigationIconClicked() },
        actions = {
            IconButton(onClick = goToSettings) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_24),
                    contentDescription = stringResource(R.string.settings),
                )
            }
        },
    ) { padding ->
        DashboardContent(
            uiState = uiState,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
        )
    }
}
