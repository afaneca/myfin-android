package com.afaneca.myfin.data.source

import com.afaneca.myfin.domain.model.BiometricCredentials
import kotlinx.coroutines.flow.Flow

interface PersistentDataSource {
    suspend fun getBaseApiUrl(): String
    suspend fun setBaseApiUrl(url: String)

    suspend fun getUseDynamicTheme(): Boolean
    suspend fun setUseDynamicTheme(enabled: Boolean)
    fun observeUseDynamicTheme(): Flow<Boolean>

    suspend fun getBiometricCredentials(): BiometricCredentials?
    suspend fun setBiometricCredentials(credentials: BiometricCredentials)
    suspend fun isBiometricEnabled(): Boolean
    suspend fun clearBiometricCredentials()

    suspend fun getSavedUser(): String?
    suspend fun setSavedUser(username: String)

    suspend fun clearUserData()
}
