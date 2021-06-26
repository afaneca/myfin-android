package com.afaneca.myfin.closed.preferences

import android.os.Bundle
import android.os.PersistableBundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseActivity

class PreferencesActivity : BaseActivity() {
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
        setPreferencesChangeListener()
    }


    override fun onResume() {
        super.onResume()
        setAppTheme(getUserThemeFromPreferences())
    }

    override fun onStop() {
        super.onStop()
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
        finish()
        return true
    }

    class MainPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_screen, rootKey)
        }
    }

    companion object {
        private val TITLE_TAG = PreferencesActivity::getTitle.toString()
    }
}