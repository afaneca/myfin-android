package com.afaneca.myfin.closed.transactions.ui

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.data.model.FilteredResultsByPage
import com.afaneca.myfin.domain.repository.TransactionsRepository
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by me on 20/06/2021
 */
private const val TRX_PAGE_SIZE = 25

@HiltViewModel
class TransactionsViewModel
@Inject
constructor(
    private val repository: TransactionsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), TransactionsListAdapter.TransactionsListItemClickListener {


    private val _transactionsListData: SingleLiveEvent<Resource<FilteredResultsByPage<MyFinTransaction>>?> =
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
    private var _query: String? = null

    private fun requestTransactionsList(page: Int, query: String?, pageSize: Int = TRX_PAGE_SIZE) =
        viewModelScope.launch {
            _transactionsListData.value = Resource.Loading
            _transactionsListData.value =
                repository.getFilteredTransactionsByPage(page, pageSize, query)
            if (_transactionsListData.value is Resource.Success<FilteredResultsByPage<MyFinTransaction>>) {
                val newItems =
                    (_transactionsListData.value as Resource.Success<FilteredResultsByPage<MyFinTransaction>>).data.results
                _trxHasMore.postValue(newItems.isNotEmpty())
                val aggregatedList: List<MyFinTransaction> =
                    _transactionsListDataset.value!! + newItems

                _transactionsListDataset.postValue(aggregatedList)
            }
        }

    override fun onTransactionClick(trx: MyFinTransaction) {
        _clickedTransactionDetails.postValue(trx)
    }

    fun requestTransactions(query: String? = null, pageSize: Int = TRX_PAGE_SIZE) {
        clearData()
        _query = query;
        requestTransactionsList(_trxCurrentPage, query, pageSize)
    }

    fun requestMoreTransactions() {
        if (!trxHasMore.value!!) {
            return
        }

        requestTransactionsList(++_trxCurrentPage, _query)
    }

    fun clearData() {
        _trxCurrentPage = 0
        _query = null
        _trxHasMore.value = true
        _transactionsListData.value = null
        _transactionsListDataset.value = ArrayList()
    }

    fun init() {
        clearData()
    }
}