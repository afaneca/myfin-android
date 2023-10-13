package com.afaneca.myfin.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep
data class FilteredResultsByPage<T>(
    @SerializedName("results") val results: ArrayList<T>,
    @SerializedName("filtered_count") val filteredCount: Int,
    @SerializedName("total_count") val totalCount: Int
)
