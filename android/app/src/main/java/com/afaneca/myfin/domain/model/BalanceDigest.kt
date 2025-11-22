package com.afaneca.myfin.domain.model

import java.math.BigDecimal

data class BalanceDigest(
    val netWorth: BigDecimal = 0.toBigDecimal(),
    val operatingFunds: BigDecimal = 0.toBigDecimal(),
    val investing: BigDecimal = 0.toBigDecimal(),
    val debt: BigDecimal = 0.toBigDecimal(),
    val currency: String = "EUR",
)
