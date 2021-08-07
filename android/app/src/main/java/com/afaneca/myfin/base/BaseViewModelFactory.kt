package com.afaneca.myfin.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.afaneca.myfin.closed.budgets.data.BudgetsRepository
import com.afaneca.myfin.closed.budgets.ui.BudgetsViewModel
import com.afaneca.myfin.closed.dashboard.data.DashboardRepository
import com.afaneca.myfin.closed.dashboard.ui.DashboardViewModel
import com.afaneca.myfin.closed.transactions.data.TransactionsRepository
import com.afaneca.myfin.closed.transactions.ui.TransactionsViewModel
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.open.login.data.LoginRepository
import com.afaneca.myfin.open.login.ui.LoginViewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Created by me on 22/12/2020
 */
@KoinApiExtension
class BaseViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory(), KoinComponent {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(
                repository as LoginRepository,
                get()
            ) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(
                repository as DashboardRepository
            ) as T
            modelClass.isAssignableFrom(TransactionsViewModel::class.java) -> TransactionsViewModel(
                repository as TransactionsRepository
            ) as T
            modelClass.isAssignableFrom(BudgetsViewModel::class.java) -> BudgetsViewModel(
                repository as BudgetsRepository
            ) as T
            // Add More ViewModels here
            else -> throw IllegalArgumentException("ViewModel class not found")
        }
    }
}