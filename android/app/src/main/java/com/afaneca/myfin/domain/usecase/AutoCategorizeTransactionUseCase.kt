package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.model.AutoCatSuggestion
import com.afaneca.myfin.domain.repository.TransactionGateway
import java.math.BigDecimal
import javax.inject.Inject

class AutoCategorizeTransactionUseCase @Inject constructor(
    private val transactionGateway: TransactionGateway,
) : BaseUseCase<AutoCategorizeTransactionUseCase.Params, AutoCatSuggestion, DomainError> {

    override suspend operator fun invoke(params: Params): Result<AutoCatSuggestion, DomainError> {
        return transactionGateway.autoCategorizeTransaction(
            description = params.description,
            amount = params.amount,
            accountFromId = params.accountFromId ?: 0,
            accountToId = params.accountToId ?: 0,
        )
    }

    data class Params(
        val description: String,
        val amount: BigDecimal,
        val accountFromId: Int?,
        val accountToId: Int?,
    )
}

