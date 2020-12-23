package com.afaneca.myfin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserDataStore(
    context: Context
) {
    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences>

    init {
        dataStore = applicationContext.createDataStore(name = DATA_STORE_NAME)
    }

    val sessionKey: Flow<String?>
        get() = dataStore.data.map { preferences ->
            {
                preferences[KEY_SESSIONKEY]
            }
        } as Flow<String?>


    suspend fun saveSessionKey(sessionKey: String) {
        dataStore.edit { preferences -> preferences[KEY_SESSIONKEY] = sessionKey }
    }

    companion object {
        private val DATA_STORE_NAME = "myfin_userdata_datastore"
        private val KEY_SESSIONKEY = preferencesKey<String>("key_sessionkey")
    }
}