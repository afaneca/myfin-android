package com.afaneca.myfin.data

import android.content.Context
import com.afaneca.myfin.data.db.MyFinDatabase
import com.afaneca.myfin.data.db.accounts.UserAccountEntity
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.domain.repository.LoginRepository
import com.afaneca.myfin.utils.MyFinConstants
import com.afaneca.myfin.utils.PasswordStorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by me on 21/12/2020
 */
class LiveLoginRepository(
    private val api: MyFinAPIServices,
    private val userData: UserDataManager,
    private val db: MyFinDatabase
) : LoginRepository, BaseRepository() {

    override suspend fun attemptLogin(username: String, password: String) = safeAPICall {
        api.attemptLogin(username, password)
    }

    override fun saveSessionKeyToken(token: String) {
        userData.saveSessionKey(token)
    }

    override fun saveUsername(username: String) {
        userData.saveLastUser(username)
    }

    override fun getLastUsername(): String? {
        return userData.getLastUsername()
    }

    override fun saveIsToKeepSession(keepSession: Boolean) {
        userData.saveIsToKeepSession(keepSession)
    }

    override fun getIsToKeepSession(): Boolean {
        return userData.getIsToKeepSession()
    }

    override fun saveEncryptedPassword(context: Context, password: String) {
        val passwordStorageHelper = PasswordStorageHelper(context)
        passwordStorageHelper.setData(MyFinConstants.PASSWORD_STORAGE_KEY, password.toByteArray())
        //userData.saveEncryptedPassword(password)
    }

    override fun getPassword(context: Context): String {
        val passwordStorageHelper = PasswordStorageHelper(context)
        return String(
            (passwordStorageHelper.getData(MyFinConstants.PASSWORD_STORAGE_KEY) ?: ByteArray(0))
        )
    }

    override fun saveUserAccounts(userAccounts: List<UserAccountEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            db.userAccountsDao().insertAll(userAccounts)
        }
    }

    override fun getSessionKey() = userData.getSessionKey()

}