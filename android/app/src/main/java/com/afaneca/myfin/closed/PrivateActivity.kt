package com.afaneca.myfin.closed

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.afaneca.myfin.R
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.open.login.ui.LoginActivity
import com.afaneca.myfin.utils.startNewActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class PrivateActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private)

        setupDrawerMenu(setupToolbar())

        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

        }
    }

    private fun setupToolbar(): Toolbar {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        return toolbar
    }

    private fun setupDrawerMenu(toolbar: Toolbar) {
        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_logout -> showLogoutDialog()
        }

        return true
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this@PrivateActivity)
            .setMessage(getString(R.string.confirmation_logout_message))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.generic_yes)) { dialog, id -> doLogout() }
            .setNegativeButton(getString(R.string.generic_go_back)) { dialog, id -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun doLogout() {
        val userDataManager: UserDataManager by inject()
        lifecycleScope.launch(Dispatchers.IO) {
            userDataManager.clearUserSessionData() // clear session data
            withContext(Dispatchers.IO) {
                goToLoginActivity() // go back to login activity
            }
        }
    }

    private fun goToLoginActivity() {
        startNewActivity(LoginActivity::class.java)
        finish()
    }

}