package com.afaneca.myfin.ui.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency

fun BigDecimal.formatAsCurrency(currency: String): String {
    val format = NumberFormat.getCurrencyInstance().apply {
        minimumIntegerDigits = 2
        maximumFractionDigits = 2
        val mappedCurrency = try {
            Currency.getInstance(currency)
        } catch (_: Exception) {
            Currency.getInstance("EUR")
        }

        this@apply.currency = mappedCurrency
    }

    return format.format(this)
}
