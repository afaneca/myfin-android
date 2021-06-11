package com.afaneca.myfin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.clear
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.afaneca.myfin.utils.SecurityUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class UserDataStore(
    context: Context
) {
    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences>
    private val bytesToStringSeparator = "|"

    init {
        dataStore = applicationContext.createDataStore(name = DATA_STORE_NAME)
    }

    val sessionKey: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_SESSIONKEY]
        }


    /**
     * Retrieves the session key from the UserDataStore and returns it after decryption
     */
    suspend fun getSessionKey(): String {

        return SecurityUtils().decryptData(
            KEY_SESSIONKEY.name,
            sessionKey.first()?.split(bytesToStringSeparator)?.map { it.toByte() }!!.toByteArray()
        )
    }

    /**
     * Encrypts the received sessionKey and puts it in the UserDataStore
     */
    suspend fun saveSessionKey(sessionKey: String) {
        val encryptedSessionKey =
            SecurityUtils().encryptData(KEY_SESSIONKEY.name, Json.encodeToString(sessionKey))
        dataStore.edit { preferences ->
            preferences[KEY_SESSIONKEY] = encryptedSessionKey.joinToString(bytesToStringSeparator)
        }
    }

    suspend fun clearData() {
        dataStore.edit { it.clear() }
    }

    companion object {
        private val DATA_STORE_NAME = "myfin_userdata_datastore"
        private val KEY_SESSIONKEY = preferencesKey<String>("key_sessionkey")
    }
}
