package com.afaneca.myfin.ui.transactions.add

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afaneca.myfin.R
import com.afaneca.myfin.domain.model.AccountType
import com.afaneca.myfin.domain.model.Category
import com.afaneca.myfin.domain.model.Entity
import com.afaneca.myfin.domain.model.TransactionType
import com.afaneca.myfin.domain.model.UserAccount
import com.afaneca.myfin.ui.common.MFPrimaryButton
import com.afaneca.myfin.ui.common.MFSecondaryButton
import com.afaneca.myfin.ui.common.MFTextInput
import com.afaneca.myfin.ui.theme.AppTheme
import com.afaneca.myfin.ui.theme.LocalSpacing
import com.afaneca.myfin.ui.utils.toDisplayDate
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    uiState: AddTransactionContract.MainState,
    modifier: Modifier = Modifier,
    onTypeUpdated: (TransactionType) -> Unit,
    onDateUpdated: (Long) -> Unit,
    onAmountUpdated: (BigDecimal?) -> Unit,
    onAccountFromUpdated: (UserAccount?) -> Unit,
    onAccountToUpdated: (UserAccount?) -> Unit,
    onCategoryUpdated: (Category?) -> Unit,
    onEntityUpdated: (Entity?) -> Unit,
    onDescriptionUpdated: (String) -> Unit,
    onIsEssentialUpdated: (Boolean) -> Unit,
    onAutoCategorizeClicked: () -> Unit,
    onSaveClicked: () -> Unit,
) {
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

    var amountInput by rememberSaveable(uiState.amount?.toPlainString()) {
        mutableStateOf(uiState.amount?.toPlainString().orEmpty())
    }
    var descriptionInput by rememberSaveable(uiState.description) {
        mutableStateOf(uiState.description.orEmpty())
    }

    val isAmountInvalid = amountInput.isNotBlank() && parseAmount(amountInput) == null
    val isAutoCategorizeEnabled =
        !uiState.isLoadingAutoCategorization && descriptionInput.isNotBlank() && uiState.amount != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
    ) {
        // Header
        Text(
            text = stringResource(R.string.add_transaction),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                start = spacing.medium,
                end = spacing.medium,
                top = spacing.small,
                bottom = spacing.medium,
            ),
        )

        // Transaction type selector
        TransactionTypeInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.medium),
            selectedType = uiState.type,
            onTypeSelected = onTypeUpdated,
        )

        // Animated accent strip below type selector
        val accentColor by animateColorAsState(
            targetValue = when (uiState.type) {
                TransactionType.INCOME -> MaterialTheme.colorScheme.tertiary
                TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                TransactionType.TRANSFER -> MaterialTheme.colorScheme.secondary
                null -> MaterialTheme.colorScheme.surfaceVariant
            },
            animationSpec = tween(durationMillis = 300),
            label = "typeAccentColor",
        )
        Box(
            modifier = Modifier
                .padding(horizontal = spacing.medium)
                .padding(top = 6.dp)
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(accentColor.copy(alpha = 0.6f)),
        )

        Spacer(modifier = Modifier.height(spacing.medium))

        // Form data loading indicator
        AnimatedVisibility(
            visible = uiState.isLoadingFormData,
            modifier = Modifier.padding(horizontal = spacing.medium),
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
            )
        }

        // Amount & Date section
        FormSection(modifier = Modifier.padding(horizontal = spacing.medium)) {
            MFTextInput(
                modifier = Modifier.fillMaxWidth(),
                value = amountInput,
                onValueChange = { input ->
                    amountInput = input
                    onAmountUpdated(parseAmount(input))
                },
                label = stringResource(R.string.amount),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                supportingText = {
                    if (isAmountInvalid) {
                        Text(text = stringResource(R.string.error_invalid_amount))
                    }
                },
                isError = isAmountInvalid,
            )
            TransactionDateInput(
                selectedDate = uiState.date,
                onDateSelected = onDateUpdated,
            )
        }

        // Accounts section
        AnimatedVisibility(visible = uiState.isAccountFromVisible || uiState.isAccountToVisible) {
            FormSection(
                modifier = Modifier.padding(
                    start = spacing.medium,
                    end = spacing.medium,
                    top = spacing.medium,
                ),
                title = stringResource(R.string.accounts),
            ) {
                if (uiState.isAccountFromVisible) {
                    SearchableDropdownField(
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.transactions_sender_account),
                        selectedItem = uiState.accountFrom,
                        items = uiState.availableAccounts,
                        itemLabel = { it.name },
                        onItemSelected = onAccountFromUpdated,
                    )
                }
                if (uiState.isAccountToVisible) {
                    SearchableDropdownField(
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.transactions_destination_account),
                        selectedItem = uiState.accountTo,
                        items = uiState.availableAccounts,
                        itemLabel = { it.name },
                        onItemSelected = onAccountToUpdated,
                    )
                }
            }
        }

        // Details section
        FormSection(
            modifier = Modifier.padding(
                start = spacing.medium,
                end = spacing.medium,
                top = spacing.medium,
            ),
            title = stringResource(R.string.details),
        ) {
            SearchableDropdownField(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.category),
                selectedItem = uiState.category,
                items = uiState.availableCategories,
                itemLabel = { it.name.orEmpty() },
                onItemSelected = onCategoryUpdated,
            )
            SearchableDropdownField(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.entity),
                selectedItem = uiState.entity,
                items = uiState.availableEntities,
                itemLabel = { it.name.orEmpty() },
                onItemSelected = onEntityUpdated,
            )
            MFTextInput(
                modifier = Modifier.fillMaxWidth(),
                value = descriptionInput,
                onValueChange = { input ->
                    descriptionInput = input
                    onDescriptionUpdated(input)
                },
                label = stringResource(R.string.description),
                singleLine = false,
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = spacing.extraSmall),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            // Essential toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.essential),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = stringResource(R.string.essential_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = uiState.isEssential,
                    onCheckedChange = onIsEssentialUpdated,
                )
            }
        }

        // Auto-categorize progress
        AnimatedVisibility(
            visible = uiState.isLoadingAutoCategorization,
            modifier = Modifier.padding(horizontal = spacing.medium),
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.medium)
                    .clip(RoundedCornerShape(50)),
            )
        }

        // Actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = spacing.medium,
                    end = spacing.medium,
                    top = spacing.medium,
                    bottom = spacing.large,
                ),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            MFSecondaryButton(
                text = stringResource(
                    if (uiState.isLoadingAutoCategorization) R.string.auto_categorize_loading
                    else R.string.auto_categorize,
                ),
                isEnabled = isAutoCategorizeEnabled,
                onClick = onAutoCategorizeClicked,
            )
            AnimatedVisibility(
                visible = uiState.isLoading,
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50)),
                )
            }
            MFPrimaryButton(
                text = stringResource(R.string.save),
                isEnabled = uiState.isSubmitButtonEnabled && !uiState.isLoading,
                onClick = onSaveClicked,
            )
        }
    }
}

