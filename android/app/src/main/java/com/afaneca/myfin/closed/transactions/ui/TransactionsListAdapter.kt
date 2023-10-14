package com.afaneca.myfin.closed.transactions.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.afaneca.myfin.R
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.databinding.TransactionsListItemBinding
import com.afaneca.myfin.utils.*
import java.util.*

/**
 * Created by me on 26/06/2021
 */
class TransactionsListAdapter(
    private val context: Context,
    private var dataset: List<MyFinTransaction>,
    private val clickListener: TransactionsListItemClickListener,
) : RecyclerView.Adapter<TransactionsListAdapter.ViewHolder>() {
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
            dateTv.text = String.format(
                context.getString(R.string.transactions_list_item_date_format),
                DateTimeUtils.getDayOfMonthFromUnixTime(item.dateTimestamp.toLong() * 1000L),
                DateTimeUtils.getAbbreviatedMonthFromUnixTime(item.dateTimestamp.toLong() * 1000L),
                DateTimeUtils.getFullYearFromUnixTime(item.dateTimestamp.toLong() * 1000L)
            ) //"30 Jun 2021"
            transactionDescriptionTv.text = item.description ?: ""
            transactionAmountTv.text = formatMoney(item.amount.toDoubleOrNull() ?: 0.00)
            transactionEntityTv.text = item.entityName ?: ""
            transactionCategoryTv.text = item.categoryName ?: ""
            categoryEntityDividerView.visible(!item.entityName.isNullOrBlank() && !item.categoryName.isNullOrBlank())
            essentialInclude.root.isVisible = parseStringToBoolean(item.isEssential)
            setupAmountStyle(item.type, holder.binding)
            setupIconStyle(item.type, holder.binding.iconIv)
        }

        holder.bindListener(clickListener, item)
    }

    private fun setupIconStyle(type: String, imageView: ImageView) {
        when (type) {
            MyFinConstants.MYFIN_TRX_TYPE.INCOME.value -> {
                /*imageView.setColorFilter(ContextCompat.getColor(context, R.color.colorGreen))*/
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_login_black_24dp
                    )
                )
            }
            MyFinConstants.MYFIN_TRX_TYPE.EXPENSE.value -> {
                /*imageView.setColorFilter(ContextCompat.getColor(context, R.color.colorRed))*/
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_logout_24dp
                    )
                )
            }
            else -> {
                /*imageView.setColorFilter(ContextCompat.getColor(context, R.color.colorOrange))*/
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_sync_alt_black_24dp
                    )
                )
            }
        }


    }

    fun updateDataset(newDataset: List<MyFinTransaction>) {
        (dataset as ArrayList<MyFinTransaction>).clear()
        (dataset as ArrayList<MyFinTransaction>).addAll(newDataset)
    }

    private fun setupAmountStyle(type: String, binding: TransactionsListItemBinding) {
        setupAmountStyle(type, binding.transactionAmountTv)
    }

    override fun getItemCount() = datasetFiltered.size

    interface TransactionsListItemClickListener {
        fun onTransactionClick(trx: MyFinTransaction)
    }
}