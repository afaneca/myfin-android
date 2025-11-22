package com.afaneca.myfin.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.afaneca.myfin.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MFScaffold(
    title: String,
    navigationIcon: @Composable (() -> Unit) = {},
    snackbarHost: @Composable (() -> Unit) = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                actions = actions,
                navigationIcon = navigationIcon,
            )
        },
        snackbarHost = snackbarHost,
    ) { padding ->
        content(padding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MFScaffoldWithNavDrawer(
    title: String,
    onNavigationIconClicked: () -> Unit = {},
    snackbarHost: @Composable (() -> Unit) = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    MFScaffold(
        title = title,
        navigationIcon = {
            IconButton(onClick = onNavigationIconClicked) {
                Icon(
                    painter = painterResource(R.drawable.menu_24),
                    contentDescription = stringResource(R.string.go_back),
                )
            }
        },
        snackbarHost = snackbarHost,
        actions = actions,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MFScaffoldWithBackNav(
    title: String,
    onNavigationIconClicked: () -> Unit = {},
    snackbarHost: @Composable (() -> Unit) = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    MFScaffold(
        title = title,
        navigationIcon = {
            IconButton(onClick = onNavigationIconClicked) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back_24),
                    contentDescription = stringResource(R.string.go_back),
                )
            }
        },
        snackbarHost = snackbarHost,
        actions = actions,
        content = content,
    )
}

@Composable
fun MFNoToolbarScaffold(
    snackbarHost: @Composable (() -> Unit) = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        snackbarHost = snackbarHost,
    ) { padding ->
        content(padding)
    }
}