// Generic searchable dropdown backed by ExposedDropdownMenuBox
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SearchableDropdownField(
    modifier: Modifier = Modifier,
    label: String,
    selectedItem: T?,
    items: List<T>,
    itemLabel: (T) -> String,
    onItemSelected: (T?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var query by rememberSaveable(selectedItem) {
        mutableStateOf(selectedItem?.let { itemLabel(it) }.orEmpty())
    }
    val filtered = remember(query, items) {
        if (query.isBlank()) items
        else items.filter { itemLabel(it).contains(query, ignoreCase = true) }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            value = query,
            onValueChange = { newQuery ->
                query = newQuery
                expanded = true
                if (newQuery.isBlank()) onItemSelected(null)
            },
            label = { Text(label) },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = {
                        query = ""
                        onItemSelected(null)
                        expanded = false
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.close_24),
                            contentDescription = null,
                        )
                    }
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        if (filtered.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                filtered.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemLabel(item)) },
                        onClick = {
                            query = itemLabel(item)
                            onItemSelected(item)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionDateInput(
    selectedDate: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showDatePicker = true
            }
        }
    }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = selectedDate?.toDisplayDate().orEmpty(),
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        label = { Text(text = stringResource(R.string.date)) },
        placeholder = { Text(text = stringResource(R.string.select_date)) },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.calendar_24),
                contentDescription = null,
            )
        },
        interactionSource = interactionSource,
    )

    if (showDatePicker) {
        val initialMillis = (selectedDate ?: (System.currentTimeMillis() / 1000)) * 1000
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(millis / 1000)
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun FormSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = LocalSpacing.current
    androidx.compose.material3.Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            content()
        }
    }
}

