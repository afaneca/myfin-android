package com.afaneca.myfin.closed.budgets.ui

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.afaneca.myfin.R
import com.afaneca.myfin.closed.transactions.data.MyFinBudgetCategory
import com.afaneca.myfin.databinding.FragmentBudgetDetailsCategoryBottomSheetBinding
import com.afaneca.myfin.utils.formatMoney
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BudgetDetailsCategoryBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBudgetDetailsCategoryBottomSheetBinding

    companion object {
        private const val BUNDLE_IN_BUDGET_CAT_OBJECT_TAG = "BUNDLE_IN_BUDGET_CAT_OBJECT_TAG"

        fun newInstance(catObj: MyFinBudgetCategory) =
            BudgetDetailsCategoryBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(BUNDLE_IN_BUDGET_CAT_OBJECT_TAG, catObj)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
                    BUNDLE_IN_BUDGET_CAT_OBJECT_TAG,
                    MyFinBudgetCategory::class.java
                ) as MyFinBudgetCategory
            } else {
                requireArguments().getSerializable(
                    BUNDLE_IN_BUDGET_CAT_OBJECT_TAG
                ) as? MyFinBudgetCategory
            }
            if (obj != null) {
                setupViews(obj)
            }
        }
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

    private fun setupViews(budgetCategory: MyFinBudgetCategory) {
        binding.obj = budgetCategory
        binding.formattedIncomeAmount =
            formatMoney(budgetCategory.currentAmountCredit.toDoubleOrNull() ?: 0.00)
        binding.formattedExpenseAmount =
            formatMoney(budgetCategory.currentAmountDebit.toDoubleOrNull() ?: 0.00)

        binding.plannedExpenseTv.addTextChangedListener { binding.saveBtn.isVisible =
            binding.plannedExpenseTv.text.toString()
                .toDoubleOrNull() != budgetCategory.plannedAmountDebit.toDoubleOrNull()
        }
        binding.plannedIncomeTv.addTextChangedListener { binding.saveBtn.isVisible =
            binding.plannedIncomeTv.text.toString()
                .toDoubleOrNull() != budgetCategory.plannedAmountCredit.toDoubleOrNull()
        }
    }
}