package com.afaneca.myfin.closed.dashboard.data

import com.afaneca.myfin.data.UserDataStore
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices

class DashboardRepository(
    private val api: MyFinAPIServices,
    private val userData: UserDataStore
) : BaseRepository() {

    suspend fun getMonthlyExpensesIncomeDistribution(
        month: Int,
        year: Int
    ) = safeAPICall {
        api.getMonthlyExpensesIncomeDistribution(month, year)
    }
}