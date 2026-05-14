package com.afaneca.myfin.data.source

import com.afaneca.myfin.data.model.AddTransactionStep0ResponseDto
import com.afaneca.myfin.data.model.AttemptLoginResponseDto
import com.afaneca.myfin.data.model.AutoCatResponseDto
import com.afaneca.myfin.data.model.FilteredResultsByPageDto
import com.afaneca.myfin.data.model.TransactionDto
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.common.Result

interface NetworkDataSource {

    suspend fun attemptLogin(
        username: String,
        password: String,
    ): Result<AttemptLoginResponseDto, DomainError>

    suspend fun getTransactionsListByPage(
        page: Int,
        pageSize: Int,
        query: String? = null,
    ): Result<FilteredResultsByPageDto<TransactionDto>, DomainError>

    suspend fun addTransactionStep0(): Result<AddTransactionStep0ResponseDto, DomainError>

    suspend fun createTransaction(
        amount: String,
        type: String,
        description: String?,
        entityId: Int?,
        accountFromId: Int?,
        accountToId: Int?,
        categoryId: Int?,
        dateTimestamp: Long,
        isEssential: Boolean,
    ): Result<Unit, DomainError>

    suspend fun autoCategorizeTransaction(
        description: String,
        amount: String,
        accountFromId: Int,
        accountToId: Int,
    ): Result<AutoCatResponseDto, DomainError>
}
