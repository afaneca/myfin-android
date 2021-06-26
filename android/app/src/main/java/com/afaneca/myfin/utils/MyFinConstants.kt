package com.afaneca.myfin.utils

/**
 * Created by me on 19/06/2021
 */
object MyFinConstants {
    const val DEFAULT_CURRENCY = "EUR"
    const val PASSWORD_STORAGE_KEY = "PASSWORD_STORAGE_KEY"

    enum class ACCOUNT_TYPE(val value: String) {
        CHECKING("CHEAC"),
        SAVINGS("SAVAC"),
        INVESTING("INVAC"),
        CREDIT("CREAC"),
        MEAL("MEALAC"),
        WALLET("WALLET"),
        OTHER("OTHAC")
    }

    enum class MYFIN_TRX_TYPE(val value: String) {
        INCOME("I"),
        EXPENSE("E"),
        TRANSFER("T")
    }
}