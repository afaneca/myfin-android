package com.afaneca.myfin.open.login.ui

import android.content.Intent
import android.os.Bundle
import com.afaneca.myfin.R
import com.afaneca.myfin.base.BaseActivity
import com.afaneca.myfin.closed.PrivateActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*if (!userData.getSessionKey().isBlank()) {
            // TODO check validity of session key before logging in
            goToPrivateActivity()
        }*/
    }


    private fun goToPrivateActivity() {
        startActivity(Intent(this, PrivateActivity::class.java))
    }
}
