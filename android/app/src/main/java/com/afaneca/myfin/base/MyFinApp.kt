package com.afaneca.myfin.base

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by me on 10/06/2021
 */
@HiltAndroidApp
class MyFinApp : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}