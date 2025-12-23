package com.afaneca.myfin.data.repository

import com.afaneca.myfin.data.model.toDomain
import com.afaneca.myfin.data.source.NetworkDataSource
import com.afaneca.myfin.data.source.TransientDataSource
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.common.map
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.model.FilteredResultsByPage
import com.afaneca.myfin.domain.model.Transaction
import com.afaneca.myfin.domain.repository.TransactionGateway
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val transientDataSource: TransientDataSource,
) : TransactionGateway {
    override suspend fun getTransactionsListByPage(
        page: Int,
        pageSize: Int,
        query: String?,
    ): Result<FilteredResultsByPage<Transaction>, DomainError> {
        val currency = transientDataSource.getSessionData()?.currency
        return networkDataSource.getTransactionsListByPage(
            page = page,
            pageSize = pageSize,
            query = query,
        ).map {
            it.toDomain(
                transformResultItem = { item -> item.toDomain(currency = currency) },
            )
        }
    }
}
