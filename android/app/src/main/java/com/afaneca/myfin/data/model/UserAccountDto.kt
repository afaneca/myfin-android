package com.afaneca.myfin.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAccountDto(
    @Json(name = "account_id")
    val accountId: Int?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "description")
    val description: String?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "color_gradient")
    val color_gradient: String?,
    @Json(name = "exclude_from_budgets")
    val isToExcludeFromBudgets: Boolean?,
    @Json(name = "balance")
    val balance: String?,
    @Json(name = "users_user_id")
    val usersUserId: Int?
)
