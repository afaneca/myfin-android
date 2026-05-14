package com.afaneca.myfin.domain.repository

import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.model.AddTransactionFormData
import com.afaneca.myfin.domain.model.AutoCatSuggestion
import com.afaneca.myfin.domain.model.FilteredResultsByPage
import com.afaneca.myfin.domain.model.Transaction
import com.afaneca.myfin.domain.model.TransactionType
import java.math.BigDecimal

interface TransactionGateway {
    suspend fun getTransactionsListByPage(page: Int, pageSize: Int, query: String? = null): Result<FilteredResultsByPage<Transaction>, DomainError>
    suspend fun getAddTransactionFormData(): Result<AddTransactionFormData, DomainError>
    suspend fun addTransaction(
        type: TransactionType,
        amount: BigDecimal,
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
        amount: BigDecimal,
        accountFromId: Int,
        accountToId: Int,
    ): Result<AutoCatSuggestion, DomainError>
}
