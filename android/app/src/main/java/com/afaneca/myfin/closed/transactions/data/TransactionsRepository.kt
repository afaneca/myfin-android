package com.afaneca.myfin.closed.transactions.data

import com.afaneca.myfin.data.network.Resource

/**
 * Created by me on 05/09/2021
 */
interface TransactionsRepository {

    suspend fun getTransactionsList(trxLimit: Int): Resource<LatestTransactionsListResponse>
    suspend fun getTransactionsByPage(
        page: Int,
        pageSize: Int
    ): Resource<LatestTransactionsListResponse>

    suspend fun addTransactionStep0(): Resource<AddTransactionStep0Response>
    suspend fun addTransactionStep1(
        dateTimestamp: Long,
        amount: String,
        type: Char,
        accountFromId: String?,
        accountToId: String?,
        description: String,
        entityId: String?,
        categoryId: String?,
        isEssential: Boolean
    ): Resource<Unit>

    suspend fun updateTransaction(
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
    ): Resource<Unit>
}