package com.afaneca.myfin.utils

import java.text.NumberFormat

/**
 * Created by me on 19/06/2021
 */

fun formatMoney(value: Double, currency: String = "EUR"): String {
    /*val formatter = DecimalFormat("###,###,##0.00")
    return formatter.format(amount.toDouble())*/
    val format = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 2

    return format.format(value)
}