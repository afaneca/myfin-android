package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.model.FilteredResultsByPage
import com.afaneca.myfin.domain.model.Transaction
import com.afaneca.myfin.domain.repository.TransactionGateway
import javax.inject.Inject

class GetTransactionListByPageUseCase @Inject constructor(
    private val transactionGateway: TransactionGateway,
) : BaseUseCase<GetTransactionListByPageUseCase.Params, FilteredResultsByPage<Transaction>, GetTransactionListByPageUseCase.FeatureError> {

    data class Params(val page: Int, val pageSize: Int, val query: String? = null)

    override suspend operator fun invoke(params: Params): Result<FilteredResultsByPage<Transaction>, FeatureError> {
        return when (val result = transactionGateway.getTransactionsListByPage(
            page = params.page,
            pageSize = params.pageSize,
            query = params.query,
        )) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> {
                Result.Error(FeatureError.GenericError)
            }
        }
    }

    sealed interface FeatureError {
        data object GenericError : FeatureError
    }
}
