package com.afaneca.myfin.data.source

import kotlinx.coroutines.flow.Flow

interface PersistentDataSource {
    suspend fun getBaseApiUrl(): String
    suspend fun setBaseApiUrl(url: String)

    suspend fun getUseDynamicTheme(): Boolean
    suspend fun setUseDynamicTheme(enabled: Boolean)
    fun observeUseDynamicTheme(): Flow<Boolean>
}
