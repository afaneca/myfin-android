package com.afaneca.myfin.data.network

import com.afaneca.myfin.open.login.data.AttemptLoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by me on 09/12/2020
 */
interface MyFinAPIServices {

    @FormUrlEncoded
    @POST("auth/")
    suspend fun attemptLogin(
        @Field("username")
        username: String,
        @Field("password")
        password: String
    ): AttemptLoginResponse
}