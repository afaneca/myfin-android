package com.afaneca.myfin.ui.transactions.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afaneca.myfin.R
import com.afaneca.myfin.domain.model.Transaction
import com.afaneca.myfin.domain.model.TransactionType
import com.afaneca.myfin.domain.model.isExpense
import com.afaneca.myfin.ui.common.MFPrimaryButton
import com.afaneca.myfin.ui.common.MFSecondaryButton
import com.afaneca.myfin.ui.theme.AppTheme
import com.afaneca.myfin.ui.theme.LocalSpacing
import com.afaneca.myfin.ui.utils.SigningBehaviour
import com.afaneca.myfin.ui.utils.formatAsCurrency
import com.afaneca.myfin.ui.utils.toDisplayDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsBottomSheet(
    transaction: Transaction,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onEditClicked: (Transaction) -> Unit,
    onRemoveClicked: (Transaction) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Header(transaction)
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item { Spacer(modifier = Modifier.height(LocalSpacing.current.large)) }
                item {
                    DetailItem(
                        label = stringResource(R.string.transactions_sender_account),
                        value = transaction.accountFromName ?: stringResource(id = R.string.account_external)
                    )
                }
                item {
                    DetailItem(
                        label = stringResource(R.string.transactions_destination_account),
                        value = transaction.accountToName ?: stringResource(id = R.string.account_external)
                    )
                }
                item {
                    DetailItem(
                        label = stringResource(R.string.category),
                        value = transaction.categoryName ?: "-"
                    )
                }
                item {
                    DetailItem(
                        label = stringResource(R.string.entity),
                        value = transaction.entityName ?: "-"
                    )
                }
                item { Spacer(modifier = Modifier.height(LocalSpacing.current.large)) }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = LocalSpacing.current.large)
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(LocalSpacing.current.default)
                    ) {
                        MFSecondaryButton.Negative(
                            text = stringResource(id = R.string.delete),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = { onRemoveClicked(transaction) },
                        )
                        MFPrimaryButton(
                            text = stringResource(id = R.string.edit),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = { onEditClicked(transaction) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(LocalSpacing.current.large)) }
            }
        }
    }
}

@Composable
private fun Header(transaction: Transaction) {
    val amountContentColor = when (transaction.type) {
        TransactionType.INCOME -> MaterialTheme.colorScheme.tertiary
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val signingBehaviour = when (transaction.type) {
        TransactionType.INCOME -> SigningBehaviour.ALL
        TransactionType.EXPENSE -> SigningBehaviour.NEGATIVE_ONLY
        else -> SigningBehaviour.NONE
    }

    val amount = if (transaction.isExpense()) transaction.amount.negate() else transaction.amount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LocalSpacing.current.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (transaction.isEssential) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = Color.Transparent,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = LocalSpacing.current.small,
                        vertical = LocalSpacing.current.extraSmall
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.bookmark_star_24),
                        contentDescription = stringResource(id = R.string.essential),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(LocalSpacing.current.small))
                    Text(
                        text = stringResource(id = R.string.essential),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(LocalSpacing.current.small))
        }

        Surface(
            shape = MaterialTheme.shapes.small,
            color = amountContentColor.copy(alpha = 0.15f),
        ) {
            Box(
                modifier = Modifier.padding(
                    horizontal = LocalSpacing.current.small,
                )
            ) {
                Text(
                    text = amount.formatAsCurrency(transaction.currency, signingBehaviour),
                    style = MaterialTheme.typography.displaySmall,
                    color = amountContentColor
                )
            }
        }
        Spacer(modifier = Modifier.height(LocalSpacing.current.small))
        Text(
            text = transaction.description,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(LocalSpacing.current.small))
        Text(
            text = transaction.dateTimestamp.toLong().toDisplayDate(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = LocalSpacing.current.large,
                vertical = LocalSpacing.current.default
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.4f),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(LocalSpacing.current.default))
        Text(
            text = value,
            modifier = Modifier.weight(0.6f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TransactionDetailsBottomSheetPreview() {
    AppTheme {
        TransactionDetailsBottomSheet(
            transaction = Transaction(
                accountFromName = "Account From bla bla bla bla bla bla bla bla",
                accountToName = "Account To bla bla bla bla bla bla bla",
                accountsAccountFromId = "1",
                accountsAccountToId = "2",
                amount = 100432.29.toBigDecimal(),
                categoriesCategoryId = "1",
                categoryName = "Category 1 bla bla bla bla bla bla bla bla bla bla",
                dateTimestamp = 1672531200,
                description = "This is a transaction description that is a bit long to see how it wraps bla bla bla bla bla bla bla bla bla bla",
                entityId = "1",
                entityName = "Entidade 1 bla bla bla bla bla bla bla bla bla bla bla bla bla",
                transactionId = 1,
                type = TransactionType.EXPENSE,
                isEssential = true,
                currency = "EUR",
            ),
            sheetState = rememberModalBottomSheetState(),
            onDismiss = {},
            onEditClicked = {},
            onRemoveClicked = {}
        )
    }
}
