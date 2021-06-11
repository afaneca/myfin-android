package com.afaneca.myfin.open.login.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.afaneca.myfin.R
import com.afaneca.myfin.closed.PrivateActivity
import com.afaneca.myfin.data.UserDataStore
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {
    protected val userData: UserDataStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //val userData: UserDataStore = get()//UserDataStore(this)

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
