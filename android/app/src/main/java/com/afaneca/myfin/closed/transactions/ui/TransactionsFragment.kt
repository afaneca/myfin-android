package com.afaneca.myfin.closed.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentTransactionsBinding
import com.afaneca.myfin.utils.safeNavigate
import com.afaneca.myfin.utils.visible
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by me on 20/06/2021
 */
@AndroidEntryPoint
class TransactionsFragment :
    BaseFragment() {
    private lateinit var binding: FragmentTransactionsBinding
    private val viewModel: TransactionsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init()
        bindObservers()
        getTransactionsList()
        setupAddTransactionFab()
    }

    private fun setupAddTransactionFab() {
        binding.addTransactionFab.root.setOnClickListener { showAddTransactionBottomSheet() }
    }

    private fun showAddTransactionBottomSheet() {
        val action = TransactionsFragmentDirections
            .actionTransactionsFragmentToAddTransactionBottomSheetFragment()
        findNavController().safeNavigate(action)
    }

    private fun getTransactionsList(query: String? = null) {
        viewModel.requestTransactions(query)
    }

    private fun onMoreTransactionsAsked() {
        viewModel.requestMoreTransactions()
    }

    private fun bindObservers() {
        viewModel.apply {
            getTransactionsListData().observe(viewLifecycleOwner) {
                binding.loadingPb.root.visible(it is Resource.Loading)
                when (it) {
                    is Resource.Success -> {

                    }

                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                    }

                    else -> {}
                }
            }

            transactionsListDataset.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                if (binding.recyclerView.adapter == null)
                    setupTransactionsRecyclerView(it)
                else {
                    (binding.recyclerView.adapter!! as TransactionsListAdapter).updateDataset(it)
                    binding.recyclerView.adapter!!.notifyDataSetChanged()
                }

            }

            clickedTransactionDetails.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                showTransactionDetailsBottomSheetFragment(it)
            }
        }
    }

    private fun showTransactionDetailsBottomSheetFragment(trx: MyFinTransaction) {
        val action =
            TransactionsFragmentDirections.actionTransactionsFragmentToTransactionDetailsBottomSheetFragment(
                trx
            )
        findNavController().safeNavigate(action)
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

                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.requestTransactions(p0)
                return false
            }

        })

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    //scrolled to BOTTOM
                    onMoreTransactionsAsked()
                }
            }
        })
    }
}