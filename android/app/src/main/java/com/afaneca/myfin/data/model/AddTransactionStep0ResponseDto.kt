package com.afaneca.myfin.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddTransactionStep0ResponseDto(
    @Json(name = "accounts") val accounts: List<UserAccountDto>?,
    @Json(name = "categories") val categories: List<CategoryDto>?,
    @Json(name = "entities") val entities: List<EntityDto>?,
)

