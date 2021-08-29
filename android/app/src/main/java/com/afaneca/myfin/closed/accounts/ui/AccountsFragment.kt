package com.afaneca.myfin.closed.accounts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.base.objects.MyFinAccount
import com.afaneca.myfin.closed.accounts.data.AccountsRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentAccountsBinding
import com.afaneca.myfin.utils.visible
import com.google.android.material.tabs.TabLayout

/**
 * Created by me on 14/08/2021
 */
class AccountsFragment :
    BaseFragment<AccountsViewModel, FragmentAccountsBinding, AccountsRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        getAccountsList()
    }

    private fun bindObservers() {
        viewModel.getAccountsListResponse().observe(viewLifecycleOwner, {
            binding.loadingPb.visible(it is Resource.Loading)
            when (it) {
                is Resource.Failure -> Toast.makeText(
                    requireContext(),
                    it.errorMessage,
                    Toast.LENGTH_LONG
                ).show()
                is Resource.Success -> {
                    setupTabLayout()
                }
            }
        })

        viewModel.getAccountsListData().observe(viewLifecycleOwner, {
            setupAccountsListRecyclerView(it)
        })
    }

    private fun setupTabLayout() {
        binding.tabLayout2.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewModel.onTabSelected(tab.position)
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun setupAccountsListRecyclerView(accsList: List<MyFinAccount>) {
        context?.let {
            if (accsList.isNullOrEmpty()) {
                binding.accountsListEmptyViewTv.visible(true)
                binding.accountsListRv.visible(false)
            } else {
                binding.accountsListEmptyViewTv.visible(false)
                binding.accountsListRv.visible(true)
                binding.accountsListRv.layoutManager = LinearLayoutManager(context)
                binding.accountsListRv.adapter = AccountsListAdapter(it, accsList)
                binding.accountsListRv.addItemDecoration(
                    DividerItemDecoration(
                        binding.accountsListRv.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
            }

        }
    }

    private fun getAccountsList() {
        viewModel.requestAccountsList()
    }

    /**/
    override fun getViewModel() = AccountsViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAccountsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = AccountsRepository(
        remoteDataSource.create(MyFinAPIServices::class.java),
        userData
    )
}