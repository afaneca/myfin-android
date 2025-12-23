package com.afaneca.myfin.ui.transactions.list

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.afaneca.myfin.R
import com.afaneca.myfin.domain.model.Transaction
import com.afaneca.myfin.domain.model.TransactionType
import com.afaneca.myfin.domain.model.isExpense
import com.afaneca.myfin.ui.main.MainContract.LocalLoadingState
import com.afaneca.myfin.ui.theme.AppTheme
import com.afaneca.myfin.ui.theme.LocalSpacing
import com.afaneca.myfin.ui.utils.SigningBehaviour
import com.afaneca.myfin.ui.utils.formatAsCurrency
import com.afaneca.myfin.ui.utils.toDisplayDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListContent(
    uiState: TransactionListContract.MainState,
    modifier: Modifier = Modifier,
    onQueryUpdated: (String) -> Unit,
    onEndOfListReached: () -> Unit,
    onClearQueryClicked: () -> Unit,
    onTransactionClicked: (Transaction) -> Unit,
) {
    val loadingState = LocalLoadingState.current

    val listState = rememberLazyListState()
    val reachedBottomOfList = !listState.canScrollForward

    LaunchedEffect(reachedBottomOfList) {
        if (uiState.canRequestMoreItems && reachedBottomOfList) onEndOfListReached()
    }

    LaunchedEffect(uiState.isLoading) {
        loadingState.setLoading(uiState.isLoading)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = LocalSpacing.current.large),
    ) {
        // Search bar
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            inputField = {
                SearchBarDefaults.InputField(
                    query = uiState.query,
                    onQueryChange = { query ->
                        onQueryUpdated(query)
                    },
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = {
                        Text("Search")
                    },
                    onSearch = {},
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search_24),
                            contentDescription = null,
                        )
                    },
                    trailingIcon = {
                        if (!uiState.query.isEmpty()) {
                            Icon(
                                modifier = Modifier.clickable { onClearQueryClicked() },
                                painter = painterResource(R.drawable.close_24),
                                contentDescription = null,
                            )
                        }
                    },

                    )
            },
            expanded = false,
            onExpandedChange = { },
        ) {}
        if (uiState.groupedItems.isNullOrEmpty()) {
            EmptyView()
        } else {
            Spacer(modifier = Modifier.height(LocalSpacing.current.small))
            uiState.groupedItems.let { groupedList ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                ) {
                    groupedList.forEach { (date, transactionList) ->
                        stickyHeader(contentType = String) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.background),
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            vertical = LocalSpacing.current.small,
                                            horizontal = LocalSpacing.current.default,
                                        ),
                                    style = MaterialTheme.typography.labelMedium,
                                    text = date,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        items(
                            items = transactionList,
                            key = { trx -> trx.transactionId },
                        ) { transaction ->
                            val isFirst = transactionList.first() == transaction
                            val isLast = transactionList.last() == transaction
                            if (isFirst) Spacer(modifier = Modifier.height(LocalSpacing.current.small))
                            Column {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    tonalElevation = 1.dp,
                                    shadowElevation = 0.dp,
                                    shape = when {
                                        transactionList.size == 1 -> MaterialTheme.shapes.medium
                                        isFirst -> MaterialTheme.shapes.medium.copy(
                                            bottomStart = CornerSize(0.dp),
                                            bottomEnd = CornerSize(0.dp),
                                        )

                                        isLast -> MaterialTheme.shapes.medium.copy(
                                            topStart = CornerSize(0.dp),
                                            topEnd = CornerSize(0.dp),
                                        )

                                        else -> RectangleShape
                                    },
                                ) {
                                    TransactionListItem(transaction, onTransactionClicked)
                                }

                                if (!isLast) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.background)
                                } else {
                                    Spacer(modifier = Modifier.height(LocalSpacing.current.small))
                                }
                            }
                        }
                    }
                }
                if (uiState.isLoadingAdditionalPage) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionListItem(
    transaction: Transaction,
    onTransactionClicked: (Transaction) -> Unit = {},
) {
    // Define content and icon colors based on transaction type
    val amountContentColor = when (transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.tertiary
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val iconColor = when (transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.tertiary
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    val amountBorder = when (transaction.type) {
        TransactionType.INCOME, TransactionType.EXPENSE -> BorderStroke(
            1.dp,
            amountContentColor.copy(alpha = 0.5f),
        )

        TransactionType.TRANSFER -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    }

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onTransactionClicked(transaction)
            },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        overlineContent = {
            Text(if (transaction.entityName.isNullOrBlank()) stringResource(R.string.no_entity) else transaction.entityName)
        },
        headlineContent = {
            Text(
                transaction.description.ifBlank { "(${stringResource(R.string.no_description)})" },
            )
        },
        supportingContent = {
            Text(if (transaction.categoryName.isNullOrBlank()) stringResource(R.string.no_entity) else transaction.categoryName)
        },
        leadingContent = {
            Column {
                Icon(
                    painter = painterResource(
                        id = when (transaction.type) {
                            TransactionType.INCOME -> R.drawable.ic_arrow_upward
                            TransactionType.EXPENSE -> R.drawable.ic_arrow_downward
                            TransactionType.TRANSFER -> R.drawable.ic_swap_horiz
                        },
                    ),
                    contentDescription = stringResource(R.string.settings),
                    tint = iconColor,
                )

                if (transaction.isEssential) {
                    Icon(
                        painter = painterResource(R.drawable.bookmark_star_24),
                        contentDescription = stringResource(R.string.settings),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        trailingContent = {
            AmountValue(
                transaction = transaction,
                contentColor = amountContentColor,
                border = amountBorder,
            )
        },
    )
}

@Composable
private fun AmountValue(
    transaction: Transaction,
    contentColor: Color,
    border: BorderStroke?,
) {
    val signingBehaviour = when (transaction.type) {
        TransactionType.INCOME -> SigningBehaviour.ALL
        TransactionType.EXPENSE -> SigningBehaviour.NEGATIVE_ONLY
        else -> SigningBehaviour.NONE
    }

    val amount = transaction.amount.let {
        if (transaction.isExpense()) it.negate()
        else it
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent,
        border = border,
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = LocalSpacing.current.small,
                vertical = LocalSpacing.current.extraSmall,
            ),
        ) {
            Text(
                style = MaterialTheme.typography.labelLarge,
                text = amount.formatAsCurrency(
                    currency = transaction.currency,
                    signingBehaviour = signingBehaviour,
                ),
                color = contentColor,
            )
        }
    }
}

@Composable
private fun EmptyView() {
    Spacer(modifier = Modifier.height(LocalSpacing.current.large))
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.no_transactions),
        textAlign = TextAlign.Center,
    )
}

