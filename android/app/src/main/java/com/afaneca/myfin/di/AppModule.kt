package com.afaneca.myfin.di

import android.content.Context
import com.afaneca.myfin.data.UserDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by me on 10/06/2021
 */
val appModule = module {
    single { provideUserDataStore(androidContext()) }
}

fun provideUserDataStore(context: Context): UserDataStore = UserDataStore(context)