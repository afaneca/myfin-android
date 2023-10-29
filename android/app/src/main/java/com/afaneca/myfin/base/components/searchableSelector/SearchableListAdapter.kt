package com.afaneca.myfin.base.components.searchableSelector

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afaneca.myfin.databinding.SearchableListItemBinding
import java.util.*
import kotlin.collections.ArrayList

class SearchableListAdapter(
    private val dataset: List<SearchableListItem>,
    private val onItemSelectedCallback: (item: SearchableListItem) -> Unit,
) : ListAdapter<SearchableListItem, SearchableListAdapter.ViewHolder>(
    AsyncDifferConfig.Builder(
        DiffCallback()
    ).build()
), Filterable {
    var datasetFiltered: List<SearchableListItem> = ArrayList()

    init {
        datasetFiltered = dataset
        submitList(datasetFiltered)
    }


    inner class ViewHolder(val binding: SearchableListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchableListAdapter.ViewHolder {
        val binding =
            SearchableListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchableListAdapter.ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.apply {
            root.setOnClickListener { onItemSelectedCallback(item) }
            nameTv.text = item.name
            if (item.name.isNotBlank()) {
                descriptionTv.isVisible = true
                descriptionTv.text = item.description
            } else descriptionTv.isVisible = false
        }
    }


    override fun getItemCount() = datasetFiltered.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    datasetFiltered = dataset
                } else {
                    val resultList = ArrayList<SearchableListItem>()
                    for (item in dataset) {
                        if (item.toString().lowercase(Locale.ROOT).contains(
                                charSearch.lowercase(Locale.ROOT)
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
                datasetFiltered = results?.values as List<SearchableListItem>
                submitList(datasetFiltered)
            }

        }
    }


    /* DIFFUTIL */
    private class DiffCallback : DiffUtil.ItemCallback<SearchableListItem>() {
        override fun areItemsTheSame(oldItem: SearchableListItem, newItem: SearchableListItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SearchableListItem, newItem: SearchableListItem) =
            oldItem == newItem
    }
}