@Preview(
    showBackground = true,
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun TransactionListPreview() {
    AppTheme {
        TransactionListContent(
            uiState = TransactionListContract.MainState(
                query = "query text",
                isLoadingAdditionalPage = true,
                groupedItems = listOf(
                    Transaction(
                        accountFromName = "Account From",
                        accountToName = "Account To",
                        accountsAccountFromId = "1",
                        accountsAccountToId = "2",
                        amount = 100.toBigDecimal(),
                        categoriesCategoryId = "1",
                        categoryName = "Category 1",
                        dateTimestamp = 1,
                        description = "Description 1",
                        entityId = "1",
                        entityName = "Entidade 1",
                        transactionId = 1,
                        type = TransactionType.INCOME,
                        isEssential = false,
                        currency = "EUR",
                    ),
                    Transaction(
                        accountFromName = "Account From",
                        accountToName = "Account To",
                        accountsAccountFromId = "1",
                        accountsAccountToId = "2",
                        amount = 100.toBigDecimal(),
                        categoriesCategoryId = "1",
                        categoryName = "",
                        dateTimestamp = 1,
                        description = "",
                        entityId = "1",
                        entityName = "",
                        transactionId = 2,
                        type = TransactionType.INCOME,
                        isEssential = false,
                        currency = "EUR",
                    ),
                    Transaction(
                        accountFromName = "Account From",
                        accountToName = "Account To",
                        accountsAccountFromId = "1",
                        accountsAccountToId = "2",
                        amount = 100.toBigDecimal(),
                        categoriesCategoryId = "1",
                        categoryName = "Category 1",
                        dateTimestamp = 1,
                        description = "Description 3",
                        entityId = "1",
                        entityName = "Entidade 1",
                        transactionId = 3,
                        type = TransactionType.EXPENSE,
                        isEssential = true,
                        currency = "EUR",
                    ),

                    Transaction(
                        accountFromName = "Account From",
                        accountToName = "Account To",
                        accountsAccountFromId = "1",
                        accountsAccountToId = "2",
                        amount = 100.toBigDecimal(),
                        categoriesCategoryId = "1",
                        categoryName = "Category 1",
                        dateTimestamp = 1000000,
                        description = "Description 4",
                        entityId = "1",
                        entityName = "Entidade 1",
                        transactionId = 4,
                        type = TransactionType.TRANSFER,
                        isEssential = false,
                        currency = "EUR",
                    ),
                ).groupBy { it.dateTimestamp.toDisplayDate() },
            ),
            modifier = Modifier,
            onQueryUpdated = {},
            onEndOfListReached = {},
            onTransactionClicked = {},
            onClearQueryClicked = {},
        )
    }
}
