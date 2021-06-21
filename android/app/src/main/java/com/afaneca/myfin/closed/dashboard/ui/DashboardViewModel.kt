package com.afaneca.myfin.closed.dashboard.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.StringUtil
import com.afaneca.myfin.closed.dashboard.data.DashboardRepository
import com.afaneca.myfin.closed.dashboard.data.MonthlyIncomeExpensesDistributionResponse
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.utils.SingleLiveEvent
import com.afaneca.myfin.utils.formatMoney
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _monthlyIncomeExpensesDistributionData: SingleLiveEvent<Resource<MonthlyIncomeExpensesDistributionResponse>> =
        SingleLiveEvent()

    fun getMonthlyIncomeExpensesDistributionData(): SingleLiveEvent<Resource<MonthlyIncomeExpensesDistributionResponse>> =
        _monthlyIncomeExpensesDistributionData

    private val _monthlyOverviewChartData: MutableLiveData<MonthlyOverviewChartData> =
        MutableLiveData()
    val monthlyOverviewChartData: LiveData<MonthlyOverviewChartData>
        get() = _monthlyOverviewChartData


    fun requestMonthlyExpensesIncomeDistribution(month: Int, year: Int) = viewModelScope.launch {
        _monthlyIncomeExpensesDistributionData.value =
            repository.getMonthlyExpensesIncomeDistribution(
                month, year
            )
        if (_monthlyIncomeExpensesDistributionData.value is Resource.Success<MonthlyIncomeExpensesDistributionResponse>) {
            _monthlyOverviewChartData.postValue(
                generateMonthlyOverviewChartDataObject(
                    (_monthlyIncomeExpensesDistributionData.value as Resource.Success<MonthlyIncomeExpensesDistributionResponse>).data
                )
            )
        }

    }


    private fun generateMonthlyOverviewChartDataObject(response: MonthlyIncomeExpensesDistributionResponse): DashboardViewModel.MonthlyOverviewChartData {
        var plannedAmount = 0.00
        var currentAmount = 0.00

        for (cat in response.categories) {
            plannedAmount += cat.plannedAmountDebit.toDoubleOrNull() ?: 0.00
            currentAmount += cat.currentAmountDebit.toDoubleOrNull() ?: 0.00
        }

        return MonthlyOverviewChartData(
            formatMoney(currentAmount),
            formatMoney(plannedAmount),
            currentAmount / plannedAmount
        )
    }

    data class MonthlyOverviewChartData(
        val currentAmount: String,
        val plannedAmount: String,
        val progressValue: Double
    )
}
