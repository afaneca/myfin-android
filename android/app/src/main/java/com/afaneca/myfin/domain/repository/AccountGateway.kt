package com.afaneca.myfin.domain.repository

import com.afaneca.myfin.domain.model.BalanceDigest
import com.afaneca.myfin.domain.model.UserAccount
import kotlinx.coroutines.flow.Flow

interface AccountGateway {
    fun refreshBalanceDigest(accounts: List<UserAccount>)
    fun observeBalanceDigest() : Flow<BalanceDigest>
}
