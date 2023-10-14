package com.afaneca.myfin.closed.budgets.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.domain.repository.BudgetsRepository
import com.afaneca.myfin.data.model.BudgetDetailsResponse
import com.afaneca.myfin.data.model.MyFinBudgetCategory
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.utils.DateTimeUtils
import com.afaneca.myfin.utils.SingleLiveEvent
import com.afaneca.myfin.utils.formatMoney
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

/**
 * Created by me on 07/08/2021
 */
@HiltViewModel
class BudgetDetailsViewModel @Inject constructor(
    private val repository: BudgetsRepository
) : ViewModel() {
    enum class TAB { EXPENSES, INCOME }

    var isOpen: Boolean = false
    private var _selectedTab = TAB.EXPENSES
    fun getSelectedTab() = _selectedTab

    private val _budgetDetailsRequest: SingleLiveEvent<Resource<BudgetDetailsResponse>> =
        SingleLiveEvent()

    fun getBudgetDetailsRequest() = _budgetDetailsRequest

    private val _budgetDetailsData: MutableLiveData<BudgetDetailsData> = MutableLiveData()
    val budgetDetailsData: LiveData<BudgetDetailsData> get() = _budgetDetailsData

    fun requestBudgetDetails(id: String) = viewModelScope.launch {
        _selectedTab = TAB.EXPENSES
        _budgetDetailsRequest.value = Resource.Loading
        _budgetDetailsRequest.value = repository.getBudgetDetails(id)
        if (_budgetDetailsRequest.value is Resource.Success<BudgetDetailsResponse>) {
            val budgetDetails =
                (_budgetDetailsRequest.value as Resource.Success<BudgetDetailsResponse>)

            var expensesPlannedAmountTotal = 0.0
            var expensesCurrentAmountTotal = 0.0
            var incomePlannedAmountTotal = 0.0
            var incomeCurrentAmountTotal = 0.0

            for (category in budgetDetails.data.myFinBudgetCategories) {
                expensesPlannedAmountTotal += category.plannedAmountDebit.toDoubleOrNull() ?: 0.00
                expensesCurrentAmountTotal += category.currentAmountDebit.toDoubleOrNull() ?: 0.00
                incomePlannedAmountTotal += category.plannedAmountCredit.toDoubleOrNull() ?: 0.00
                incomeCurrentAmountTotal += category.currentAmountCredit.toDoubleOrNull() ?: 0.00
            }

            _budgetDetailsData.postValue(
                BudgetDetailsData(
                    budgetDetails.data.myFinBudgetCategories,
                    "${
                        DateTimeUtils.convertMonthIntToString(
                            budgetDetails.data.month.toIntOrNull() ?: 1,
                            TextStyle.FULL_STANDALONE
                        ).capitalize(Locale.ROOT)
                    } ${budgetDetails.data.year}",
                    formatMoney(expensesPlannedAmountTotal),
                    formatMoney(expensesCurrentAmountTotal),
                    formatMoney(incomePlannedAmountTotal),
                    formatMoney(incomeCurrentAmountTotal),
                    formatMoney(
                        if (isOpen)
                            incomePlannedAmountTotal - expensesPlannedAmountTotal
                        else
                            incomeCurrentAmountTotal - expensesPlannedAmountTotal
                    ),
                    if (isOpen)
                        incomePlannedAmountTotal - expensesPlannedAmountTotal
                    else
                        incomeCurrentAmountTotal - expensesPlannedAmountTotal,
                    ((expensesCurrentAmountTotal / expensesPlannedAmountTotal) * 100).toInt(),
                    ((incomeCurrentAmountTotal / incomePlannedAmountTotal) * 100).toInt(),
                    isOpen
                )
            )
        }
    }

    fun sortCategoriesList(myFinBudgetCategories: ArrayList<MyFinBudgetCategory>): ArrayList<MyFinBudgetCategory> {
        val isExpenses = _selectedTab == TAB.EXPENSES
        myFinBudgetCategories.sortByDescending {
            if (isExpenses) it.currentAmountDebit.toDoubleOrNull()
                ?: 0.0 else it.currentAmountCredit.toDoubleOrNull() ?: 0.0
        }
        return myFinBudgetCategories
    }

    fun onTabSelected(position: Int) {
        _selectedTab = when (position) {
            0 -> TAB.EXPENSES
            1 -> TAB.INCOME
            else -> TAB.EXPENSES
        }
    }

    data class BudgetDetailsData(
        val myFinBudgetCategories: List<MyFinBudgetCategory>,
        val monthYearFormatted: String,
        val expensesPlannedAmountFormatted: String,
        val expensesCurrentAmountFormatted: String,
        val incomePlannedAmountFormatted: String,
        val incomeCurrentAmountFormatted: String,
        val balanceAmountFormatted: String,
        val balanceAmount: Double,
        val expensesProgressPercentage: Int,
        val incomeProgressPercentage: Int,
        val isOpen: Boolean
    )
}