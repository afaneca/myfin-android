package com.afaneca.myfin.data.repository

import com.afaneca.myfin.data.model.AddTransactionStep0ResponseDto
import com.afaneca.myfin.data.model.toDomain
import com.afaneca.myfin.data.source.NetworkDataSource
import com.afaneca.myfin.data.source.TransientDataSource
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.common.map
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.model.AddTransactionFormData
import com.afaneca.myfin.domain.model.AutoCatSuggestion
import com.afaneca.myfin.domain.model.FilteredResultsByPage
import com.afaneca.myfin.domain.model.Transaction
import com.afaneca.myfin.domain.model.TransactionType
import com.afaneca.myfin.domain.repository.TransactionGateway
import java.math.BigDecimal
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

    override suspend fun getAddTransactionFormData(): Result<AddTransactionFormData, DomainError> {
        val step0Result: Result<AddTransactionStep0ResponseDto, DomainError> =
            networkDataSource.addTransactionStep0()
        return step0Result.map { dto: AddTransactionStep0ResponseDto ->
            AddTransactionFormData(
                accounts = dto.accounts?.map { acc -> acc.toDomain() } ?: emptyList(),
                categories = dto.categories?.map { cat -> cat.toDomain() } ?: emptyList(),
                entities = dto.entities?.map { ent -> ent.toDomain() } ?: emptyList(),
            )
        }
    }

    override suspend fun addTransaction(
        type: TransactionType,
        amount: BigDecimal,
        description: String?,
        entityId: Int?,
        accountFromId: Int?,
        accountToId: Int?,
        categoryId: Int?,
        dateTimestamp: Long,
        isEssential: Boolean,
    ): Result<Unit, DomainError> {
        return networkDataSource.createTransaction(
            amount = amount.toPlainString(),
            type = type.value,
            description = description,
            entityId = entityId,
            accountFromId = accountFromId,
            accountToId = accountToId,
            categoryId = categoryId,
            dateTimestamp = dateTimestamp,
            isEssential = isEssential,
        )
    }

    override suspend fun autoCategorizeTransaction(
        description: String,
        amount: BigDecimal,
        accountFromId: Int,
        accountToId: Int,
    ): Result<AutoCatSuggestion, DomainError> {
        return networkDataSource.autoCategorizeTransaction(
            description = description,
            amount = amount.toPlainString(),
            accountFromId = accountFromId,
            accountToId = accountToId,
        ).map { dto ->
            AutoCatSuggestion(categoryId = dto.categoryId, entityId = dto.entityId)
        }
    }
}
