package com.afaneca.myfin.data

import android.annotation.SuppressLint
import androidx.security.crypto.EncryptedSharedPreferences
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


/**
 * Created by me on 12/06/2021
 */
@KoinApiExtension
class UserDataManager : KoinComponent {
    private val userSharedPrefs: EncryptedSharedPreferences by inject()

    /* SESSION KEY */
    fun saveSessionKey(sessionKey: String) {
        with(userSharedPrefs.edit()) {
            putString(KEY_SESSION_KEY, sessionKey)
            apply()
        }
    }

    fun getSessionKey(): String = userSharedPrefs?.getString(KEY_SESSION_KEY, "") ?: ""


    /* LAST USER USERNAME */
    fun saveLastUser(username: String) {
        with(userSharedPrefs.edit()) {
            putString(KEY_LAST_USER, username)
            apply()
        }
    }

    fun getLastUsername(): String = userSharedPrefs?.getString(KEY_LAST_USER, "") ?: ""

    @SuppressLint("ApplySharedPref")
    fun clearUserData() {
        userSharedPrefs.edit().clear().commit()
    }

    fun clearUserDataAsync() {
        userSharedPrefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_SESSION_KEY = "KEY_SESSION_KEY"
        private const val KEY_LAST_USER = "KEY_LAST_USER"
    }
}