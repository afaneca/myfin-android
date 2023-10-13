package com.afaneca.myfin.data

import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.domain.repository.TransactionsRepository

/**
 * Created by me on 20/06/2021
 */
class LiveTransactionsRepository(
    private val api: MyFinAPIServices,
) : TransactionsRepository, BaseRepository() {
    override suspend fun getFilteredTransactionsByPage(
        page: Int,
        pageSize: Int,
        query: String?
    ) = safeAPICall { api.getTransactionsListByPage(page, pageSize, query) }

    override suspend fun addTransactionStep0() = safeAPICall { api.addTransactionStep0() }

    override suspend fun addTransactionStep1(
        dateTimestamp: Long,
        amount: String,
        type: Char,
        accountFromId: String?,
        accountToId: String?,
        description: String,
        entityId: String?,
        categoryId: String?,
        isEssential: Boolean
    ) = safeAPICall {
        api.addTransactionStep1(
            amount,
            type,
            description,
            entityId,
            accountFromId,
            accountToId,
            categoryId,
            dateTimestamp,
            isEssential
        )
    }

    override suspend fun updateTransaction(
        transactionId: Int,
        newDateTimestamp: Long,
        newAmount: String,
        newType: Char,
        newAccountFromId: String?,
        newAccountToId: String?,
        newDescription: String,
        newEntityId: String?,
        newCategoryId: String?,
        newIsEssential: Boolean
    ) = safeAPICall {
        api.updateTransaction(
            transactionId,
            newAmount,
            newType,
            newDescription,
            newEntityId,
            newAccountFromId,
            newAccountToId,
            newCategoryId,
            newDateTimestamp,
            newIsEssential
        )
    }

    override suspend fun removeTransaction(
        transactionId: Int,
    ) = safeAPICall {
        api.deleteTransaction(transactionId)
    }
}