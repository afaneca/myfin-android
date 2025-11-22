package com.afaneca.myfin.domain.repository

import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.common.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.HttpUrl

interface SettingsGateway {
    suspend fun getBaseApiUrl(): Result<HttpUrl, DomainError>
    suspend fun setBaseApiUrl(url: String): Result<Boolean, DomainError>

    suspend fun getUseDynamicTheme(): Result<Boolean, DomainError>
    suspend fun setUseDynamicTheme(useDynamicTheme: Boolean): Result<Boolean, DomainError>
    fun observeUseDynamicTheme(): Flow<Boolean>
}