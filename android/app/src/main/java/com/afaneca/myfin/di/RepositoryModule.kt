package com.afaneca.myfin.di

import com.afaneca.myfin.domain.repository.AccountsRepository
import com.afaneca.myfin.data.LiveAccountsRepository
import com.afaneca.myfin.domain.repository.BudgetsRepository
import com.afaneca.myfin.data.LiveBudgetsRepository
import com.afaneca.myfin.domain.repository.DashboardRepository
import com.afaneca.myfin.data.LiveDashboardRepository
import com.afaneca.myfin.data.LiveTransactionsRepository
import com.afaneca.myfin.domain.repository.TransactionsRepository
import com.afaneca.myfin.data.LiveAccountRepository
import com.afaneca.myfin.domain.repository.AccountRepository
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.MyFinDatabase
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.LiveLoginRepository
import com.afaneca.myfin.domain.repository.LoginRepository
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
    ): AccountRepository = LiveAccountRepository(db, userData)
}