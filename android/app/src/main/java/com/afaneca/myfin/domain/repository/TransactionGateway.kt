package com.afaneca.myfin.domain.repository

import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.model.FilteredResultsByPage
import com.afaneca.myfin.domain.model.Transaction

interface TransactionGateway {
    suspend fun getTransactionsListByPage(page: Int, pageSize: Int, query: String? = null): Result<FilteredResultsByPage<Transaction>, DomainError>
}
