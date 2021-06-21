package com.afaneca.myfin.utils

import java.text.NumberFormat

/**
 * Created by me on 19/06/2021
 */

/**
 * Uses the given [value] and [currency] to return an adequately formatted string representation of the number as money
 */
fun formatMoney(value: Double, currency: String = "EUR"): String {
    /*val formatter = DecimalFormat("###,###,##0.00")
    return formatter.format(amount.toDouble())*/
    val format = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 2

    return format.format(value)
}