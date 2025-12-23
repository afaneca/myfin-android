package com.afaneca.myfin.data.source

import com.afaneca.myfin.data.model.AttemptLoginResponseDto
import com.afaneca.myfin.data.model.FilteredResultsByPageDto
import com.afaneca.myfin.data.model.TransactionDto
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.common.Result

interface NetworkDataSource {

    suspend fun attemptLogin(
        username: String,
        password: String
    ): Result<AttemptLoginResponseDto, DomainError>

    suspend fun getTransactionsListByPage(
        page: Int,
        pageSize: Int,
        query: String? = null,
    ): Result<FilteredResultsByPageDto<TransactionDto>, DomainError>
}
