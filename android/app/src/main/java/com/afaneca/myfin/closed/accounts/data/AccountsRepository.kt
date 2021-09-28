package com.afaneca.myfin.closed.accounts.data

import com.afaneca.myfin.data.network.Resource

/**
 * Created by me on 05/09/2021
 */
interface AccountsRepository {
    suspend fun getAccountsList(): Resource<AccountsListResponse>
}