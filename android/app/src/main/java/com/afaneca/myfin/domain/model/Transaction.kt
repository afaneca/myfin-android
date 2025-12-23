package com.afaneca.myfin.domain.model

import java.math.BigDecimal

data class Transaction(
    val accountFromName: String?,
    val accountToName: String?,
    val accountsAccountFromId: String?,
    val accountsAccountToId: String?,
    val amount: BigDecimal,
    val categoriesCategoryId: String?,
    val categoryName: String?,
    val dateTimestamp: Long,
    val description: String,
    val entityId: String?,
    val entityName: String?,
    val transactionId: Int,
    val type: TransactionType,
    val isEssential: Boolean,
    val currency: String? = null,
)

fun Transaction.isExpense(): Boolean {
    return type == TransactionType.EXPENSE
}

enum class TransactionType(val value: String) {
    INCOME("I"),
    EXPENSE("E"),
    TRANSFER("T");

    companion object {
        fun fromString(value: String?): TransactionType {
            return TransactionType.entries.find { it.value == value } ?: EXPENSE
        }
    }
}
