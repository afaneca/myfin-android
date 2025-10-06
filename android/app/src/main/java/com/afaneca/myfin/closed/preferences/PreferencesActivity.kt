package com.afaneca.myfin.closed.preferences

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseActivity
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class PreferencesActivity : BaseActivity() {
    private lateinit var mThemeListPreference: ListPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        setupEdgeToEdge()
        setPreferencesChangeListener()
    }


    override fun onResume() {
        super.onResume()
        setAppTheme(getUserThemeFromPreferences())
    }

    override fun onStop() {
        super.onStop()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootWrapper)) { v, windowInsets ->
            val insets =
                windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(left = insets.left, top = insets.top, right = insets.right, bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
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
            val apiUrlPref = findPreference<EditTextPreference>(getString(R.string.preferences_key_api_url))
            apiUrlPref?.setOnPreferenceChangeListener { preference, newValue ->
                var url = newValue as? String ?: return@setOnPreferenceChangeListener false

                // Prepend scheme if missing
                if (!url.startsWith("http")) {
                    url = "https://$url"
                }

                val parsed = url.toHttpUrlOrNull()
                if (parsed == null) {
                    Toast.makeText(requireContext(), getString(R.string.error_invalid_url), Toast.LENGTH_SHORT).show()
                    return@setOnPreferenceChangeListener false
                }

                // Normalize and save the parsed URL back into preferences
                (preference as EditTextPreference).text = parsed.toString()

                // Return false because we already wrote the normalized value
                false
            }
        }

    }

    companion object {
        private val TITLE_TAG = PreferencesActivity::getTitle.toString()
    }
}