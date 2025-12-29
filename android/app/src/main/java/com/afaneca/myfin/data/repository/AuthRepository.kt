package com.afaneca.myfin.data.repository

import com.afaneca.myfin.data.model.toDomain
import com.afaneca.myfin.data.source.NetworkDataSource
import com.afaneca.myfin.data.source.PersistentDataSource
import com.afaneca.myfin.data.source.TransientDataSource
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.model.LoginResult
import com.afaneca.myfin.domain.repository.AuthGateway
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.common.map
import com.afaneca.myfin.domain.model.SessionData
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val transientDataSource: TransientDataSource,
    private val persistentDataSource: PersistentDataSource,
) : AuthGateway {

    override suspend fun login(username: String, password: String): Result<LoginResult, DomainError> {
        val data = networkDataSource.attemptLogin(username, password).map { it.toDomain() }
        (data as? Result.Success)?.data?.let {
            transientDataSource.setSessionData(SessionData(
                username = it.username,
                sessionKey = it.sessionKey,
                currency = it.currency,
            ))
            persistentDataSource.setSavedUser(username)
            // Mark user as authenticated after successful login
            transientDataSource.setAuthenticated()
        }

        return data
    }

    override fun getSessionKey(): String? {
        return transientDataSource.getSessionData()?.sessionKey
    }

    override fun getUsername(): String? {
        return transientDataSource.getSessionData()?.username
    }

    override suspend fun getSavedUsername(): String? {
        return persistentDataSource.getSavedUser()
    }

    override suspend fun logout() {
        transientDataSource.clearUserData()
        persistentDataSource.clearUserData()
    }
}
