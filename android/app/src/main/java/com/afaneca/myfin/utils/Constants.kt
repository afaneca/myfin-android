package com.afaneca.myfin.utils

/**
 * Created by me on 19/06/2021
 */
object Constants {
    const val DEFAULT_CURRENCY = "EUR"

    enum class ACCOUNT_TYPE(val value: String){
        CHECKING("CHEAC"),
        SAVINGS("SAVAC"),
        INVESTING("INVAC"),
        CREDIT("CREAC"),
        MEAL("MEALAC"),
        WALLET("WALLET"),
        OTHER("OTHAC")
    }
}