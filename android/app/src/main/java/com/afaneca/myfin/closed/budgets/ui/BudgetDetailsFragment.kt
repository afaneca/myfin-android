package com.afaneca.myfin.closed.budgets.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.budgets.data.BudgetsRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentBudgetDetailsBinding
import com.afaneca.myfin.utils.setProgressBarValueWithAnimation
import com.afaneca.myfin.utils.setupBalanceStyle
import com.afaneca.myfin.utils.visible
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

/**
 * Created by me on 07/08/2021
 */
class BudgetDetailsFragment :
    BaseFragment<BudgetDetailsViewModel, FragmentBudgetDetailsBinding, BudgetsRepository>() {
    private val args: BudgetDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObservers()
        getBudgetDetails(args.budgetId)
        setupTabLayout()
    }

    private fun getBudgetDetails(id: String) {
        viewModel.requestBudgetDetails(id)
    }

    private fun bindObservers() {
        viewModel.getBudgetDetailsRequest().observe(viewLifecycleOwner, {
            binding.loadingPb.visible(it is Resource.Loading)
            when (it) {
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.budgetDetailsData.observe(viewLifecycleOwner, {
            bindDataToViews(it)
        })
    }

    private fun bindDataToViews(budgetDetailsData: BudgetDetailsViewModel.BudgetDetailsData?) {
        val isTabExpenses = viewModel.getSelectedTab() == BudgetDetailsViewModel.TAB.EXPENSES

        budgetDetailsData?.let {
            binding.budgetBalanceTv.text = it.balanceAmountFormatted
            setupBalanceStyle(it.balanceAmount, binding.budgetBalanceTv)
            binding.budgetMonthYearTv.text = it.monthYearFormatted
            binding.budgetProgressPlannedAmountTv.text =
                if (isTabExpenses) it.expensesPlannedAmountFormatted else it.incomePlannedAmountFormatted
            binding.budgetProgressCurrentAmountTv.text =
                if (isTabExpenses) it.expensesCurrentAmountFormatted else it.incomeCurrentAmountFormatted

            refreshProgressBarState(if (isTabExpenses) it.expensesProgressPercentage else it.incomeProgressPercentage)
        }
    }

    private fun refreshProgressBarState(progress: Int) {
        /*binding.progressBar.progress = progress*/
        setProgressBarValueWithAnimation(binding.progressBar, progress)
        val viewColor = when (progress) {
            in 0..75 -> R.color.colorGreen
            in 75..99 -> R.color.colorOrange
            else -> R.color.colorRed
        }
        context?.let {
            binding.progressBar.progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(it, viewColor)
            )
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab == null) return
                viewModel.onTabSelected(tab.position)
                bindDataToViews(viewModel.budgetDetailsData.value)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    /**/
    override fun getViewModel(): Class<BudgetDetailsViewModel> = BudgetDetailsViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBudgetDetailsBinding =
        FragmentBudgetDetailsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): BudgetsRepository =
        BudgetsRepository(remoteDataSource.create(MyFinAPIServices::class.java), userData)
}