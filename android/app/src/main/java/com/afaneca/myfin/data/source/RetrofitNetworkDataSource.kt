package com.afaneca.myfin.data.source

import com.afaneca.myfin.data.model.AttemptLoginResponseDto
import com.afaneca.myfin.data.network.MyFinApiServices
import com.afaneca.myfin.data.common.safeApiCall
import com.afaneca.myfin.data.model.FilteredResultsByPageDto
import com.afaneca.myfin.data.model.TransactionDto
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.common.Result
import javax.inject.Inject

/**
 * The concrete implementation of the [NetworkDataSource] that uses Retrofit to make network calls.
 */
class RetrofitNetworkDataSource @Inject constructor(
    private val apiServices: MyFinApiServices,
) : NetworkDataSource {

    override suspend fun attemptLogin(
        username: String,
        password: String,
    ): Result<AttemptLoginResponseDto, DomainError> {
        return safeApiCall {
            apiServices.attemptLogin(username, password)
        }
    }

    override suspend fun getTransactionsListByPage(
        page: Int,
        pageSize: Int,
        query: String?,
    ): Result<FilteredResultsByPageDto<TransactionDto>, DomainError> {
        return safeApiCall {
            apiServices.getTransactionsListByPage(page, pageSize, query)
        }
    }
}
