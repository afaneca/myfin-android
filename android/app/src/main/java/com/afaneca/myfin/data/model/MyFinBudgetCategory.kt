package com.afaneca.myfin.data.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class MyFinBudgetCategory(
    @SerializedName("avg_12_months_credit")
    val avg12MonthsCredit: Double,
    @SerializedName("avg_12_months_debit")
    val avg12MonthsDebit: String,
    @SerializedName("avg_lifetime_credit")
    val avgLifetimeCredit: Double,
    @SerializedName("avg_lifetime_debit")
    val avgLifetimeDebit: String,
    @SerializedName("avg_previous_month_credit")
    val avgPreviousMonthCredit: Double,
    @SerializedName("avg_previous_month_debit")
    val avgPreviousMonthDebit: String,
    @SerializedName("avg_same_month_previous_year_credit")
    val avgSameMonthPreviousYearCredit: Double,
    @SerializedName("avg_same_month_previous_year_debit")
    val avgSameMonthPreviousYearDebit: String,
    @SerializedName("budgets_budget_id")
    val budgetsBudgetId: String,
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("color_gradient")
    val colorGradient: String?,
    @SerializedName("current_amount")
    val currentAmount: String,
    @SerializedName("current_amount_credit")
    val currentAmountCredit: String,
    @SerializedName("current_amount_debit")
    val currentAmountDebit: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("planned_amount_credit")
    val plannedAmountCredit: String,
    @SerializedName("planned_amount_debit")
    val plannedAmountDebit: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("users_user_id")
    val usersUserId: String
) : Serializable