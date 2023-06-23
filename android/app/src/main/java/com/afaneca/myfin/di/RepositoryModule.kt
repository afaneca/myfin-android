package com.afaneca.myfin.di

import com.afaneca.myfin.closed.accounts.data.AccountsRepository
import com.afaneca.myfin.closed.accounts.data.LiveAccountsRepository
import com.afaneca.myfin.closed.budgets.data.BudgetsRepository
import com.afaneca.myfin.closed.budgets.data.LiveBudgetsRepository
import com.afaneca.myfin.closed.dashboard.data.DashboardRepository
import com.afaneca.myfin.closed.dashboard.data.LiveDashboardRepository
import com.afaneca.myfin.closed.transactions.data.LiveTransactionsRepository
import com.afaneca.myfin.closed.transactions.data.TransactionsRepository
import com.afaneca.myfin.data.LivePrivateRepository
import com.afaneca.myfin.data.PrivateRepository
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.MyFinDatabase
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.open.login.data.LiveLoginRepository
import com.afaneca.myfin.open.login.data.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by me on 05/09/2021
 */
@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideLoginRepository(
        service: MyFinAPIServices,
        userData: UserDataManager,
        db: MyFinDatabase
    ): LoginRepository = LiveLoginRepository(service, userData, db)

    @Singleton
    @Provides
    fun provideTransactionsRepository(
        service: MyFinAPIServices): TransactionsRepository = LiveTransactionsRepository(service)

    @Singleton
    @Provides
    fun provideAccountsRepository(
        service: MyFinAPIServices,
        userData: UserDataManager
    ): AccountsRepository = LiveAccountsRepository(service, userData)

    @Singleton
    @Provides
    fun provideDashboardRepository(
        service: MyFinAPIServices,
        userData: UserDataManager
    ): DashboardRepository = LiveDashboardRepository(service, userData)

    @Singleton
    @Provides
    fun provideBudgetsRepository(
        service: MyFinAPIServices,
        userData: UserDataManager
    ): BudgetsRepository = LiveBudgetsRepository(service, userData)

    @Singleton
    @Provides
    fun providePrivateRepository(
        db: MyFinDatabase,
        userData: UserDataManager
    ): PrivateRepository = LivePrivateRepository(db, userData)
}