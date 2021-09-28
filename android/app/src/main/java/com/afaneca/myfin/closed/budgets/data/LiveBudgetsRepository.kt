package com.afaneca.myfin.closed.budgets.data

import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices

class LiveBudgetsRepository(
    private val api: MyFinAPIServices,
    private val userData: UserDataManager
) : BudgetsRepository, BaseRepository() {
    override suspend fun getBudgetsList() = safeAPICall { api.getBudgetsList() }
    override suspend fun getBudgetDetails(id: String) = safeAPICall { api.getBudgetDetails(id) }
}
