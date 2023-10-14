package com.afaneca.myfin.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class AddTransactionStep0Response(
    @SerializedName("entities")
    val entities: List<EntityResponse>,
    @SerializedName("categories")
    val categories: List<CategoryResponse>,
    @SerializedName("accounts")
    val accounts: List<AccountResponse>,
)

@Keep
data class EntityResponse(
    @SerializedName("entity_id")
    val entityId: String,
    @SerializedName("name")
    val name: String,
)

@Keep
data class CategoryResponse(
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
)

@Keep
data class AccountResponse(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
)