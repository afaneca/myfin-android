package com.afaneca.myfin.data.model

import com.afaneca.myfin.domain.model.Transaction
import com.afaneca.myfin.domain.model.TransactionType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransactionDto(
    @Json(name = "account_from_name")
    val accountFromName: String?,
    @Json(name = "account_to_name")
    val accountToName: String?,
    @Json(name = "accounts_account_from_id")
    val accountsAccountFromId: String?,
    @Json(name = "accounts_account_to_id")
    val accountsAccountToId: String?,
    @Json(name = "amount")
    val amount: String,
    @Json(name = "categories_category_id")
    val categoriesCategoryId: String?,
    @Json(name = "category_name")
    val categoryName: String?,
    @Json(name = "date_timestamp")
    val dateTimestamp: Long,
    @Json(name = "description")
    val description: String,
    @Json(name = "entity_id")
    val entityId: String?,
    @Json(name = "entity_name")
    val entityName: String?,
    @Json(name = "transaction_id")
    val transactionId: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "is_essential")
    val isEssential: Int,
)

fun TransactionDto.toDomain(currency: String? = null): Transaction {
    return Transaction(
        accountFromName = accountFromName,
        accountToName = accountToName,
        accountsAccountFromId = accountsAccountFromId,
        accountsAccountToId = accountsAccountToId,
        amount = amount.toBigDecimal(),
        categoriesCategoryId = categoriesCategoryId,
        categoryName = categoryName,
        dateTimestamp = dateTimestamp,
        description = description,
        entityId = entityId,
        entityName = entityName,
        transactionId = transactionId,
        type = TransactionType.fromString(type),
        isEssential = isEssential == 1,
        currency = currency,
    )
}
