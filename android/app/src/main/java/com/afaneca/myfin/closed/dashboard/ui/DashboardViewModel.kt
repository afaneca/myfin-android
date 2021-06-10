package com.afaneca.myfin.closed.dashboard.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.closed.dashboard.data.DashboardRepository
import com.afaneca.myfin.closed.dashboard.data.MonthlyIncomeExpensesDistributionResponse
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.open.login.data.AttemptLoginResponse
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _monthlyIncomeExpensesDistributionData: MutableLiveData<Resource<MonthlyIncomeExpensesDistributionResponse>> =
        MutableLiveData()
    val monthlyIncomeExpensesDistributionData: LiveData<Resource<MonthlyIncomeExpensesDistributionResponse>>
        get() = _monthlyIncomeExpensesDistributionData

    fun getMonthlyExpensesIncomeDistribution(month: Int, year: Int) = viewModelScope.launch {
        _monthlyIncomeExpensesDistributionData.value = repository.getMonthlyExpensesIncomeDistribution(
            month, year
        )
    }
}