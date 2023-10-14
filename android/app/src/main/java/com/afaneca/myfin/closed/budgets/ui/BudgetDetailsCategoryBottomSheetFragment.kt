package com.afaneca.myfin.closed.budgets.ui

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afaneca.myfin.R
import com.afaneca.myfin.data.model.MyFinBudgetCategory
import com.afaneca.myfin.databinding.FragmentBudgetDetailsCategoryBottomSheetBinding
import com.afaneca.myfin.utils.formatMoney
import com.afaneca.myfin.utils.safeNavigate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BudgetDetailsCategoryBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBudgetDetailsCategoryBottomSheetBinding
    private val viewModel: BudgetDetailsCategoryViewModel by viewModels()

    companion object {
        private const val BUNDLE_IN_BUDGET_CAT_OBJECT_TAG = "BUNDLE_IN_BUDGET_CAT_OBJECT_TAG"
        private const val BUNDLE_IN_BUDGET_CAT_BUDGET_ID_TAG = "BUNDLE_IN_BUDGET_CAT_BUDGET_ID_TAG"

        fun newInstance(budgetId: String, catObj: MyFinBudgetCategory) =
            BudgetDetailsCategoryBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(BUNDLE_IN_BUDGET_CAT_OBJECT_TAG, catObj)
                    putString(BUNDLE_IN_BUDGET_CAT_BUDGET_ID_TAG, budgetId)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentBudgetDetailsCategoryBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val obj = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireArguments().getSerializable(
                    BUNDLE_IN_BUDGET_CAT_OBJECT_TAG, MyFinBudgetCategory::class.java
                ) as MyFinBudgetCategory
            } else {
                requireArguments().getSerializable(
                    BUNDLE_IN_BUDGET_CAT_OBJECT_TAG
                ) as? MyFinBudgetCategory
            }

            val budgetId = requireArguments().getString(BUNDLE_IN_BUDGET_CAT_BUDGET_ID_TAG, "")

            if (obj != null) {
                setupViews(obj, budgetId)
            }
        }

        bindObservers()
    }

    private fun bindObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) {
            binding.loadingPb.root.isVisible = it is BudgetDetailsCategoryViewModel.State.Loading
            binding.saveBtn.isEnabled = it !is BudgetDetailsCategoryViewModel.State.Loading
            binding.plannedExpenseTil.isEnabled =
                it !is BudgetDetailsCategoryViewModel.State.Loading
            binding.plannedIncomeTil.isEnabled = it !is BudgetDetailsCategoryViewModel.State.Loading
        }

        viewModel.viewEvent.observe(viewLifecycleOwner) {
            when (it) {
                is BudgetDetailsCategoryViewModel.Event.ShowError -> Toast.makeText(
                    requireContext(),
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
                is BudgetDetailsCategoryViewModel.Event.GoToBudget -> goBackToBudget(it.budgetId)
            }
        }
    }

    private fun goBackToBudget(budgetId: String) {
        refreshCurrentFragment(budgetId)
        dismiss()
    }

    private fun refreshCurrentFragment(budgetId: String) {
        val id = findNavController().currentDestination?.id
        findNavController().popBackStack(id!!, true)
        val action =
            BudgetsFragmentDirections.actionBudgetsFragmentToBudgetDetailsFragment(
                budgetId,
                true
            )
        findNavController().safeNavigate(action)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.also {
            val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)

            binding.root.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    try {
                        binding.root.viewTreeObserver.removeGlobalOnLayoutListener(this)
                        behavior.peekHeight = binding.root.height
                        view?.requestLayout()
                    } catch (e: Exception) {
                    }
                }
            })
        }
    }

    private fun setupViews(budgetCategory: MyFinBudgetCategory, budgetId: String) {
        binding.obj = budgetCategory
        binding.formattedIncomeAmount =
            formatMoney(budgetCategory.currentAmountCredit.toDoubleOrNull() ?: 0.00)
        binding.formattedExpenseAmount =
            formatMoney(budgetCategory.currentAmountDebit.toDoubleOrNull() ?: 0.00)

        binding.plannedExpenseTv.addTextChangedListener {
            binding.saveBtn.isVisible = binding.plannedExpenseTv.text.toString()
                .toDoubleOrNull() != budgetCategory.plannedAmountDebit.toDoubleOrNull()
        }
        binding.plannedIncomeTv.addTextChangedListener {
            binding.saveBtn.isVisible = binding.plannedIncomeTv.text.toString()
                .toDoubleOrNull() != budgetCategory.plannedAmountCredit.toDoubleOrNull()
        }

        binding.saveBtn.setOnClickListener {
            viewModel.performAction(
                BudgetDetailsCategoryViewModel.Action.OnSaveButtonClick(
                    budgetId,
                    budgetCategory.categoryId,
                    binding.plannedExpenseTv.text.toString(),
                    binding.plannedIncomeTv.text.toString()
                )
            )
        }
    }
}