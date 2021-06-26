package com.afaneca.myfin.open.login.data

import android.content.Context
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.MyFinDatabase
import com.afaneca.myfin.data.db.accounts.UserAccountEntity
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.utils.MyFinConstants
import com.afaneca.myfin.utils.PasswordStorageHelper
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

    fun getLastUsername(): String? {
        return userData.getLastUsername()
    }

    fun saveIsToKeepSession(keepSession: Boolean) {
        userData.saveIsToKeepSession(keepSession)
    }

    fun getIsToKeepSession(): Boolean {
        return userData.getIsToKeepSession()
    }

    fun saveEncryptedPassword(context: Context, password: String) {
        val passwordStorageHelper = PasswordStorageHelper(context)
        passwordStorageHelper.setData(MyFinConstants.PASSWORD_STORAGE_KEY, password.toByteArray())
        //userData.saveEncryptedPassword(password)
    }

    fun getPassword(context: Context): String {
        val passwordStorageHelper = PasswordStorageHelper(context)
        return String(
            (passwordStorageHelper.getData(MyFinConstants.PASSWORD_STORAGE_KEY) ?: ByteArray(0))
        )
    }

    fun saveUserAccounts(userAccounts: List<UserAccountEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            db.userAccountsDao().insertAll(userAccounts)
        }

    }
}