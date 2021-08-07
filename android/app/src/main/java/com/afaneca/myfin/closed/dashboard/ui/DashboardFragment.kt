package com.afaneca.myfin.closed.dashboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.PrivateActivity
import com.afaneca.myfin.closed.dashboard.data.DashboardRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentDashboardBinding
import com.afaneca.myfin.utils.charts.ChartUtils
import com.afaneca.myfin.utils.visible
import com.google.android.material.tabs.TabLayout
import java.util.*


class DashboardFragment :
    BaseFragment<DashboardViewModel, FragmentDashboardBinding, DashboardRepository>() {

    companion object {
        private const val TAB_PIE_CHART_EXPENSES_POSITION = 0
        private const val TAB_PIE_CHART_INCOME_POSITION = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        bindListeners()
        getMonthlyIncomeExpensesDistributionDataForCurrentMonth()
    }

    private fun setupMonthlyDistributionCharts() {
        binding.amountDistributionPiechartTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    TAB_PIE_CHART_EXPENSES_POSITION -> setupExpensesDistributionPieChart()
                    TAB_PIE_CHART_INCOME_POSITION -> setupIncomeDistributionPieChart()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
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
                        setupMonthlyDistributionCharts()
                        setupExpensesDistributionPieChart()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            })
            expensesDistributionChartData.observe(viewLifecycleOwner, {
                val selectedTabPosition =
                    binding.amountDistributionPiechartTabLayout.selectedTabPosition
                if (selectedTabPosition == TAB_PIE_CHART_EXPENSES_POSITION) {
                    setupExpensesDistributionPieChart()
                }
            })

            incomeDistributionChartData.observe(viewLifecycleOwner, {
                val selectedTabPosition =
                    binding.amountDistributionPiechartTabLayout.selectedTabPosition
                if (selectedTabPosition == TAB_PIE_CHART_INCOME_POSITION) {
                    setupIncomeDistributionPieChart()
                }
            })

            monthlyOverviewChartData.observe(viewLifecycleOwner, {
                if (it == null) return@observe
                setupMonthlyOverviewChart(it.progressValue, it.currentAmount, it.plannedAmount)
            })

            lastUpdateTimestampFormatted.observe(viewLifecycleOwner, {
                if (it == null) return@observe
                refreshLastUpdateTimestampValue(it)
            })

        }
    }

    private fun refreshLastUpdateTimestampValue(formattedTimestamp: String) {
        (activity as PrivateActivity).findViewById<TextView>(R.id.last_update_timestamp_value).text =
            formattedTimestamp
    }

    private fun setupIncomeDistributionPieChart() {
        ChartUtils.buildPieChart(
            requireContext(),
            binding.amountDistributionPiechart,
            getString(R.string.generic_expenses),
            viewModel.incomeDistributionChartData.value!!.toMap(),
            isLegendEnabled = false
        )
    }

    private fun setupExpensesDistributionPieChart() {
        ChartUtils.buildPieChart(
            requireContext(),
            binding.amountDistributionPiechart,
            getString(R.string.generic_income),
            viewModel.expensesDistributionChartData.value!!.toMap(),
            isLegendEnabled = false
        )
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