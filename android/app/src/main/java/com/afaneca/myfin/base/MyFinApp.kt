package com.afaneca.myfin.base

import android.app.Application
import com.afaneca.myfin.di.appModule
import com.afaneca.myfin.di.retrofitModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by me on 10/06/2021
 */
class MyFinApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyFinApp)
            modules(listOf(appModule, retrofitModule))
        }
    }
}