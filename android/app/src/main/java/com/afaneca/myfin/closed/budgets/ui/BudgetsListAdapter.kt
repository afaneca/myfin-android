package com.afaneca.myfin.closed.budgets.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.afaneca.myfin.R
import com.afaneca.myfin.base.objects.MyFinBudget
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.databinding.BudgetsListItemBinding
import com.afaneca.myfin.utils.DateTimeUtils
import com.afaneca.myfin.utils.formatMoney
import com.afaneca.myfin.utils.parseStringToBoolean
import com.afaneca.myfin.utils.setupBalanceStyle
import java.util.*

class BudgetsListAdapter(
    private val context: Context,
    private val dataset: List<MyFinBudget>,
    private val clickListener: BudgetsListItemClickListener
) : RecyclerView.Adapter<BudgetsListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: BudgetsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindListener(clickListener: BudgetsListItemClickListener, item: MyFinBudget) {
            binding.root.setOnClickListener { clickListener.onBudgetClick(item) }
        }

    }

    interface BudgetsListItemClickListener {
        fun onBudgetClick(budget: MyFinBudget)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BudgetsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        holder.bindListener(clickListener, item)
        holder.binding.apply {
            val currentDate = Calendar.getInstance()
            val isCurrentMonth =
                (item.month.toIntOrNull() ?: 0) == currentDate.get(Calendar.MONTH) + 1
                        && (item.year.toIntOrNull() ?: 0) == currentDate.get(Calendar.YEAR)

            mainWrapperCv.setBackgroundColor(
                context.getColor(
                    if (isCurrentMonth) R.color.colorAccent
                    else android.R.color.transparent
                )
            )

            dateMonth.text = DateTimeUtils.convertMonthIntToString(item.month.toIntOrNull() ?: 1)
            dateYear.text = item.year
            budgetDescriptionTv.text = item.observations
            budgetBalanceTv.text = formatMoney(item.balanceValue)
            if (!isCurrentMonth)
                setupBalanceStyle(item.balanceValue, budgetBalanceTv)
            else
                budgetBalanceTv.setTextColor(context.getColor(R.color.design_default_color_on_primary))

            dateMonth.setTextColor(
                context.getColor(
                    if (isCurrentMonth) android.R.color.white
                    else R.color.colorAccent
                )
            )

            dateYear.setTextColor(
                context.getColor(
                    if (isCurrentMonth) android.R.color.white
                    else R.color.colorAccent
                )
            )

            dateIconIv.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    if (parseStringToBoolean(item.isOpen)) R.drawable.ic_baseline_lock_open_24
                    else R.drawable.ic_baseline_lock_24
                )
            )

            dateIconIv.setColorFilter(
                context.getColor(
                    if (isCurrentMonth) android.R.color.white
                    else R.color.colorAccent
                )
            )
        }
    }

    override fun getItemCount(): Int = dataset.size
    fun updateDataset(newDataset: List<MyFinBudget>) {
        (dataset as ArrayList<MyFinBudget>).clear()
        dataset.addAll(newDataset)
    }
}
