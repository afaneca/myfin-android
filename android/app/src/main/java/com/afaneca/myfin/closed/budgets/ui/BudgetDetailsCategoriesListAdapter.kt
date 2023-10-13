package com.afaneca.myfin.closed.budgets.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afaneca.myfin.R
import com.afaneca.myfin.data.model.MyFinBudgetCategory
import com.afaneca.myfin.databinding.BudgetDetailsCategoriesListItemBinding
import com.afaneca.myfin.utils.formatMoney
import com.afaneca.myfin.utils.setProgressBarValueWithAnimation
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlin.math.roundToInt

/**
 * Created by me on 08/08/2021
 */
class BudgetDetailsCategoriesListAdapter(
    private val context: Context,
    private val dataset: List<MyFinBudgetCategory>,
    private val clickListener: BudgetDetailsCategoryListItemClickListener,
    private val isExpenses: Boolean
) : RecyclerView.Adapter<BudgetDetailsCategoriesListAdapter.ViewHolder>() {

    interface BudgetDetailsCategoryListItemClickListener {
        fun onCategoryClick(cat: MyFinBudgetCategory)
    }

    inner class ViewHolder(val binding: BudgetDetailsCategoriesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindListener(
            clickListener: BudgetDetailsCategoryListItemClickListener,
            item: MyFinBudgetCategory
        ) = binding.root.setOnClickListener { clickListener.onCategoryClick(item) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BudgetDetailsCategoriesListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        val currentPercentageOfPlanned =
            if (isExpenses && (item.plannedAmountDebit.toDoubleOrNull() ?: 0.0) == 0.0) 0.0
            else if (!isExpenses && (item.plannedAmountCredit.toDoubleOrNull() ?: 0.0) == 0.0) 0.0
            else if (isExpenses) (item.currentAmountDebit.toDoubleOrNull()
                ?: 0.0) / (item.plannedAmountDebit.toDoubleOrNull() ?: 0.0) * 100
            else (item.currentAmountCredit.toDoubleOrNull()
                ?: 0.0) / (item.plannedAmountCredit.toDoubleOrNull() ?: 0.0) * 100

        holder.bindListener(clickListener, item)
        holder.binding.apply {
            setupProgressBar(holder.binding.progressBarPb, currentPercentageOfPlanned)
            holder.binding.nameTv.text = item.name
            holder.binding.currentAmountTv.text =
                formatMoney(
                    if (isExpenses) item.currentAmountDebit.toDoubleOrNull()
                        ?: 0.0 else item.currentAmountCredit.toDoubleOrNull() ?: 0.0
                )
            holder.binding.percentOfPlannedAmountTv.text = String.format(
                context.getString(R.string.budget_details_category_percent_of_planned_amount),
                currentPercentageOfPlanned,
                if (isExpenses) formatMoney(
                    item.plannedAmountDebit.toDoubleOrNull() ?: 0.0
                ) else formatMoney(item.plannedAmountCredit.toDoubleOrNull() ?: 0.0)
            )
        }
    }

    private fun setupProgressBar(
        progressBar: CircularProgressIndicator,
        value: Double
    ) {
        setProgressBarValueWithAnimation(progressBar, value.roundToInt())
        /*progressBar.progress = value.roundToInt()*/
    }

    override fun getItemCount(): Int = dataset.size


}