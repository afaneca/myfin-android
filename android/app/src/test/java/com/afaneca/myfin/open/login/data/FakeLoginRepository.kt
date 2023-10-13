package com.afaneca.myfin.open.login.data

import android.content.Context
import com.afaneca.myfin.data.db.accounts.UserAccountEntity
import com.afaneca.myfin.data.model.AttemptLoginResponse
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.domain.repository.LoginRepository

/**
 * Created by me on 25/09/2021
 */
class FakeLoginRepository : LoginRepository {

    override suspend fun attemptLogin(
        username: String,
        password: String
    ): Resource<AttemptLoginResponse> {
        return Resource.Failure("Error")
    }

    override fun saveSessionKeyToken(token: String) {
    }

    override fun saveUsername(username: String) {
    }

    override fun getLastUsername(): String? {
        return null
    }

    override fun saveIsToKeepSession(keepSession: Boolean) {
    }

    override fun getIsToKeepSession(): Boolean {
        return true
    }

    override fun saveEncryptedPassword(context: Context, password: String) {
    }

    override fun getPassword(context: Context): String {
        return ""
    }

    override fun saveUserAccounts(userAccounts: List<UserAccountEntity>) {
    }

    override fun getSessionKey(): String {
        return ""
    }
}