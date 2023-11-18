package com.afaneca.myfin.base.objects

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Created by me on 26/12/2020
 */
@Keep
data class MyFinCategory(
    @SerializedName("users_user_id")
    val userID: Int,
    @SerializedName("category_id")
    val categoryID: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("budgets_budget_id")
    val budgetID: Int,
    @SerializedName("color_gradient")
    val colorGradient: String,
    @SerializedName("current_amount")
    val currentAmount: String,
    @SerializedName("current_amount_credit")
    val currentAmountCredit: String,
    @SerializedName("current_amount_debit")
    val currentAmountDebit: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("planned_amount_credit")
    val plannedAmountCredit: String,
    @SerializedName("planned_amount_debit")
    val plannedAmountDebit: String,
    @SerializedName("exclude_from_budgets")
    val excludeFromBudgets: Int,
)