package com.afaneca.myfin.domain.repository

import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.data.model.AddTransactionStep0Response
import com.afaneca.myfin.data.model.FilteredResultsByPage
import com.afaneca.myfin.data.network.Resource

/**
 * Created by me on 05/09/2021
 */
interface TransactionsRepository {

    suspend fun getFilteredTransactionsByPage(
        page: Int,
        pageSize: Int,
        query: String?
    ): Resource<FilteredResultsByPage<MyFinTransaction>>

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

    suspend fun removeTransaction(
        transactionId: Int,
    ): Resource<Unit>
}