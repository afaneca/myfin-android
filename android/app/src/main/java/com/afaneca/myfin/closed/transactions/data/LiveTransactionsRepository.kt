package com.afaneca.myfin.closed.transactions.data

import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices

/**
 * Created by me on 20/06/2021
 */
class LiveTransactionsRepository(
    private val api: MyFinAPIServices,
) : TransactionsRepository, BaseRepository() {
    override suspend fun getTransactionsList(
        trxLimit: Int
    ) = safeAPICall { api.getLatestTransactionsList(trxLimit) }

    override suspend fun getTransactionsByPage(
        page: Int,
        pageSize: Int
    ) = safeAPICall { api.getTransactionsListByPage(page, pageSize) }

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
}