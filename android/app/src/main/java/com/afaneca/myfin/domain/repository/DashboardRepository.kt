package com.afaneca.myfin.domain.repository

import com.afaneca.myfin.data.model.MonthlyIncomeExpensesDistributionResponse
import com.afaneca.myfin.data.network.Resource

interface DashboardRepository {
    suspend fun getMonthlyExpensesIncomeDistribution(
        month: Int,
        year: Int
    ): Resource<MonthlyIncomeExpensesDistributionResponse>
}
