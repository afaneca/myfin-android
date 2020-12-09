package com.afaneca.myfin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afaneca.myfin.Networking.RetrofitClient
import com.afaneca.myfin.Public.Splash.DataModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getData()
    }

    private fun getData() {
        val call: Call<List<DataModel>> = RetrofitClient.getClient.getPhotos()
        call.enqueue(object : Callback<List<DataModel>> {

            override fun onResponse(
                call: Call<List<DataModel>>?,
                response: Response<List<DataModel>>?
            ) {
            }

            override fun onFailure(call: Call<List<DataModel>>?, t: Throwable?) {

            }

        })
    }
}