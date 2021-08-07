package com.afaneca.myfin.closed.budgets.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.closed.budgets.data.BudgetsListResponse
import com.afaneca.myfin.closed.budgets.data.BudgetsRepository
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class BudgetsViewModel(
    private val repository: BudgetsRepository
) : ViewModel() {

    private val _budgetsListData: SingleLiveEvent<Resource<BudgetsListResponse>> = SingleLiveEvent()
    fun getBudgetsListData() = _budgetsListData

    fun requestBudgetsList() = viewModelScope.launch {
        _budgetsListData.value = Resource.Loading
        _budgetsListData.value = repository.getBudgetsList()
        if (_budgetsListData.value is Resource.Success<BudgetsListResponse>) {
            val budgetsList = (_budgetsListData.value as Resource.Success<BudgetsListResponse>)
            _budgetsListData.postValue(budgetsList)
        }
    }
}
