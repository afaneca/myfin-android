package com.afaneca.myfin.closed.accounts.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.base.objects.MyFinAccount
import com.afaneca.myfin.closed.accounts.data.AccountsListResponse
import com.afaneca.myfin.closed.accounts.data.AccountsRepository
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.utils.MyFinUtils
import com.afaneca.myfin.utils.SingleLiveEvent
import kotlinx.coroutines.launch

/**
 * Created by me on 14/08/2021
 */
class AccountsViewModel(
    private val repository: AccountsRepository
) : ViewModel() {

    companion object {
        private const val TAB_ACCOUNTS_ALL = 0
        private const val TAB_ACCOUNTS_OPERATING_FUNDS = 1
        private const val TAB_ACCOUNTS_CREDITS = 2
        private const val TAB_ACCOUNTS_INVESTMENTS = 3
        private const val TAB_ACCOUNTS_OTHERS = 4
    }

    private val _accountsListResponse: SingleLiveEvent<Resource<AccountsListResponse>> =
        SingleLiveEvent()

    fun getAccountsListResponse() = _accountsListResponse

    private val _originalFullAccountsList: MutableLiveData<List<MyFinAccount>> =
        MutableLiveData(ArrayList())

    private val _accountsList: MutableLiveData<List<MyFinAccount>> = MutableLiveData(ArrayList())
    fun getAccountsListData() = _accountsList

    fun requestAccountsList() = viewModelScope.launch {
        _accountsListResponse.value = Resource.Loading
        _accountsListResponse.value = repository.getAccountsList()
        if (_accountsListResponse.value is Resource.Success<AccountsListResponse>) {
            _originalFullAccountsList.value =
                (_accountsListResponse.value as Resource.Success<AccountsListResponse>).data
        }

        onTabSelected(TAB_ACCOUNTS_ALL)
    }

    fun onTabSelected(position: Int) {
        _accountsList.value = when (position) {
            TAB_ACCOUNTS_OPERATING_FUNDS -> MyFinUtils.getOnlyOperatingFundsAccountsFromFullList(
                _originalFullAccountsList.value ?: ArrayList()
            )
            TAB_ACCOUNTS_CREDITS -> MyFinUtils.getOnlyCreditAccountsFromFullList(
                _originalFullAccountsList.value ?: ArrayList()
            )
            TAB_ACCOUNTS_INVESTMENTS -> MyFinUtils.getOnlyInvestingAccountsFromFullList(
                _originalFullAccountsList.value ?: ArrayList()
            )
            TAB_ACCOUNTS_OTHERS -> MyFinUtils.getOnlyOtherAccountsFromFullList(
                _originalFullAccountsList.value ?: ArrayList()
            )
            else -> _originalFullAccountsList.value
        }
    }


}