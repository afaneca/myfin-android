package com.afaneca.myfin.open.login.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import com.afaneca.myfin.R
import com.afaneca.myfin.closed.PrivateActivity
import com.afaneca.myfin.data.UserDataManager
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {
    protected val userData: UserDataManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (!userData.getSessionKey().isBlank()) {
            // TODO check validity of session key before logging in
            goToPrivateActivity()
        }
    }


    private fun goToPrivateActivity() {
        startActivity(Intent(this, PrivateActivity::class.java))
    }
}
