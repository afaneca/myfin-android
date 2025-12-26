package com.afaneca.myfin.domain.repository

import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.model.LoginResult
import com.afaneca.myfin.domain.common.Result

interface AuthGateway {
    suspend fun login(username: String, password: String): Result<LoginResult, DomainError>
    fun getSessionKey(): String?
    fun getUsername(): String?
    fun logout()
}
