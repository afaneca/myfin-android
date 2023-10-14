package com.afaneca.myfin.data.model


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BudgetDetailsResponse(
    @SerializedName("categories")
    val myFinBudgetCategories: List<MyFinBudgetCategory>,
    @SerializedName("initial_balance")
    val initialBalance: Double,
    @SerializedName("month")
    val month: String,
    @SerializedName("observations")
    val observations: String,
    @SerializedName("year")
    val year: String
)