package com.afaneca.myfin.domain.model

data class Category(
    val categoryId: Int,
    val name: String?,
    val description: String?,
    val excludeFromBudgets: Boolean,
    val isActive: Boolean,
)
