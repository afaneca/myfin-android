package com.afaneca.myfin.closed.budgets.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.base.components.MyFinLinearSmoothScroller
import com.afaneca.myfin.base.objects.MyFinBudget
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentBudgetsBinding
import com.afaneca.myfin.utils.parseStringToBoolean
import com.afaneca.myfin.utils.safeNavigate
import com.afaneca.myfin.utils.visible
import java.util.*


/**
 * Created by me on 07/08/2021
 */
class BudgetsFragment :
    BaseFragment(),
    BudgetsListAdapter.BudgetsListItemClickListener {
    private lateinit var binding: FragmentBudgetsBinding
    private val viewModel: BudgetsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObservers()
        getBudgetsList()
    }

    private fun bindObservers() {
        viewModel.getBudgetsListData().observe(viewLifecycleOwner) {
            binding.loadingPb.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    setupBudgetsList(it.data)
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun setupBudgetsList(dataset: List<MyFinBudget>) {
        binding.budgetsRv.layoutManager = LinearLayoutManager(context)
        binding.budgetsRv.adapter = BudgetsListAdapter(requireContext(), dataset, this)
        binding.budgetsRv.addItemDecoration(
            DividerItemDecoration(
                binding.budgetsRv.context,
                DividerItemDecoration.VERTICAL
            )
        )
        val snapHelper = LinearSnapHelper()
        binding.budgetsRv.clearOnScrollListeners()
        binding.budgetsRv.onFlingListener = null
        snapHelper.attachToRecyclerView(binding.budgetsRv)
        scrollToCurrentBudgetInList(dataset)
    }

    private fun scrollToCurrentBudgetInList(dataset: List<MyFinBudget>) {
        for (budget in dataset) {
            val currentDate = Calendar.getInstance()
            val isCurrentMonth =
                (budget.month.toIntOrNull() ?: 0) == currentDate.get(Calendar.MONTH) + 1
                        && (budget.year.toIntOrNull() ?: 0) == currentDate.get(Calendar.YEAR)

            if (isCurrentMonth) {
                smoothScrollToBudget(dataset.indexOf(budget))
                break
            }
        }
    }

    private fun smoothScrollToBudget(position: Int) {
        val smoothScroller = MyFinLinearSmoothScroller(binding.budgetsRv.context)

        smoothScroller.targetPosition = position
        binding.budgetsRv.layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun getBudgetsList() {
        viewModel.requestBudgetsList()
    }

    override fun onBudgetClick(budget: MyFinBudget) {
        val action =
            BudgetsFragmentDirections.actionBudgetsFragmentToBudgetDetailsFragment(
                budget.budgetId,
                parseStringToBoolean(budget.isOpen)
            )
        findNavController().safeNavigate(action)
    }

}