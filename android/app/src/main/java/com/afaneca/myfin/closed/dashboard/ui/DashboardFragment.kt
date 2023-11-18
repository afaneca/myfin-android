package com.afaneca.myfin.closed.dashboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.PrivateActivity
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentDashboardBinding
import com.afaneca.myfin.utils.charts.ChartUtils
import com.afaneca.myfin.utils.visible
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DashboardFragment :
    BaseFragment() {

    companion object {
        private const val TAB_PIE_CHART_EXPENSES_POSITION = 0
        private const val TAB_PIE_CHART_INCOME_POSITION = 1
    }

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
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
                binding.loadingPb.root.visible(it is Resource.Loading)
                when (it) {
                    is Resource.Success -> {
                        setupMonthlyDistributionCharts()
                        setupExpensesDistributionPieChart()
                    }

                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                    }

                    else -> {}
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
                setupMonthlyOverview(
                    it.progressValue,
                    it.currentAmount,
                    it.plannedAmount,
                    it.amountLeft,
                    it.overBudget
                )
            })

            lastUpdateTimestampFormatted.observe(viewLifecycleOwner, {
                if (it == null) return@observe
                refreshLastUpdateTimestampValue(it)
            })

        }
    }

    private fun refreshLastUpdateTimestampValue(formattedTimestamp: String) {
        (activity as PrivateActivity).refreshLastUpdateTimestampValue(formattedTimestamp)
    }

    private fun setupIncomeDistributionPieChart() {
        ChartUtils.buildPieChart(
            requireContext(),
            binding.amountDistributionPiechart,
            getString(R.string.generic_income),
            viewModel.incomeDistributionChartData.value!!.toMap(),
            isLegendEnabled = false
        )
    }

    private fun setupExpensesDistributionPieChart() {
        ChartUtils.buildPieChart(
            requireContext(),
            binding.amountDistributionPiechart,
            getString(R.string.generic_expenses),
            viewModel.expensesDistributionChartData.value!!.toMap(),
            isLegendEnabled = false
        )
    }

    private fun setupMonthlyOverview(
        progressValue: Double,
        currentAmountFormatted: String,
        plannedAmountFormatted: String,
        amountLeftFormatted: String,
        overBudget: Boolean
    ) {

        binding.expensesLpi.progress = (progressValue * 100).toInt()
        binding.expensesLeftTv.text = amountLeftFormatted
        binding.currentExpensesTv.text = currentAmountFormatted
        binding.ofPlannedExpensesTv.text = String.format(
            " %s",
            getString(
                R.string.dashboard_monthly_overview_of_spent_label,
                plannedAmountFormatted
            )
        )

        if(overBudget){
            binding.monthlyOverviewCl.background = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_red_pink)
        }
    }
}