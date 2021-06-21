package com.afaneca.myfin.base

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.security.crypto.EncryptedSharedPreferences
import com.afaneca.myfin.R
import org.koin.android.ext.android.inject
import org.koin.core.component.inject

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var preferencesChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private val userSharedPrefs: EncryptedSharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(findViewById(R.id.toolbar))

        preferencesChangeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences, key: String ->
                setAppTheme(key)
            }
        setAppTheme(
            userSharedPrefs.getString(
                getString(R.string.preferences_key_theme),
                getString(R.string.preferences_theme_option_key_system)
            ) ?: ""
        )
    }

    private fun setAppTheme(key: String) {
        if (key.equals(R.string.preferences_key_theme)) {
            when (userSharedPrefs.getString(
                key,
                getString(R.string.preferences_theme_option_key_system)
            )) {
                getString(R.string.preferences_theme_option_key_dark) -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                getString(R.string.preferences_theme_option_key_light) -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                getString(R.string.preferences_theme_option_key_system) -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        userSharedPrefs.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        userSharedPrefs.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
    }
}