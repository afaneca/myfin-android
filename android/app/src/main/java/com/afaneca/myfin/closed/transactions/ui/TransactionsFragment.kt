package com.afaneca.myfin.closed.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.ui.NavigationUI
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.PrivateActivity
import com.afaneca.myfin.closed.transactions.data.TransactionsRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.databinding.FragmentTransactionsBinding

/**
 * Created by me on 20/06/2021
 */
class TransactionsFragment :
    BaseFragment<TransactionsViewModel, FragmentTransactionsBinding, TransactionsRepository>() {
    override fun getViewModel(): Class<TransactionsViewModel> = TransactionsViewModel::class.java


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO - remove this mock
        binding.textView2.setOnClickListener {
            (activity as PrivateActivity).getNavController()
                .navigate(R.id.action_transactionsFragment_to_transactionDetailsFragment)
        }
    }


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTransactionsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        TransactionsRepository(remoteDataSource.create(MyFinAPIServices::class.java), userData)

}