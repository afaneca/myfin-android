package com.afaneca.myfin.data

import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.domain.repository.DashboardRepository

class LiveDashboardRepository(
    private val api: MyFinAPIServices,
    private val userData: UserDataManager
) : DashboardRepository, BaseRepository() {

    override suspend fun getMonthlyExpensesIncomeDistribution(
        month: Int,
        year: Int
    ) = safeAPICall {
        api.getMonthlyExpensesIncomeDistribution(month, year)
    }
}