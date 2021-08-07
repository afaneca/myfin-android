package com.afaneca.myfin.base.objects


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MyFinBudget(
    @SerializedName("balance_change_percentage")
    val balanceChangePercentage: Double,
    @SerializedName("balance_value")
    val balanceValue: Double,
    @SerializedName("budget_id")
    val budgetId: String,
    @SerializedName("credit_amount")
    val creditAmount: Double,
    @SerializedName("debit_amount")
    val debitAmount: Double,
    @SerializedName("initial_balance")
    val initialBalance: String,
    @SerializedName("is_open")
    val isOpen: String,
    @SerializedName("month")
    val month: String,
    @SerializedName("observations")
    val observations: String,
    @SerializedName("users_user_id")
    val usersUserId: String,
    @SerializedName("year")
    val year: String
)