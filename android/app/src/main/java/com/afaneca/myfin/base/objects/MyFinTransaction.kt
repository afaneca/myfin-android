package com.afaneca.myfin.base.objects


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class MyFinTransaction(
    @SerializedName("account_from_name")
    val accountFromName: String?,
    @SerializedName("account_to_name")
    val accountToName: String?,
    @SerializedName("accounts_account_from_id")
    val accountsAccountFromId: String?,
    @SerializedName("accounts_account_to_id")
    val accountsAccountToId: String?,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("categories_category_id")
    val categoriesCategoryId: String?,
    @SerializedName("category_name")
    val categoryName: String?,
    @SerializedName("date_timestamp")
    val dateTimestamp: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("entity_id")
    val entityId: String?,
    @SerializedName("entity_name")
    val entityName: String?,
    @SerializedName("transaction_id")
    val transactionId: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("is_essential")
    val isEssential: String,
) : Serializable