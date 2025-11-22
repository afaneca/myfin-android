package com.afaneca.myfin.domain.model

import java.math.BigDecimal

data class UserAccount(
    val id: Int,
    val name: String,
    val balance: BigDecimal,
    val type: AccountType,
    val description: String?,
    val status: String?,
    val colorGradient: String?,
    val isToExcludeFromBudgets: Boolean?,
)

enum class AccountType(val value: String) {
    CHECKING("CHEAC"),
    SAVINGS("SAVAC"),
    CREDIT("CREAC"),
    INVESTMENT("INVAC"),
    MEAL("MEALAC"),
    WALLET("WALLET"),
    UNKNOWN("");

    companion object {
        fun fromString(value: String?): AccountType {
            return AccountType.entries.find { it.value == value } ?: AccountType.UNKNOWN
        }
    }
}
