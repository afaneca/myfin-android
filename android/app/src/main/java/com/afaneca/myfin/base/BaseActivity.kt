package com.afaneca.myfin.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afaneca.myfin.R

class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        setSupportActionBar(findViewById(R.id.toolbar))
    }
}