package com.afaneca.myfin.closed.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.transactions.data.TransactionsRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.databinding.FragmentTransactionDetailsBinding
import com.afaneca.myfin.databinding.FragmentTransactionsBinding

/**
 * Created by me on 20/06/2021
 */
class TransactionDetailsFragment :
    BaseFragment<TransactionsViewModel, FragmentTransactionDetailsBinding, TransactionsRepository>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTransactionDetailsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        TransactionsRepository(remoteDataSource.create(MyFinAPIServices::class.java), userData)

    override fun getViewModel(): Class<TransactionsViewModel> = TransactionsViewModel::class.java
}