package com.afaneca.myfin.closed.dashboard.data

import com.afaneca.myfin.data.network.Resource

interface DashboardRepository {
    suspend fun getMonthlyExpensesIncomeDistribution(
        month: Int,
        year: Int
    ): Resource<MonthlyIncomeExpensesDistributionResponse>
}
