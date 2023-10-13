package com.afaneca.myfin.domain.repository

import com.afaneca.myfin.data.model.AccountsListResponse
import com.afaneca.myfin.data.network.Resource

/**
 * Created by me on 05/09/2021
 */
interface AccountsRepository {
    suspend fun getAccountsList(): Resource<AccountsListResponse>
}