package com.afaneca.myfin.data.model

import com.afaneca.myfin.domain.model.FilteredResultsByPage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FilteredResultsByPageDto<T>(
    @Json(name = "results") val results: List<T>,
    @Json(name = "filtered_count") val filteredCount: Int,
    @Json(name = "total_count") val totalCount: Int,
)

fun <T, R> FilteredResultsByPageDto<T>.toDomain(
    transformResultItem: (T) -> R,
): FilteredResultsByPage<R> {
    return FilteredResultsByPage(
        results = results.map(transformResultItem),
        filteredCount = filteredCount,
        totalCount = totalCount,
    )
}
