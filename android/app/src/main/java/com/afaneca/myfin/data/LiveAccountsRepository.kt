package com.afaneca.myfin.data

import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.domain.repository.AccountsRepository

/**
 * Created by me on 14/08/2021
 */
class LiveAccountsRepository(
    private val api: MyFinAPIServices,
    private val userData: UserDataManager
) : AccountsRepository, BaseRepository() {

    override suspend fun getAccountsList() = safeAPICall { api.getAccountsList() }
}