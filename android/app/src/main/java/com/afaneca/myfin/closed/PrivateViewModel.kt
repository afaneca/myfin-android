package com.afaneca.myfin.closed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afaneca.myfin.domain.repository.AccountRepository
import com.afaneca.myfin.data.db.accounts.UserAccountEntity
import com.afaneca.myfin.utils.MyFinUtils
import com.afaneca.myfin.utils.formatMoney
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by me on 14/06/2021
 */

@HiltViewModel
class PrivateViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {


    private var _patrimonyBalance: MutableLiveData<String> = MutableLiveData("0.00")
    val patrimonyBalance: LiveData<String>
        get() = _patrimonyBalance

    private var _operatingFundsBalance: MutableLiveData<String> = MutableLiveData("0.00")
    val operatingFundsBalance: LiveData<String>
        get() = _operatingFundsBalance

    private var _investingBalance: MutableLiveData<String> = MutableLiveData("0.00")
    val investingBalance: LiveData<String>
        get() = _investingBalance

    private var _debtBalance: MutableLiveData<String> = MutableLiveData("0.00")
    val debtBalance: LiveData<String>
        get() = _debtBalance


    fun getUserAccounts(): LiveData<List<UserAccountEntity>> {
        return repository.getAllUserAccounts()
    }


    fun calculateAggregatedAccountBalances(accsList: List<UserAccountEntity>) {
        _patrimonyBalance.postValue(getFormattedAggregatedPatrimonyBalance(accsList))
        _operatingFundsBalance.postValue(getFormattedAggregatedOperatingFundAccountsBalance(accsList))
        _investingBalance.postValue(getFormattedAggregatedInvestingAccountsBalance(accsList))
        _debtBalance.postValue(getFormattedAggregatedDebtAccountsBalance(accsList))
    }

    /* Patrimony */
    private fun getFormattedAggregatedPatrimonyBalance(accsList: List<UserAccountEntity>): String =
        formatMoney(calculateAggregatePatrimonyBalance(accsList))

    private fun calculateAggregatePatrimonyBalance(accsList: List<UserAccountEntity>): Double {
        var sum = 0.00F
        accsList.forEach { acc -> sum += (acc.balance ?: "0").toFloat() }
        return sum.toDouble()
    }

    /* Operating Funds Accounts */
    private fun getFormattedAggregatedOperatingFundAccountsBalance(accsList: List<UserAccountEntity>): String =
        formatMoney(
            calculateAggregatedOperatingFundAccountsBalance(
                MyFinUtils.getOnlyOperatingFundsAccountsFromDBFullList(
                    accsList
                )
            )
        )

    private fun calculateAggregatedOperatingFundAccountsBalance(accsList: List<UserAccountEntity>): Double {
        var sum = 0.00F
        accsList.forEach { acc -> sum += (acc.balance ?: "0").toFloat() }
        return sum.toDouble()
    }

    /* Investing Accounts */
    private fun getFormattedAggregatedInvestingAccountsBalance(accsList: List<UserAccountEntity>): String =
        formatMoney(
            calculateAggregatedInvestingAccountsBalance(
                MyFinUtils.getOnlyInvestingAccountsFromDBFullList(
                    accsList
                )
            )
        )

    private fun calculateAggregatedInvestingAccountsBalance(accsList: List<UserAccountEntity>): Double {
        var sum = 0.00F
        accsList.forEach { acc -> sum += (acc.balance ?: "0").toFloat() }
        return sum.toDouble()
    }

    /* Debt/Credit Accounts */
    private fun getFormattedAggregatedDebtAccountsBalance(accsList: List<UserAccountEntity>): String =
        formatMoney(
            calculateAggregatedDebtAccountsBalance(
                MyFinUtils.getOnlyCreditAccountsFromDBFullList(
                    accsList
                )
            )
        )

    private fun calculateAggregatedDebtAccountsBalance(accsList: List<UserAccountEntity>): Double {
        var sum = 0.00F
        accsList.forEach { acc -> sum += (acc.balance ?: "0").toFloat() }
        return sum.toDouble()
    }

    fun clearUserSessionData() {
        repository.clearUserSessionData()
    }
}