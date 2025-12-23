package com.afaneca.myfin.domain.model

data class FilteredResultsByPage<T>(
    val results: List<T>,
    val filteredCount: Int,
    val totalCount: Int,
)
