package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.model.TransactionType
import com.afaneca.myfin.domain.repository.TransactionGateway
import java.math.BigDecimal
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionGateway: TransactionGateway,
) : BaseUseCase<AddTransactionUseCase.Params, Unit, DomainError> {

    override suspend operator fun invoke(params: Params): Result<Unit, DomainError> {
        return transactionGateway.addTransaction(
            type = params.type,
            amount = params.amount,
            description = params.description,
            entityId = params.entityId,
            accountFromId = params.accountFromId,
            accountToId = params.accountToId,
            categoryId = params.categoryId,
            dateTimestamp = params.dateTimestamp,
            isEssential = params.isEssential,
        )
    }

    data class Params(
        val type: TransactionType,
        val amount: BigDecimal,
        val description: String?,
        val entityId: Int?,
        val accountFromId: Int?,
        val accountToId: Int?,
        val categoryId: Int?,
        val dateTimestamp: Long,
        val isEssential: Boolean,
    )
}

