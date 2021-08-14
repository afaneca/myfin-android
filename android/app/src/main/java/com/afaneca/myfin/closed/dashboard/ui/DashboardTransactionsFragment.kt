package com.afaneca.myfin.closed.dashboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.closed.transactions.data.TransactionsRepository
import com.afaneca.myfin.closed.transactions.ui.TransactionDetailsBottomSheetFragment
import com.afaneca.myfin.closed.transactions.ui.TransactionsListAdapter
import com.afaneca.myfin.closed.transactions.ui.TransactionsViewModel
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentDashboardTransactionsBinding
import com.afaneca.myfin.utils.visible

/**
 * Created by me on 25/07/2021
 */
class DashboardTransactionsFragment :
    BaseFragment<TransactionsViewModel, FragmentDashboardTransactionsBinding, TransactionsRepository>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearData()
        bindObservers()
        getTransactionsList()
    }

    private fun bindObservers() {
        viewModel.apply {
            getTransactionsListData().observe(viewLifecycleOwner, {
                binding.loadingPb.visible(it is Resource.Loading)
                when (it) {
                    is Resource.Success -> {

                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            })

            transactionsListDataset.observe(viewLifecycleOwner, {
                if (it == null) return@observe
                if (binding.recyclerView.adapter == null)
                    setupTransactionsRecyclerView(it)
                else {
                    (binding.recyclerView.adapter!! as TransactionsListAdapter).updateDataset(it)
                    binding.recyclerView.adapter!!.notifyDataSetChanged()
                }

            })

            clickedTransactionDetails.observe(viewLifecycleOwner, {
                if (it == null) return@observe
                showTransactionDetailsBottomSheetFragment(it)
            })
        }
    }

    private fun getTransactionsList() {
        viewModel.requestTransactions(5)
    }

    private fun showTransactionDetailsBottomSheetFragment(trx: MyFinTransaction) {
        val bottomSheetFragment = TransactionDetailsBottomSheetFragment.newInstance(trx)
        bottomSheetFragment.show(
            parentFragmentManager,
            TransactionDetailsBottomSheetFragment.javaClass.name
        )
    }

    private fun setupTransactionsRecyclerView(dataset: List<MyFinTransaction>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = TransactionsListAdapter(requireContext(), dataset, viewModel)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                binding.recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }


    /**/
    override fun getViewModel(): Class<TransactionsViewModel> = TransactionsViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDashboardTransactionsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        TransactionsRepository(remoteDataSource.create(MyFinAPIServices::class.java), userData)


}