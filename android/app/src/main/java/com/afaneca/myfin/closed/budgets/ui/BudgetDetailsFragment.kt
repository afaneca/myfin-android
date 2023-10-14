package com.afaneca.myfin.closed.budgets.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.data.model.MyFinBudgetCategory
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentBudgetDetailsBinding
import com.afaneca.myfin.utils.setProgressBarValueWithAnimation
import com.afaneca.myfin.utils.setupBalanceStyle
import com.afaneca.myfin.utils.visible
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.VISIBLE
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by me on 07/08/2021
 */
@AndroidEntryPoint
class BudgetDetailsFragment :
    BaseFragment(),
    BudgetDetailsCategoriesListAdapter.BudgetDetailsCategoryListItemClickListener {
    private val args: BudgetDetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentBudgetDetailsBinding
    private val viewModel: BudgetDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isOpen = args.isOpen
        bindObservers()
        getBudgetDetails(args.budgetId)
        setupTabLayout()
    }

    private fun getBudgetDetails(id: String) {
        viewModel.requestBudgetDetails(id)
    }

    private fun bindObservers() {
        viewModel.getBudgetDetailsRequest().observe(viewLifecycleOwner) {
            binding.loadingPb.root.visible(it is Resource.Loading)
            when (it) {
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        viewModel.budgetDetailsData.observe(viewLifecycleOwner) {
            bindDataToViews(it)
        }
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
            binding.budgetStatusTv.text =
                getString(if (budgetDetailsData.isOpen) R.string.generic_open else R.string.generic_closed)
            binding.budgetStatusWrapperCv.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (budgetDetailsData.isOpen) R.color.colorGreen else R.color.colorRed
                )
            )
            binding.budgetStatusWrapperCv.visibility = VISIBLE
            refreshProgressBarState(if (isTabExpenses) it.expensesProgressPercentage else it.incomeProgressPercentage)
            setupCategoriesList(it.myFinBudgetCategories, isTabExpenses)
        }
    }

    private fun setupCategoriesList(categories: List<MyFinBudgetCategory>, isTabExpenses: Boolean) {
        binding.categoriesRv.layoutManager = LinearLayoutManager(context)
        binding.categoriesRv.adapter = BudgetDetailsCategoriesListAdapter(
            requireContext(),
            viewModel.sortCategoriesList(categories as ArrayList<MyFinBudgetCategory>),
            this,
            isTabExpenses
        )
        binding.categoriesRv.addItemDecoration(
            DividerItemDecoration(
                binding.categoriesRv.context,
                DividerItemDecoration.VERTICAL
            )
        )
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

    override fun onCategoryClick(cat: MyFinBudgetCategory) {
        if (viewModel.isOpen)
            showBudgetCategoryDetailsBottomSheetFragment(cat)
    }

    private fun showBudgetCategoryDetailsBottomSheetFragment(cat: MyFinBudgetCategory) {
        val bottomSheetFragment =
            BudgetDetailsCategoryBottomSheetFragment.newInstance(args.budgetId, cat)
        bottomSheetFragment.show(
            parentFragmentManager,
            BudgetDetailsCategoryBottomSheetFragment::class.java.name
        )
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
}