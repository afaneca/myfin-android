package com.afaneca.myfin.closed.dashboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.dashboard.data.DashboardRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentDashboardBinding
import com.afaneca.myfin.open.login.data.LoginRepository
import com.afaneca.myfin.utils.visible
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.YearMonth
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

    private fun getMonthlyIncomeExpensesDistributionDataForCurrentMonth() {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        val currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Toast.makeText(
            requireContext(),
            "Month: $currentMonth and Year: $currentYear",
            Toast.LENGTH_LONG
        ).show()
        viewModel.getMonthlyExpensesIncomeDistribution(currentMonth, currentYear)
    }

    private fun bindListeners() {
        viewModel.monthlyIncomeExpensesDistributionData.observe(viewLifecycleOwner, {
            binding.loadingPb.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "${it.data}", Toast.LENGTH_SHORT).show()
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
        remoteDataSource.create(MyFinAPIServices::class.java)
        /*remoteDataSource.buildApi(
            MyFinAPIServices::class.java,
            runBlocking { userData.sessionKey.first() }
        )*/, userData
    )

}