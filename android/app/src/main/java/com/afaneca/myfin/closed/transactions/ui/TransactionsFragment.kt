package com.afaneca.myfin.closed.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.closed.transactions.data.TransactionsRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentTransactionsBinding
import com.afaneca.myfin.utils.visible

/**
 * Created by me on 20/06/2021
 */
class TransactionsFragment :
    BaseFragment<TransactionsViewModel, FragmentTransactionsBinding, TransactionsRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObververs()
        getTransactionsList()

        /*binding.textView2.setOnClickListener {
            (activity as PrivateActivity).getNavController()
                .navigate(R.id.action_transactionsFragment_to_transactionDetailsFragment)
        }*/

        //setupTransactionsList(getMockTransactionsList())
    }

    private fun getTransactionsList() {
        viewModel.requestTransactionsList()
    }

    private fun bindObververs() {
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
                setupTransactionsRecyclerView(it)
            })

            clickedTransactionDetails.observe(viewLifecycleOwner, {
                if (it == null) return@observe
                showTransactionDetailsBottomSheetFragment(it)
            })
        }
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
        // to make the whole thing clickable
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                (binding.recyclerView.adapter as Filterable).filter.filter(p0)
                return false
            }

        })
    }


    /**/
    override fun getViewModel(): Class<TransactionsViewModel> = TransactionsViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTransactionsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        TransactionsRepository(remoteDataSource.create(MyFinAPIServices::class.java), userData)

}