package com.afaneca.myfin.closed.transactions.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.closed.transactions.data.LatestTransactionsListResponse
import com.afaneca.myfin.closed.transactions.data.TransactionsRepository
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.utils.SingleLiveEvent
import kotlinx.coroutines.launch

/**
 * Created by me on 20/06/2021
 */
private const val TRX_PAGE_SIZE = 25

class TransactionsViewModel(
    private val repository: TransactionsRepository
) : ViewModel(), TransactionsListAdapter.TransactionsListItemClickListener {


    private val _transactionsListData: SingleLiveEvent<Resource<LatestTransactionsListResponse>> =
        SingleLiveEvent()

    fun getTransactionsListData() = _transactionsListData

    private val _transactionsListDataset: MutableLiveData<List<MyFinTransaction>> =
        MutableLiveData(ArrayList())
    val transactionsListDataset = _transactionsListDataset

    private val _clickedTransactionDetails: MutableLiveData<MyFinTransaction> = MutableLiveData()
    val clickedTransactionDetails = _clickedTransactionDetails

    private val _trxHasMore: MutableLiveData<Boolean> = MutableLiveData(true)
    private val trxHasMore = _trxHasMore

    private var _trxCurrentPage: Int = 0

    private fun requestTransactionsList(page: Int, pageSize: Int = TRX_PAGE_SIZE) =
        viewModelScope.launch {
            _transactionsListData.value = Resource.Loading
            _transactionsListData.value =
                repository.getTransactionsByPage(page, pageSize)
            if (_transactionsListData.value is Resource.Success<LatestTransactionsListResponse>) {
                val newItems =
                    (_transactionsListData.value as Resource.Success<LatestTransactionsListResponse>).data
                _trxHasMore.postValue(!newItems.isEmpty())
                val aggregatedList: List<MyFinTransaction> =
                    _transactionsListDataset.value!! + newItems

                _transactionsListDataset.postValue(aggregatedList)
            }
        }

    override fun onTransactionClick(trx: MyFinTransaction) {
        _clickedTransactionDetails.postValue(trx)
    }

    fun requestTransactions(pageSize: Int = TRX_PAGE_SIZE) {
        _trxCurrentPage = 0
        requestTransactionsList(_trxCurrentPage, pageSize)
    }

    fun requestMoreTransactions() {
        if (!trxHasMore.value!!) {
            return
        }

        requestTransactionsList(++_trxCurrentPage)
    }

    fun clearData() {
        _trxCurrentPage = 0
        _transactionsListData.value = null
        _transactionsListDataset.value = ArrayList()
    }
}