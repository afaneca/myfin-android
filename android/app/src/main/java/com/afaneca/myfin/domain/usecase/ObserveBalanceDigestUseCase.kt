package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.model.BalanceDigest
import com.afaneca.myfin.domain.repository.AccountGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBalanceDigestUseCase @Inject constructor(
    private val accountGateway: AccountGateway,
) {
    operator fun invoke(): Flow<BalanceDigest> {
        return accountGateway.observeBalanceDigest()
    }
}
