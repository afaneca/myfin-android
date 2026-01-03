package com.afaneca.myfin.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.afaneca.myfin.R
import com.afaneca.myfin.ui.common.GlobalLoadingOverlay
import com.afaneca.myfin.ui.common.MFModalNavigationDrawer
import com.afaneca.myfin.ui.common.MFNavDisplay
import com.afaneca.myfin.ui.common.nav.Navigator
import com.afaneca.myfin.ui.common.nav.rememberNavigationState
import com.afaneca.myfin.ui.common.nav.toEntries
import com.afaneca.myfin.ui.dashboard.DashboardScreen
import com.afaneca.myfin.ui.login.LoginScreen
import com.afaneca.myfin.ui.main.MainContract.LocalLoadingState
import com.afaneca.myfin.ui.login.biometric.BiometricSetupScreen
import com.afaneca.myfin.ui.settings.SettingsScreen
import com.afaneca.myfin.ui.theme.AppTheme
import com.afaneca.myfin.ui.transactions.list.TransactionListScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {
    @Serializable
    data object Login : AppRoute

    @Serializable
    data class BiometricSetup(val username: String, val password: String) : AppRoute

    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Settings : AppRoute

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Transactions : AppRoute
}

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val lifecycleOwner = LocalLifecycleOwner.current
            val uiState by viewModel.mainState.collectAsStateWithLifecycle()
            val navigationState = rememberNavigationState(
                initialStartRoute = AppRoute.Login,
                topLevelRoutes = setOf(
                    AppRoute.Login,
                    AppRoute.Home,
                    AppRoute.Transactions,
                ),
            )

            val navigator = remember { Navigator(navigationState) }

            val isPrivateRoute = when (navigationState.topLevelRoute) {
                AppRoute.Login, AppRoute.Settings, AppRoute.Onboarding, is AppRoute.BiometricSetup -> false
                else -> true
            }

            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                viewModel.triggerEvent(MainContract.Event.Init)
            }

            LaunchedEffect(lifecycleOwner) {
                viewModel.effect
                    .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .collect { effect ->
                        when (effect) {
                            MainContract.Effect.OpenNavigationDrawer -> {
                                scope.launch { drawerState.open() }
                            }

                            MainContract.Effect.GoToLogin -> {
                                navigationState.startRoute = AppRoute.Login
                                navigator.navigate(
                                    route = AppRoute.Login,
                                    removeCurrentFromBackStack = true,
                                )
                            }

                            MainContract.Effect.ShowUnauthorizedError -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        getString(R.string.session_invalid_error)
                                    )
                                }
                            }
                        }
                    }
            }

            fun openNavigationDrawer() {
                viewModel.triggerEvent(MainContract.Event.NavDrawerIconClicked)
            }

            AppTheme(
                dynamicColor = uiState.useDynamicTheme,
            ) {

                CompositionLocalProvider(
                    LocalLoadingState provides MainContract.LoadingState(
                        isLoading = uiState.isLoading,
                        setLoading = { viewModel.triggerEvent(MainContract.Event.UpdateLoading(it)) },
                    ),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MFModalNavigationDrawer(
                            drawerState = drawerState,
                            gesturesEnabled = isPrivateRoute,
                            balanceDigest = uiState.balanceDigest,
                            itemContent = {
                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.dashboard_24),
                                            contentDescription = stringResource(R.string.dashboard),
                                        )
                                    },
                                    label = { Text(stringResource(R.string.dashboard)) },
                                    selected = navigationState.currentRoute == AppRoute.Home,
                                    onClick = {
                                        navigator.navigate(
                                            route = AppRoute.Home,
                                        )
                                        scope.launch { drawerState.close() }
                                    },
                                )
                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.list_alt_24),
                                            contentDescription = stringResource(R.string.transactions),
                                        )
                                    },
                                    label = { Text(stringResource(R.string.transactions)) },
                                    selected = navigationState.currentRoute == AppRoute.Transactions,
                                    onClick = {
                                        navigator.navigate(AppRoute.Transactions)
                                        scope.launch { drawerState.close() }
                                    },
                                )
                                NavigationDrawerItem(
                                    icon = {
                                        Icon(
                                            painter = painterResource(R.drawable.logout_24),
                                            contentDescription = stringResource(R.string.logout),
                                        )
                                    },
                                    label = { Text(stringResource(R.string.logout)) },
                                    selected = false,
                                    onClick = {
                                        viewModel.triggerEvent(MainContract.Event.LogoutClicked)
                                        scope.launch { drawerState.close() }
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        unselectedTextColor = MaterialTheme.colorScheme.error,
                                        unselectedIconColor = MaterialTheme.colorScheme.error,
                                    ),
                                )
                            },
                        ) {
                            val entryProvider = entryProvider {
                                publicNav(
                                    goToHome = {
                                        navigationState.startRoute = AppRoute.Home
                                        navigator.navigate(
                                            route = AppRoute.Home,
                                            removeCurrentFromBackStack = true,
                                        )
                                    },
                                    goToBiometricSetup = { username, password ->
                                        navigator.navigate(
                                            route = AppRoute.BiometricSetup(username, password),
                                            removeCurrentFromBackStack = true,
                                        )
                                    },
                                    goToSettings = { navigator.navigate(AppRoute.Settings) },
                                )
                                privateNav(
                                    openNavigationDrawer = { openNavigationDrawer() },
                                    goToSettings = { navigator.navigate(AppRoute.Settings) },
                                    goToTransactions = { navigator.navigate(AppRoute.Transactions) },
                                    goToAccounts = { /* TODO */ },
                                    goToBudgets = { /* TODO */ },
                                )
                            }

                            MFNavDisplay(
                                entries = navigationState.toEntries(entryProvider),
                                onBack = {
                                    if (drawerState.isOpen) {
                                        scope.launch { drawerState.close() }
                                        return@MFNavDisplay
                                    }
                                    if (!navigator.goBack()) {
                                        finish()
                                    }
                                },
                            )
                        }

                        GlobalLoadingOverlay(isLoading = uiState.isLoading)

                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding(),
                        )
                    }
                }
            }
        }
    }

    private fun EntryProviderScope<NavKey>.publicNav(
        goToHome: () -> Unit,
        goToBiometricSetup: (username: String, password: String) -> Unit,
        goToSettings: () -> Unit,
    ) {
        entry<AppRoute.Login> { key ->
            LoginScreen(
                goToHome = goToHome,
                goToBiometricSetup = goToBiometricSetup,
                goToSettings = goToSettings,
                showError = {},
            )
        }

        entry<AppRoute.BiometricSetup> { key ->
            BiometricSetupScreen(
                username = key.username,
                password = key.password,
                goToHome = goToHome,
            )
        }

        entry<AppRoute.Settings> { key ->
            SettingsScreen()
        }
    }

    private fun EntryProviderScope<NavKey>.privateNav(
        openNavigationDrawer: () -> Unit,
        goToSettings: () -> Unit,
        goToTransactions: () -> Unit,
        goToAccounts: () -> Unit,
        goToBudgets: () -> Unit,
    ) {
        entry<AppRoute.Home> { key ->
            DashboardScreen(
                openNavigationDrawer = openNavigationDrawer,
                goToSettings = goToSettings,
                goToTransactions = goToTransactions,
                goToAccounts = goToAccounts,
                goToBudgets = goToBudgets,
            )
        }
        entry<AppRoute.Transactions> { key ->
            TransactionListScreen(
                openNavigationDrawer = openNavigationDrawer,
                goToSettings = goToSettings,
            )
        }
    }
}
