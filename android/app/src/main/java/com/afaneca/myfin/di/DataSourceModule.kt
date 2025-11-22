package com.afaneca.myfin.di

import com.afaneca.myfin.data.source.PersistentDataSource
import com.afaneca.myfin.data.source.DataStorePersistentDataSource
import com.afaneca.myfin.data.source.InMemoryDataSource
import com.afaneca.myfin.data.source.NetworkDataSource
import com.afaneca.myfin.data.source.RetrofitNetworkDataSource
import com.afaneca.myfin.data.source.TransientDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindNetworkDataSource(retrofitNetworkDataSource: RetrofitNetworkDataSource): NetworkDataSource

    @Binds
    @Singleton
    abstract fun bindPersistentDataSource(
        dataStorePersistentDataSource: DataStorePersistentDataSource
    ): PersistentDataSource

    @Binds
    @Singleton
    abstract fun bindTransientDataSource(
        inMemoryDataSource: InMemoryDataSource
    ): TransientDataSource
}
