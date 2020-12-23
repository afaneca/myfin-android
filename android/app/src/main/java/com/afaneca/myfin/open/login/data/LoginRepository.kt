package com.afaneca.myfin.open.login.data

import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices

/**
 * Created by me on 21/12/2020
 */
class LoginRepository(private val api: MyFinAPIServices) : BaseRepository() {

    suspend fun attemptLogin(username: String, password: String) = safeAPICall {
        api.attemptLogin(username, password)
    }

}