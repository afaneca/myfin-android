package com.afaneca.myfin.data

import android.annotation.SuppressLint
import androidx.security.crypto.EncryptedSharedPreferences
import javax.inject.Inject


/**
 * Created by me on 12/06/2021
 */

class UserDataManager
@Inject
constructor(
    var userSharedPrefs: EncryptedSharedPreferences
) {

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

    fun getLastUsername(): String = userSharedPrefs.getString(KEY_LAST_USER, "") ?: ""

    /* KEEP SESSION */
    fun saveIsToKeepSession(keepSession: Boolean) {
        with(userSharedPrefs.edit()) {
            putBoolean(KEY_KEEP_SESSION, keepSession)
            apply()
        }
    }

    fun getIsToKeepSession(): Boolean = userSharedPrefs.getBoolean(KEY_KEEP_SESSION, false)

    /* ENCRYPTED PASSWORD */
    fun saveEncryptedPassword(password: String) {
        with(userSharedPrefs.edit()) {
            putString(KEY_ENC_PW, password)
            apply()
        }
    }

    fun getEncryptedPassword(): String = userSharedPrefs.getString(KEY_ENC_PW, "") ?: ""

    @SuppressLint("ApplySharedPref")
    fun clearUserData() {
        userSharedPrefs.edit().clear().commit()
    }

    fun clearUserDataAsync() {
        userSharedPrefs.edit().clear().apply()
    }

    @SuppressLint("ApplySharedPref")
    fun clearUserSessionData() {
        userSharedPrefs.edit().remove(KEY_SESSION_KEY).commit()
        userSharedPrefs.edit().remove(KEY_KEEP_SESSION).commit()
    }

    fun clearUserSessionDataAsync() {
        userSharedPrefs.edit().remove(KEY_SESSION_KEY).apply()
    }

    /* DB PASSPHRASE */
    fun saveDBPassphrase(pass: String) {
        with(userSharedPrefs.edit()) {
            putString(KEY_DB_PASS, pass)
            apply()
        }
    }

    fun getDBPassphrase(): String = userSharedPrefs?.getString(KEY_DB_PASS, "") ?: ""

    companion object {
        private const val KEY_SESSION_KEY = "KEY_SESSION_KEY"
        private const val KEY_LAST_USER = "KEY_LAST_USER"
        private const val KEY_DB_PASS = "KEY_DB_PASS"
        private const val KEY_KEEP_SESSION = "KEY_KEEP_SESSION"
        private const val KEY_ENC_PW = "KEY_ENC_PW"
    }
}