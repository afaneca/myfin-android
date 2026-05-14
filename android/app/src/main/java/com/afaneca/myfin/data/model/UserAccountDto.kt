package com.afaneca.myfin.data.model

import com.afaneca.myfin.domain.model.AccountType
import com.afaneca.myfin.domain.model.UserAccount
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

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

fun UserAccountDto.toDomain(): UserAccount = UserAccount(
    id = accountId ?: 0,
    name = name.orEmpty(),
    balance = balance?.toBigDecimalOrNull() ?: BigDecimal.ZERO,
    type = AccountType.fromString(type),
    description = description,
    status = status,
    colorGradient = color_gradient,
    isToExcludeFromBudgets = isToExcludeFromBudgets ?: false,
)
