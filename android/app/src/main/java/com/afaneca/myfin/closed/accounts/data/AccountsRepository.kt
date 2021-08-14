package com.afaneca.myfin.closed.accounts.data

import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices

/**
 * Created by me on 14/08/2021
 */
class AccountsRepository(
    private val api: MyFinAPIServices,
    private val userData: UserDataManager
) : BaseRepository() {

    suspend fun getAccountsList() = safeAPICall { api.getAccountsList() }
}