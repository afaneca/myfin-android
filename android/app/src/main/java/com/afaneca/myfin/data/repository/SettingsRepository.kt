package com.afaneca.myfin.data.repository

import com.afaneca.myfin.data.source.PersistentDataSource
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.repository.SettingsGateway
import kotlinx.coroutines.flow.Flow
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val persistentDataSource: PersistentDataSource
) : SettingsGateway {
    override suspend fun getBaseApiUrl(): Result<HttpUrl, DomainError> {
        val url = persistentDataSource.getBaseApiUrl().toHttpUrlOrNull()
            ?: return Result.Error(error = DomainError.InvalidFormat)
        return Result.Success(url)
    }

    override suspend fun setBaseApiUrl(url: String): Result<Boolean, DomainError> {
        if(url.toHttpUrlOrNull() == null) return Result.Error(error = DomainError.InvalidFormat)
        persistentDataSource.setBaseApiUrl(url)
        return Result.Success(true)
    }

    override suspend fun getUseDynamicTheme(): Result<Boolean, DomainError> {
        return Result.Success(persistentDataSource.getUseDynamicTheme())
    }

    override suspend fun setUseDynamicTheme(useDynamicTheme: Boolean): Result<Boolean, DomainError> {
        persistentDataSource.setUseDynamicTheme(useDynamicTheme)
        return Result.Success(true)
    }

    override fun observeUseDynamicTheme(): Flow<Boolean> {
        return persistentDataSource.observeUseDynamicTheme()
    }
}