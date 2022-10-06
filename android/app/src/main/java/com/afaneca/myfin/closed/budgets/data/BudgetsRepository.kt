package com.afaneca.myfin.closed.budgets.data

import com.afaneca.myfin.closed.transactions.data.BudgetDetailsResponse
import com.afaneca.myfin.data.network.Resource

interface BudgetsRepository {
    suspend fun getBudgetsList(): Resource<BudgetsListResponse>
    suspend fun getBudgetDetails(
        id: String
    ): Resource<BudgetDetailsResponse>

    suspend fun updateBudgetCategoryAmounts(
        budgetId: String,
        catId: String,
        plannedExpenseAmount: String,
        plannedIncomeAmount: String
    ): Resource<Unit>
}
