package com.afaneca.myfin.Public.Splash

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by me on 09/12/2020
 */
interface ApiInterface {
    @GET("photos")
    fun getPhotos(): Call<List<DataModel>>
}