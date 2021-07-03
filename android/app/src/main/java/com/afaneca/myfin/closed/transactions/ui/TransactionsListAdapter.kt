package com.afaneca.myfin.closed.transactions.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.afaneca.myfin.R
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.databinding.TransactionsListItemBinding
import com.afaneca.myfin.utils.DateUtils
import com.afaneca.myfin.utils.formatMoney
import com.afaneca.myfin.utils.setupAmountStyle
import com.afaneca.myfin.utils.visible
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by me on 26/06/2021
 */
class TransactionsListAdapter(
    private val context: Context,
    private val dataset: List<MyFinTransaction>,
    private val clickListener: TransactionsListItemClickListener
) : RecyclerView.Adapter<TransactionsListAdapter.ViewHolder>(), Filterable {
    var datasetFiltered: List<MyFinTransaction> = ArrayList()

    init {
        datasetFiltered = dataset
    }

    inner class ViewHolder(val binding: TransactionsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindListener(
            clickListener: TransactionsListItemClickListener,
            item: MyFinTransaction
        ) {
            binding.root.setOnClickListener { clickListener.onTransactionClick(item) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TransactionsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datasetFiltered[position]

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

        holder.bindListener(clickListener, item)
    }

    private fun setupAmountStyle(type: String, binding: TransactionsListItemBinding) {
        setupAmountStyle(type, binding.transactionAmountTv)
    }

    override fun getItemCount() = datasetFiltered.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    datasetFiltered = dataset
                } else {
                    val resultList = ArrayList<MyFinTransaction>()
                    for (item in dataset) {
                        if (item.toString().toLowerCase(Locale.ROOT).contains(
                                charSearch.toLowerCase(
                                    Locale.ROOT
                                )
                            )
                        )
                            resultList.add(item)
                    }
                    datasetFiltered = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = datasetFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                datasetFiltered = results?.values as List<MyFinTransaction>
                notifyDataSetChanged()
            }

        }
    }

    interface TransactionsListItemClickListener {
        fun onTransactionClick(trx: MyFinTransaction)
    }
}