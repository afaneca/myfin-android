package com.afaneca.myfin.ui.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class SigningBehaviour {
    ALL, // Will show '+' for positive and '-' for negative
    NEGATIVE_ONLY, // Will only show '-' for negative
    NONE // Will show no signs
}

fun BigDecimal.formatAsCurrency(
    currency: String? = "EUR",
    signingBehaviour: SigningBehaviour = SigningBehaviour.NEGATIVE_ONLY,
): String {
    val format = NumberFormat.getCurrencyInstance().apply {
        minimumIntegerDigits = 1
        maximumFractionDigits = 2
        val mappedCurrency = try {
            Currency.getInstance(currency)
        } catch (_: Exception) {
            Currency.getInstance("EUR")
        }

        this@apply.currency = mappedCurrency
    }

    val formattedAmount = format.format(this.abs())

    return when (signingBehaviour) {
        SigningBehaviour.ALL -> when {
            this > BigDecimal.ZERO -> "+ $formattedAmount"
            this < BigDecimal.ZERO -> "- $formattedAmount"
            else -> formattedAmount
        }

        SigningBehaviour.NEGATIVE_ONLY -> if (this < BigDecimal.ZERO) {
            "- $formattedAmount"
        } else {
            formattedAmount
        }

        SigningBehaviour.NONE -> formattedAmount
    }
}

fun Long.toDisplayDate(
    dateFormat: String = "dd MMM yyyy",
    timeZone: TimeZone = TimeZone.getDefault(),
): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(Date(this * 1000))
}
