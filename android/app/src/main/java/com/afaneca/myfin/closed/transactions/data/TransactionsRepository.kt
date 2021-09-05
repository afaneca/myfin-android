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
}