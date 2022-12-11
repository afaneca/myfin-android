package com.afaneca.myfin.closed.transactions.data

import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices

/**
 * Created by me on 20/06/2021
 */
class LiveTransactionsRepository(
    private val api: MyFinAPIServices,
    private val userData: UserDataManager
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
}