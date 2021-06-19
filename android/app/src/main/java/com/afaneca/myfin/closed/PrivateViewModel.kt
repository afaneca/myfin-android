package com.afaneca.myfin.closed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.MyFinDatabase
import com.afaneca.myfin.data.db.accounts.UserAccountEntity
import com.afaneca.myfin.utils.MyFinUtils
import com.afaneca.myfin.utils.formatMoney
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

/**
 * Created by me on 14/06/2021
 */

@KoinApiExtension
class PrivateViewModel : ViewModel(), KoinComponent {
    private val repository: PrivateRepository by lazy {
        PrivateRepository(get(), get())
    }

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
                MyFinUtils.getOnlyOperatingFundsAccountsFromFullList(
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
                MyFinUtils.getOnlyInvestingAccountsFromFullList(
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
                MyFinUtils.getOnlyCreditAccountsFromFullList(
                    accsList
                )
            )
        )

    private fun calculateAggregatedDebtAccountsBalance(accsList: List<UserAccountEntity>): Double {
        var sum = 0.00F
        accsList.forEach { acc -> sum += (acc.balance ?: "0").toFloat() }
        return sum.toDouble()
    }
}