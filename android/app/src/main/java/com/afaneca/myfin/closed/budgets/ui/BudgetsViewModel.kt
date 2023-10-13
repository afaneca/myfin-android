package com.afaneca.myfin.closed.budgets.ui

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.base.objects.MyFinBudget
import com.afaneca.myfin.data.model.FilteredResultsByPage
import com.afaneca.myfin.domain.repository.BudgetsRepository
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val BUDGET_PAGE_SIZE = 15
@HiltViewModel
class BudgetsViewModel
@Inject
constructor(
    private val repository: BudgetsRepository
) : ViewModel() {

    private val _budgetsListData: SingleLiveEvent<Resource<FilteredResultsByPage<MyFinBudget>>> = SingleLiveEvent()
    private val _budgestListDataset: MutableLiveData<List<MyFinBudget>> = MutableLiveData(ArrayList())
    val budgetsListDataset: LiveData<List<MyFinBudget>>
        get() = _budgestListDataset

    private var _currentPage = 0
    private val _hasMore = MutableLiveData(true)

    fun getBudgetsListData() = _budgetsListData

    @SuppressLint("NullSafeMutableLiveData")
    fun clearData() {
        _currentPage = 0
        _hasMore.value = true
        _budgetsListData.value = null
        _budgestListDataset.value = ArrayList()
    }

    fun isPaginating() = _currentPage != 0
    fun requestBudgets() {
        clearData()
        requestBudgetsList(0)
    }

    fun requestMoreBudgets() {
        if(!_hasMore.value!!){
            return
        }
        requestBudgetsList(++_currentPage)
    }

    private fun requestBudgetsList(page: Int) = viewModelScope.launch {
        _budgetsListData.value = Resource.Loading
        _budgetsListData.value = repository.getBudgetsList(page,  BUDGET_PAGE_SIZE)
        if (_budgetsListData.value is Resource.Success<FilteredResultsByPage<MyFinBudget>>) {
            val newItems = (_budgetsListData.value as Resource.Success<FilteredResultsByPage<MyFinBudget>>).data.results
            _hasMore.value = newItems.isNotEmpty()
            val aggregatedList = _budgestListDataset.value!! + newItems
            _budgestListDataset.value = aggregatedList
        }
    }
}
