package com.afaneca.myfin.di

import com.afaneca.myfin.closed.PrivateRepository
import com.afaneca.myfin.closed.accounts.data.AccountsRepository
import com.afaneca.myfin.closed.budgets.data.BudgetsRepository
import com.afaneca.myfin.closed.dashboard.data.DashboardRepository
import com.afaneca.myfin.closed.transactions.data.TransactionsRepository
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.MyFinDatabase
import com.afaneca.myfin.data.network.MyFinAPIServices
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
    ): LoginRepository = LoginRepository(service, userData, db)

    @Singleton
    @Provides
    fun provideTransactionsRepository(
        service: MyFinAPIServices,
        userData: UserDataManager
    ): TransactionsRepository = TransactionsRepository(service, userData)

    @Singleton
    @Provides
    fun provideAccountsRepository(
        service: MyFinAPIServices,
        userData: UserDataManager
    ): AccountsRepository = AccountsRepository(service, userData)

    @Singleton
    @Provides
    fun provideDashboardRepository(
        service: MyFinAPIServices,
        userData: UserDataManager
    ): DashboardRepository = DashboardRepository(service, userData)

    @Singleton
    @Provides
    fun provideBudgetsRepository(
        service: MyFinAPIServices,
        userData: UserDataManager
    ): BudgetsRepository = BudgetsRepository(service, userData)

    @Singleton
    @Provides
    fun providePrivateRepository(
        db: MyFinDatabase,
        userData: UserDataManager
    ): PrivateRepository = PrivateRepository(db, userData)
}