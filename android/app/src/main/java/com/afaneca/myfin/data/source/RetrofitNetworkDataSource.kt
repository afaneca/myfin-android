package com.afaneca.myfin.data.source

import com.afaneca.myfin.data.model.AddTransactionStep0ResponseDto
import com.afaneca.myfin.data.model.AttemptLoginResponseDto
import com.afaneca.myfin.data.model.AutoCatResponseDto
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
    private val transientDataSource: TransientDataSource,
) : NetworkDataSource {

    override suspend fun attemptLogin(
        username: String,
        password: String,
    ): Result<AttemptLoginResponseDto, DomainError> {
        return safeApiCall(transientDataSource) {
            apiServices.attemptLogin(username, password)
        }
    }

    override suspend fun getTransactionsListByPage(
        page: Int,
        pageSize: Int,
        query: String?,
    ): Result<FilteredResultsByPageDto<TransactionDto>, DomainError> {
        return safeApiCall(transientDataSource) {
            apiServices.getTransactionsListByPage(page, pageSize, query)
        }
    }

    override suspend fun addTransactionStep0(): Result<AddTransactionStep0ResponseDto, DomainError> {
        return safeApiCall(transientDataSource) {
            apiServices.addTransactionStep0()
        }
    }

    override suspend fun createTransaction(
        amount: String,
        type: String,
        description: String?,
        entityId: Int?,
        accountFromId: Int?,
        accountToId: Int?,
        categoryId: Int?,
        dateTimestamp: Long,
        isEssential: Boolean,
    ): Result<Unit, DomainError> {
        return safeApiCall(transientDataSource) {
            apiServices.createTransaction(
                amount = amount,
                type = type,
                description = description,
                entityId = entityId,
                accountFromId = accountFromId,
                accountToId = accountToId,
                categoryId = categoryId,
                dateTimestamp = dateTimestamp,
                isEssential = isEssential,
            )
        }
    }

    override suspend fun autoCategorizeTransaction(
        description: String,
        amount: String,
        accountFromId: Int,
        accountToId: Int,
    ): Result<AutoCatResponseDto, DomainError> {
        return safeApiCall(transientDataSource) {
            apiServices.autoCategorizeTransaction(
                description = description,
                amount = amount,
                accountFromId = accountFromId,
                accountToId = accountToId,
            )
        }
    }
}
