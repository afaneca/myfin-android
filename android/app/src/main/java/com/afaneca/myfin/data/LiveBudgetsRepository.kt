package com.afaneca.myfin.data

import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.domain.repository.BudgetsRepository

class LiveBudgetsRepository(
    private val api: MyFinAPIServices,
    private val userData: UserDataManager
) : BudgetsRepository, BaseRepository() {
    override suspend fun getBudgetsList(page: Int, pageSize: Int) = safeAPICall { api.getBudgetsList(page, pageSize) }
    override suspend fun getBudgetDetails(id: String) = safeAPICall { api.getBudgetDetails(id) }
    override suspend fun updateBudgetCategoryAmounts(
        budgetId: String,
        catId: String,
        plannedExpenseAmount: String,
        plannedIncomeAmount: String
    ) = safeAPICall {
        api.updateBudgetCategoryPlannedAmounts(
            budgetId, catId, plannedExpenseAmount, plannedIncomeAmount
        )
    }
}
