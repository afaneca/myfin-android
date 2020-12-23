package com.afaneca.myfin.open.login.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.afaneca.myfin.R
import com.afaneca.myfin.closed.PrivateActivity
import com.afaneca.myfin.data.UserDataStore
import com.afaneca.myfin.utils.startNewActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val userData = UserDataStore(this)

        userData.sessionKey.asLiveData().observe(this, Observer {
            if (it !== null) {
                goToPrivateActivity()
            }
        })
    }

    private fun goToPrivateActivity() {
        startActivity(Intent(this, PrivateActivity::class.java))
    }
}
