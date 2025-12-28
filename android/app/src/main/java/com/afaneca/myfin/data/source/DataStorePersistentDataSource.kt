package com.afaneca.myfin.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.afaneca.myfin.R
import com.afaneca.myfin.domain.model.BiometricCredentials
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
        val BIOMETRIC_USERNAME = stringPreferencesKey("biometric_username")
        val BIOMETRIC_ENCRYPTED_PASSWORD = stringPreferencesKey("biometric_encrypted_password")
        val BIOMETRIC_IV = stringPreferencesKey("biometric_iv")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val SAVED_USER = stringPreferencesKey("saved_user")
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

    override suspend fun getBiometricCredentials(): BiometricCredentials? {
        val preferences = context.settingsDataStore.data.firstOrNull()
        val username = preferences?.get(PreferencesKeys.BIOMETRIC_USERNAME)
        val encryptedPassword = preferences?.get(PreferencesKeys.BIOMETRIC_ENCRYPTED_PASSWORD)
        val iv = preferences?.get(PreferencesKeys.BIOMETRIC_IV)

        return if (username != null && encryptedPassword != null && iv != null) {
            BiometricCredentials(
                username = username,
                encryptedPassword = encryptedPassword,
                initializationVector = iv
            )
        } else {
            null
        }
    }

    override suspend fun setBiometricCredentials(credentials: BiometricCredentials) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_USERNAME] = credentials.username
            preferences[PreferencesKeys.BIOMETRIC_ENCRYPTED_PASSWORD] = credentials.encryptedPassword
            preferences[PreferencesKeys.BIOMETRIC_IV] = credentials.initializationVector
            preferences[PreferencesKeys.BIOMETRIC_ENABLED] = true
        }
    }

    override suspend fun isBiometricEnabled(): Boolean {
        return context.settingsDataStore.data
            .firstOrNull()?.get(PreferencesKeys.BIOMETRIC_ENABLED) ?: false
    }

    override suspend fun clearBiometricCredentials() {
        context.settingsDataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.BIOMETRIC_USERNAME)
            preferences.remove(PreferencesKeys.BIOMETRIC_ENCRYPTED_PASSWORD)
            preferences.remove(PreferencesKeys.BIOMETRIC_IV)
            preferences.remove(PreferencesKeys.BIOMETRIC_ENABLED)
        }
    }

    override suspend fun getSavedUser(): String? {
        return context.settingsDataStore.data
            .firstOrNull()?.get(PreferencesKeys.SAVED_USER)
    }

    override suspend fun setSavedUser(username: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.SAVED_USER] = username
        }
    }

    override suspend fun clearUserData() {
        context.settingsDataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.SAVED_USER)
            preferences.remove(PreferencesKeys.BIOMETRIC_USERNAME)
            preferences.remove(PreferencesKeys.BIOMETRIC_ENCRYPTED_PASSWORD)
            preferences.remove(PreferencesKeys.BIOMETRIC_IV)
            preferences.remove(PreferencesKeys.BIOMETRIC_ENABLED)
        }

    }
}
