package com.afaneca.myfin.closed.transactions.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.afaneca.myfin.R
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.databinding.TransactionsListItemBinding
import com.afaneca.myfin.utils.DateUtils
import com.afaneca.myfin.utils.MyFinConstants
import com.afaneca.myfin.utils.formatMoney
import com.afaneca.myfin.utils.visible

/**
 * Created by me on 26/06/2021
 */
class TransactionsListAdapter(
    private val context: Context,
    private val dataset: List<MyFinTransaction>
) : RecyclerView.Adapter<TransactionsListAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: TransactionsListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TransactionsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]

        holder.binding.apply {
            dateDay.text = DateUtils.getDayOfMonthFromUnixTime(item.dateTimestamp.toLong() * 1000L)
            dateMonthYear.text = String.format(
                context.getString(R.string.transactions_list_item_month_year_format),
                DateUtils.getAbbreviatedMonthFromUnixTime(item.dateTimestamp.toLong() * 1000L),
                DateUtils.getFullYearFromUnixTime(item.dateTimestamp.toLong() * 1000L)
            ) //"Jun 2021"
            transactionDescriptionTv.text = item.description ?: ""
            transactionAmountTv.text = formatMoney(item.amount.toDoubleOrNull() ?: 0.00)
            transactionEntityTv.text = item.entityName ?: ""
            transactionCategoryTv.text = item.categoryName ?: ""
            categoryEntityDividerView.visible(!item.entityName.isNullOrBlank() && !item.categoryName.isNullOrBlank())
            setupAmountStyle(item.type, holder.binding)
        }
    }

    private fun setupAmountStyle(type: String, binding: TransactionsListItemBinding) {
        TextViewCompat.setTextAppearance(
            binding.transactionAmountTv,
            when (type) {
                MyFinConstants.MYFIN_TRX_TYPE.INCOME.value -> R.style.AmountTypeCredit
                MyFinConstants.MYFIN_TRX_TYPE.EXPENSE.value -> R.style.AmountTypeDebit
                else -> R.style.AmountTypeTransfer
            }
        )
    }

    override fun getItemCount() = dataset.size
}