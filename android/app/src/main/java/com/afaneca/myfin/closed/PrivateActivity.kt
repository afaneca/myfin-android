package com.afaneca.myfin.closed

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseActivity
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.databinding.ActivityPrivateBinding
import com.afaneca.myfin.open.login.ui.LoginActivity
import com.afaneca.myfin.utils.startNewActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

class PrivateActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityPrivateBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var privateViewModel: PrivateViewModel

    @KoinApiExtension
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        privateViewModel = ViewModelProvider(this).get(PrivateViewModel::class.java)
        setupDrawerMenu(setupToolbar())

        /* Observers*/

        privateViewModel.apply {
            getUserAccounts().observe(this@PrivateActivity, {
                if (it.isNullOrEmpty()) return@observe
                privateViewModel.calculateAggregatedAccountBalances(it)
            })

            patrimonyBalance.observe(this@PrivateActivity, {
                if (it.isNullOrEmpty()) return@observe
                populateMainPatrimonyBalance(it)
                populatePatrimonyBalance(it)
            })

            operatingFundsBalance.observe(this@PrivateActivity, {
                if (it.isNullOrEmpty()) return@observe
                populateOperatingFundsBalance(it)
            })

            investingBalance.observe(this@PrivateActivity, {
                if (it.isNullOrEmpty()) return@observe
                populateInvestmentsBalance(it)
            })

            debtBalance.observe(this@PrivateActivity, {
                if (it.isNullOrEmpty()) return@observe
                populateDebtBalance(it)
            })
        }

        // TODO - remove this!
        val fab = binding.floatingActionButton
        fab.setOnClickListener {
            when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    private fun populateMainPatrimonyBalance(value: String) {
        binding.navView.getHeaderView(0)?.findViewById<TextView>(R.id.patrimony_balance_amount)?.text = value
    }

    private fun populatePatrimonyBalance(value: String) {
        binding.navView.getHeaderView(0)?.findViewById<TextView>(R.id.secondary_asset_patrimony_amount)?.text = value
    }

    private fun populateOperatingFundsBalance(value: String) {
        binding.navView.getHeaderView(0)?.findViewById<TextView>(R.id.secondary_asset_operating_funds_amount)?.text =
            value
    }

    private fun populateInvestmentsBalance(value: String) {
        binding.navView.getHeaderView(0)?.findViewById<TextView>(R.id.secondary_asset_investments_amount)?.text = value
    }

    private fun populateDebtBalance(value: String) {
        binding.navView.getHeaderView(0)?.findViewById<TextView>(R.id.secondary_asset_debt_amount)?.text = value
    }


    private fun goToAccountsView() {
        // TODO - navigate to accounts view
    }

    private fun setupToolbar(): Toolbar {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        return toolbar
    }

    private fun setupDrawerMenu(toolbar: Toolbar) {
        drawer = binding.drawerLayout
        binding.navView.setNavigationItemSelectedListener(this)
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

        binding.navView.getHeaderView(0).setOnClickListener { goToAccountsView() }
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
            privateViewModel.clearUserSessionData() // clear session data
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