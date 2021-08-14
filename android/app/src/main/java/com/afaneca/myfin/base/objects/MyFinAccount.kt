package com.afaneca.myfin.base.objects


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MyFinAccount(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("balance")
    val balance: String,
    @SerializedName("color_gradient")
    val colorGradient: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("exclude_from_budgets")
    val excludeFromBudgets: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("users_user_id")
    val usersUserId: String
)