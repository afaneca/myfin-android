package com.afaneca.myfin.closed.dashboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.dashboard.data.DashboardRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentDashboardBinding
import com.afaneca.myfin.utils.visible
import java.util.*

class DashboardFragment :
    BaseFragment<DashboardViewModel, FragmentDashboardBinding, DashboardRepository>() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)

        bindObservers()
        bindListeners()
        getMonthlyIncomeExpensesDistributionDataForCurrentMonth()
        return rootView;
    }

    override fun onResume() {
        super.onResume()
        setActionBarTitle(getString(R.string.dashboard_label))
    }

    private fun getMonthlyIncomeExpensesDistributionDataForCurrentMonth() {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        val currentYear = Calendar.getInstance().get(Calendar.YEAR);
        viewModel.requestMonthlyExpensesIncomeDistribution(currentMonth, currentYear)
    }

    private fun bindListeners() {
        viewModel.getMonthlyIncomeExpensesDistributionData().observe(viewLifecycleOwner, {
            binding.loadingPb.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {

                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun bindObservers() {
        // TODO("Not yet implemented")
    }

    override fun getViewModel(): Class<DashboardViewModel> = DashboardViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDashboardBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = DashboardRepository(
        remoteDataSource.create(MyFinAPIServices::class.java), userData
    )

}