package com.afaneca.myfin.domain.repository

import android.content.Context
import com.afaneca.myfin.data.db.accounts.UserAccountEntity
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.data.model.AttemptLoginResponse

/**
 * Created by me on 05/09/2021
 */
interface LoginRepository {

    suspend fun attemptLogin(username: String, password: String): Resource<AttemptLoginResponse>
    fun saveSessionKeyToken(token: String)
    fun saveUsername(username: String)
    fun getLastUsername(): String?
    fun saveIsToKeepSession(keepSession: Boolean)
    fun getIsToKeepSession(): Boolean
    fun saveEncryptedPassword(context: Context, password: String)
    fun getPassword(context: Context): String
    fun saveUserAccounts(userAccounts: List<UserAccountEntity>)
    fun getSessionKey(): String
}