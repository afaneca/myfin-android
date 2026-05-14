package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.model.AddTransactionFormData
import com.afaneca.myfin.domain.repository.TransactionGateway
import javax.inject.Inject

class GetAddTransactionFormDataUseCase @Inject constructor(
    private val transactionGateway: TransactionGateway,
) : BaseUseCase<Unit, AddTransactionFormData, DomainError> {
    override suspend operator fun invoke(params: Unit): Result<AddTransactionFormData, DomainError> {
        return transactionGateway.getAddTransactionFormData()
    }
}

