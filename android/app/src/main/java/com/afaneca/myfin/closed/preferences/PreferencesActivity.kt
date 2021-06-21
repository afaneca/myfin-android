package com.afaneca.myfin.closed.preferences

import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.security.crypto.EncryptedSharedPreferences
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseActivity
import org.koin.android.ext.android.inject

class PreferencesActivity : BaseActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private lateinit var preferencesChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private val userSharedPrefs: EncryptedSharedPreferences by inject()

    private lateinit var mThemeListPreference: ListPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.preferences_fl, MainPreferenceFragment())
                .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.generic_settings)
            }
        }

        setupToolbar()

        preferencesChangeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences, key: String ->
                if (key.equals(R.string.preferences_key_theme)) {
                    setAppTheme(
                        userSharedPrefs.getString(
                            key,
                            getString(R.string.preferences_theme_option_key_system)
                        )!!
                    )
                }
            }
        setAppTheme(
            userSharedPrefs.getString(
                getString(R.string.preferences_key_theme),
                getString(R.string.preferences_theme_option_key_system)
            ) ?: ""
        )
    }

    private fun setAppTheme(value: String) {
        when (value) {
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

    override fun onStop() {
        super.onStop()
        userSharedPrefs.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setTitle(R.string.generic_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        /*if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()*/
        finish()
        return true
    }

    class MainPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_screen, rootKey)


        }

    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference?
    ): Boolean {
        val args = pref?.extras
        val fragment = pref?.fragment?.let {
            supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                it
            ).apply {
                arguments = args
                setTargetFragment(caller, 0)
            }
        }

        fragment?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.preferences_fl, it)
                .addToBackStack(null)
                .commit()
        }
        title = pref?.title
        return true
    }

    companion object {
        private val TITLE_TAG = PreferencesActivity::getTitle.toString()
    }
}