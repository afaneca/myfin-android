package com.afaneca.myfin.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutoCatResponseDto(
    @Json(name = "category_id") val categoryId: Int?,
    @Json(name = "entity_id") val entityId: Int?,
)

