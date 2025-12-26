package com.afaneca.myfin.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.afaneca.myfin.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DataStorePersistentDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : PersistentDataSource {

    private val defaultBaseApiUrl = context.getString(R.string.default_api_base_url)
    private val defaultUseDynamicTheme = true

    private object PreferencesKeys {
        val BASE_API_URL = stringPreferencesKey("base_api_url")
        val USE_DYNAMIC_THEME = booleanPreferencesKey("use_dynamic_theme")
    }

    override suspend fun getBaseApiUrl(): String {
            val storedValue = context.settingsDataStore.data
                .firstOrNull()?.get(PreferencesKeys.BASE_API_URL) ?: defaultBaseApiUrl
            return storedValue
    }


    override suspend fun setBaseApiUrl(url: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.BASE_API_URL] = url
        }
    }

    override suspend fun getUseDynamicTheme(): Boolean =
        context.settingsDataStore.data
            .firstOrNull()?.get(PreferencesKeys.USE_DYNAMIC_THEME) ?: defaultUseDynamicTheme

    override suspend fun setUseDynamicTheme(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_DYNAMIC_THEME] = enabled
        }
    }

    override fun observeUseDynamicTheme(): Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USE_DYNAMIC_THEME] ?: defaultUseDynamicTheme
        }

    override fun clearUserData() {
        // No-op - no user data to clear
    }
}
