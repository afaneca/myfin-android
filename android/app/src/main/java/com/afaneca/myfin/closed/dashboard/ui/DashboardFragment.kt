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
import com.afaneca.myfin.utils.ChartUtils
import com.afaneca.myfin.utils.visible
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.tabs.TabLayout
import java.util.*


class DashboardFragment :
    BaseFragment<DashboardViewModel, FragmentDashboardBinding, DashboardRepository>() {

    /*val args: DashboardFragmentA*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        bindListeners()
        setupMonthlyDistributionCharts()
        getMonthlyIncomeExpensesDistributionDataForCurrentMonth()
    }

    private fun setupMonthlyDistributionCharts() {
        binding.incomeDistributionPiechartTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    1 -> setupExpensesDistributionPieChart()
                    0 -> setupIncomeDistributionPieChart()
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
                        setupExpensesDistributionPieChart() // TODO - add real data
                        setupIncomeDistributionPieChart()
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

    private fun setupIncomeDistributionPieChart() {
        var dataset = ArrayList<PieEntry>()
        dataset.apply {
            add(PieEntry(50F, "data 1"))
            add(PieEntry(30F, "data 2"))
            add(PieEntry(10F, "data 3"))
            add(PieEntry(10F, "data 4"))
        }

        val pieDataSet: PieDataSet = PieDataSet(dataset, "dataset 123")
        pieDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)

        ChartUtils.buildPieChart(
            requireContext(),
            binding.expensesDistributionPiechart,
            "Despesa",
            PieData(pieDataSet),
            isLegendEnabled = false
        )
    }

    private fun setupExpensesDistributionPieChart() {
        var dataset = ArrayList<PieEntry>()
        dataset.apply {
            add(PieEntry(25F, "eheh"))
            add(PieEntry(25F, "ohoh"))
            add(PieEntry(40F, "eheh"))
            add(PieEntry(10F, "ihih"))
        }

        val pieDataSet: PieDataSet = PieDataSet(dataset, "dataset 123")
        pieDataSet.setColors(*ColorTemplate.MATERIAL_COLORS)

        ChartUtils.buildPieChart(
            requireContext(),
            binding.expensesDistributionPiechart,
            "Receita",
            PieData(pieDataSet),
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