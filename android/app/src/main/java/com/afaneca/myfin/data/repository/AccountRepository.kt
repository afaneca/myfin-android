package com.afaneca.myfin.data.repository

import com.afaneca.myfin.data.source.NetworkDataSource
import com.afaneca.myfin.data.source.TransientDataSource
import com.afaneca.myfin.domain.model.AccountType
import com.afaneca.myfin.domain.model.BalanceDigest
import com.afaneca.myfin.domain.model.UserAccount
import com.afaneca.myfin.domain.repository.AccountGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val transientDataSource: TransientDataSource,
) : AccountGateway {

    override fun refreshBalanceDigest(accounts: List<UserAccount>) {
        val balanceDigest = BalanceDigest(
            netWorth = accounts.sumOf { it.balance },
            operatingFunds = accounts.filter {
                it.type in listOf(
                    AccountType.CHECKING, AccountType.SAVINGS,
                    AccountType.MEAL, AccountType.WALLET,
                )
            }.sumOf { it.balance },
            investing = accounts.filter { it.type == AccountType.INVESTMENT }.sumOf { it.balance },
            debt = accounts.filter { it.type == AccountType.CREDIT }.sumOf { it.balance },
            currency = transientDataSource.getSessionData()?.currency ?: "EUR",
        )
        transientDataSource.setBalanceDigest(balanceDigest)
    }

    override fun observeBalanceDigest(): Flow<BalanceDigest> {
        return transientDataSource.observeBalanceDigest()
    }
}
