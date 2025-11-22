package com.afaneca.myfin.data.model

import com.afaneca.myfin.domain.model.AccountType
import com.afaneca.myfin.domain.model.LoginResult
import com.afaneca.myfin.domain.model.UserAccount
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttemptLoginResponseDto(
    @Json(name = "sessionkey")
    val sessionKey: String?,
    @Json(name = "username")
    val username: String?,
    @Json(name = "trustlimit")
    val trustLimit: Int?,
    @Json(name = "accounts")
    val accounts: List<UserAccountDto>?,
    @Json(name = "currency")
    val currency: String?,
)

fun AttemptLoginResponseDto.toDomain(): LoginResult {

    return LoginResult(
        sessionKey = sessionKey ?: "",
        username = username ?: "",
        trustLimit = trustLimit ?: 0,
        accounts = accounts?.map { dto ->
            UserAccount(
                id = dto.accountId ?: -1,
                name = dto.name ?: "",
                balance = dto.balance?.toBigDecimalOrNull() ?: 0.toBigDecimal(),
                type = AccountType.fromString(dto.type),
                description = dto.description,
                status = dto.status,
                colorGradient = dto.color_gradient,
                isToExcludeFromBudgets = dto.isToExcludeFromBudgets,
            )
        } ?: emptyList(),
        currency = currency ?: "EUR",
    )
}
