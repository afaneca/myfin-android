package com.afaneca.myfin.closed.dashboard.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.closed.dashboard.data.DashboardRepository
import com.afaneca.myfin.closed.dashboard.data.MonthlyIncomeExpensesDistributionResponse
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.utils.DateTimeUtils
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

    private val _lastUpdateTimestampFormatted: MutableLiveData<String> = MutableLiveData()
    val lastUpdateTimestampFormatted: LiveData<String> get() = _lastUpdateTimestampFormatted

    private val _expensesDistributionChartData: MutableLiveData<MutableMap<String, Double>> =
        MutableLiveData(mutableMapOf())
    val expensesDistributionChartData: LiveData<MutableMap<String, Double>>
        get() = _expensesDistributionChartData

    private val _incomeDistributionChartData: MutableLiveData<MutableMap<String, Double>> =
        MutableLiveData(mutableMapOf())
    val incomeDistributionChartData: LiveData<MutableMap<String, Double>>
        get() = _incomeDistributionChartData

    fun requestMonthlyExpensesIncomeDistribution(month: Int, year: Int) = viewModelScope.launch {
        _monthlyIncomeExpensesDistributionData.value =
            repository.getMonthlyExpensesIncomeDistribution(
                month, year
            )
        if (_monthlyIncomeExpensesDistributionData.value is Resource.Success<MonthlyIncomeExpensesDistributionResponse>) {
            val response =
                (_monthlyIncomeExpensesDistributionData.value as Resource.Success<MonthlyIncomeExpensesDistributionResponse>).data
            _monthlyOverviewChartData.postValue(generateMonthlyOverviewChartDataObject(response))
            _lastUpdateTimestampFormatted.postValue(
                DateTimeUtils.getFormattedDateTimeFromUnixTime(
                    response.lastUpdateTimestamp
                )
            )
        }

    }


    private fun generateMonthlyOverviewChartDataObject(response: MonthlyIncomeExpensesDistributionResponse): MonthlyOverviewChartData {
        var plannedAmount = 0.00
        var currentAmount = 0.00

        val incomeMap: MutableMap<String, Double> = mutableMapOf()
        val expensesMap: MutableMap<String, Double> = mutableMapOf()

        for (cat in response.categories) {
            plannedAmount += cat.plannedAmountDebit.toDoubleOrNull() ?: 0.00
            currentAmount += cat.currentAmountDebit.toDoubleOrNull() ?: 0.00

            if (cat.currentAmountDebit != "0" && cat.currentAmountDebit != "0.00") {
                expensesMap.put(
                    cat.name,
                    cat.currentAmountDebit.toDoubleOrNull() ?: 0.00
                )
            }
            if (cat.currentAmountCredit != "0" && cat.currentAmountCredit != "0.00") {
                incomeMap.put(
                    cat.name,
                    cat.currentAmountCredit.toDoubleOrNull() ?: 0.00
                )
            }
        }
        _expensesDistributionChartData.postValue(expensesMap)
        _incomeDistributionChartData.postValue(incomeMap)

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