@Composable
fun TransactionTypeInput(
    modifier: Modifier = Modifier,
    selectedType: TransactionType?,
    onTypeSelected: (TransactionType) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        TransactionType.entries.forEachIndexed { index, transactionType ->
            SegmentedButton(
                selected = selectedType == transactionType,
                onClick = { onTypeSelected(transactionType) },
                shape = SegmentedButtonDefaults.itemShape(index, TransactionType.entries.size),
                label = { Text(text = stringResource(id = transactionType.labelRes())) },
            )
        }
    }
}

@StringRes
private fun TransactionType.labelRes(): Int {
    return when (this) {
        TransactionType.INCOME -> R.string.transaction_type_income
        TransactionType.EXPENSE -> R.string.transaction_type_expense
        TransactionType.TRANSFER -> R.string.transaction_type_transfer
    }
}

private fun parseAmount(input: String): BigDecimal? {
    if (input.isBlank()) return null
    val normalized = input.replace(',', '.').trim()
    return try {
        BigDecimal(normalized)
    } catch (_: NumberFormatException) {
        null
    }
}

@Preview
@Composable
fun AddTransactionPreview() {
    AppTheme {
        AddTransactionContent(
            uiState = AddTransactionContract.MainState(
                type = TransactionType.EXPENSE,
                date = System.currentTimeMillis() / 1000,
                amount = BigDecimal("23.45"),
                accountFrom = UserAccount(
                    id = 1,
                    name = "Checking",
                    balance = BigDecimal("1234.56"),
                    type = AccountType.CHECKING,
                    description = null,
                    status = null,
                    colorGradient = null,
                    isToExcludeFromBudgets = false,
                ),
                isAccountFromVisible = true,
                availableAccounts = listOf(
                    UserAccount(
                        id = 1,
                        name = "Checking",
                        balance = BigDecimal("1234.56"),
                        type = AccountType.CHECKING,
                        description = null,
                        status = null,
                        colorGradient = null,
                        isToExcludeFromBudgets = false,
                    ),
                ),
                category = Category(
                    categoryId = 1,
                    name = "Groceries",
                    description = null,
                    excludeFromBudgets = false,
                    isActive = true,
                ),
                availableCategories = listOf(
                    Category(categoryId = 1, name = "Groceries", description = null, excludeFromBudgets = false, isActive = true),
                ),
                entity = Entity(entityId = 1, name = "Local Market"),
                availableEntities = listOf(
                    Entity(entityId = 1, name = "Local Market"),
                ),
                description = "Weekly shop",
                isEssential = true,
                isSubmitButtonEnabled = true,
            ),
            modifier = Modifier.fillMaxSize(),
            onTypeUpdated = {},
            onDateUpdated = {},
            onAmountUpdated = {},
            onAccountFromUpdated = {},
            onAccountToUpdated = {},
            onCategoryUpdated = {},
            onEntityUpdated = {},
            onDescriptionUpdated = {},
            onIsEssentialUpdated = {},
            onAutoCategorizeClicked = {},
            onSaveClicked = {},
        )
    }
}
