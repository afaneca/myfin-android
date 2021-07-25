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

    /*val args: DashboardFragmentA*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        bindListeners()
        getMonthlyIncomeExpensesDistributionDataForCurrentMonth()
    }


    private fun getMonthlyIncomeExpensesDistributionDataForCurrentMonth() {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        val currentYear = Calendar.getInstance().get(Calendar.YEAR);
        viewModel.requestMonthlyExpensesIncomeDistribution(currentMonth, currentYear)
    }

    private fun bindListeners() {

    }

    private fun bindObservers() {
        viewModel.apply {
            getMonthlyIncomeExpensesDistributionData().observe(viewLifecycleOwner, {
                binding.loadingPb.visible(it is Resource.Loading)
                when (it) {
                    is Resource.Success -> {
                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            })

            monthlyOverviewChartData.observe(viewLifecycleOwner, {
                if (it == null) return@observe
                setupMonthlyOverviewChart(it.progressValue, it.currentAmount, it.plannedAmount)
            })

        }
    }

    private fun setupMonthlyOverviewChart(
        progressValue: Double,
        currentAmountFormatted: String,
        plannedAmountFormatted: String
    ) {
        binding.monthlyOverviewPchart.setProgress(progressValue.toFloat())
        binding.chartCurrentAmountTv.text = currentAmountFormatted
        binding.chartPlannedAmountTv.text = String.format(
            getString(R.string.dashboard_monthly_overview_chart_planned_amount_label),
            plannedAmountFormatted
        )

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