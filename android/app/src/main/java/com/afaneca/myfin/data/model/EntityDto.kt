package com.afaneca.myfin.data.model

import com.afaneca.myfin.domain.model.Entity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EntityDto(
    @Json(name = "entity_id") val entityId: Int?,
    @Json(name = "name") val name: String?,
)

fun EntityDto.toDomain(): Entity = Entity(
    entityId = entityId ?: 0,
    name = name,
)

