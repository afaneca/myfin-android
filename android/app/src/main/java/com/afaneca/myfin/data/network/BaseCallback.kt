package com.afaneca.myfin.data.network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by me on 21/12/2020
 */
open class BaseCallback : Callback<BaseResponse> {
    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
        Log.e("eheh", "ON RESPONSE 1");
    }

    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
        TODO("Not yet implemented")
    }
}