package com.afaneca.myfin.base

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.afaneca.myfin.R

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var preferencesChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private val userSharedPrefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(findViewById(R.id.toolbar))

        setPreferencesChangeListener()
    }

    protected fun setPreferencesChangeListener() {
        preferencesChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == getString(R.string.preferences_key_theme)) {
                setAppTheme(getUserThemeFromPreferences())
            }
        }
        setAppTheme(getUserThemeFromPreferences())
    }

    protected fun getUserThemeFromPreferences(): String =
        userSharedPrefs.getString(
            getString(R.string.preferences_key_theme),
            getString(R.string.preferences_theme_option_key_system)
        ) ?: ""


    protected fun setAppTheme(selected_theme_key: String) {
        when (selected_theme_key) {
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

    override fun onResume() {
        super.onResume()
        userSharedPrefs.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        userSharedPrefs.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
    }
}