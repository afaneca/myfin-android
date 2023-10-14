package com.afaneca.myfin.domain.repository

import com.afaneca.myfin.base.objects.MyFinBudget
import com.afaneca.myfin.data.model.BudgetDetailsResponse
import com.afaneca.myfin.data.model.FilteredResultsByPage
import com.afaneca.myfin.data.network.Resource

interface BudgetsRepository {
    suspend fun getBudgetsList(page: Int, pageSize: Int): Resource<FilteredResultsByPage<MyFinBudget>>
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
