package com.afaneca.myfin.data.model

import com.afaneca.myfin.domain.model.Category
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryDto(
    @Json(name = "category_id") val categoryId: Int?,
    @Json(name = "name") val name: String?,
    @Json(name = "description") val description: String?,
    @Json(name = "exclude_from_budgets") val excludeFromBudgets: Int?,
    @Json(name = "is_active") val isActive: Int?,
)

fun CategoryDto.toDomain(): Category = Category(
    categoryId = categoryId ?: 0,
    name = name,
    description = description,
    excludeFromBudgets = excludeFromBudgets == 1,
    isActive = isActive != 0,
)

