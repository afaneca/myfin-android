package com.afaneca.myfin.open.login.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
    }
}
