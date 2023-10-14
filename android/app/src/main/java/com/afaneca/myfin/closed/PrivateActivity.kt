package com.afaneca.myfin.closed

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseActivity
import com.afaneca.myfin.closed.preferences.PreferencesActivity
import com.afaneca.myfin.databinding.ActivityPrivateBinding

import com.afaneca.myfin.open.login.ui.LoginActivity
import com.afaneca.myfin.utils.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PrivateActivity : BaseActivity() {
    private lateinit var binding: ActivityPrivateBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var privateViewModel: PrivateViewModel
    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            // Tof level destinations - will show hamburger menu in toolbar
            setOf(
                R.id.dashboardFragment,
                R.id.transactionsFragment,
                R.id.budgetsFragment,
                R.id.accountsFragment
            ),
            binding.drawerLayout
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_private
            ) as ActivityPrivateBinding

        //ActivityPrivateBinding.inflate(layoutInflater)
        /*setContentView(binding.root)*/
        privateViewModel = ViewModelProvider(this).get(PrivateViewModel::class.java)

        setupToolbar()
        setupDrawerMenu()


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
    }

    fun getNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        return navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = getNavController()
        return NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_preferences) {
            Intent(this, PreferencesActivity::class.java).also {
                startActivity(it)
            }

            return super.onOptionsItemSelected(item)
        }

        val navController = getNavController()
        return item.onNavDestinationSelected(navController)
                || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun populateMainPatrimonyBalance(value: String) {
        binding.navView.getHeaderView(0)
            ?.findViewById<TextView>(R.id.patrimony_balance_amount)?.text = value
    }

    private fun populatePatrimonyBalance(value: String) {
        binding.navView.getHeaderView(0)
            ?.findViewById<TextView>(R.id.secondary_asset_patrimony_amount)?.text = value
    }

    private fun populateOperatingFundsBalance(value: String) {
        binding.navView.getHeaderView(0)
            ?.findViewById<TextView>(R.id.secondary_asset_operating_funds_amount)?.text =
            value
    }

    protected fun populateInvestmentsBalance(value: String) {
        binding.navView.getHeaderView(0)
            ?.findViewById<TextView>(R.id.secondary_asset_investments_amount)?.text = value
    }

    private fun populateDebtBalance(value: String) {
        binding.navView.getHeaderView(0)
            ?.findViewById<TextView>(R.id.secondary_asset_debt_amount)?.text = value
    }

    fun refreshLastUpdateTimestampValue(formattedTimestamp: String) {
        binding.navView.getHeaderView(0)
            ?.findViewById<TextView>(R.id.last_update_timestamp_value)?.text = formattedTimestamp
    }

    private fun goToAccountsView() {
        // TODO - navigate to accounts view
    }

    private fun setupToolbar(): Toolbar {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        return toolbar
    }

    private fun setupDrawerMenu() {
        val navController = getNavController()
        NavigationUI.setupWithNavController(binding.navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        val logoutItem = binding.navView.menu.findItem(R.id.nav_item_logout)
        logoutItem.setOnMenuItemClickListener {
            showLogoutDialog()
            true
        }
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