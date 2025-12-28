package com.afaneca.myfin.di

import com.afaneca.myfin.data.repository.AccountRepository
import com.afaneca.myfin.data.repository.AuthRepository
import com.afaneca.myfin.data.repository.BiometricRepository
import com.afaneca.myfin.data.repository.SettingsRepository
import com.afaneca.myfin.data.repository.TransactionRepository
import com.afaneca.myfin.domain.repository.AccountGateway
import com.afaneca.myfin.domain.repository.AuthGateway
import com.afaneca.myfin.domain.repository.BiometricGateway
import com.afaneca.myfin.domain.repository.SettingsGateway
import com.afaneca.myfin.domain.repository.TransactionGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepository): AuthGateway

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepository): SettingsGateway

    @Binds
    @Singleton
    abstract fun bindAccountRepository(accountRepositoryImpl: AccountRepository): AccountGateway

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(transactionRepositoryImpl: TransactionRepository): TransactionGateway

    @Binds
    @Singleton
    abstract fun bindBiometricRepository(biometricRepositoryImpl: BiometricRepository): BiometricGateway
}
