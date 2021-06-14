package com.afaneca.myfin.open.login.data

import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.MyFinDatabase
import com.afaneca.myfin.data.db.accounts.UserAccountEntity
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by me on 21/12/2020
 */
class LoginRepository(
    private val api: MyFinAPIServices,
    private val userData: UserDataManager,
    private val db: MyFinDatabase
) : BaseRepository() {

    suspend fun attemptLogin(username: String, password: String) = safeAPICall {
        api.attemptLogin(username, password)
    }

    fun saveSessionKeyToken(token: String) {
        userData.saveSessionKey(token)
    }

    fun saveUsername(username: String) {
        userData.saveLastUser(username)
    }

    fun saveUserAccounts(userAccounts: List<UserAccountEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            db.userAccountsDao().insertAll(userAccounts)
        }

    }
}