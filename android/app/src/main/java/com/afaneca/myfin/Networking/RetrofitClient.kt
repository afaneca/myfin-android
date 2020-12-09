package com.afaneca.myfin.Networking

import android.content.Context
import com.afaneca.myfin.BuildConfig
import com.afaneca.myfin.Consts
import com.afaneca.myfin.Public.Splash.ApiInterface
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by me on 09/12/2020
 */
object RetrofitClient {
    var BASE_URL: String = Consts.BASE_URL

    val getClient: ApiInterface
        get() {

            val gson = GsonBuilder()
                .setPrettyPrinting()
                .create()
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder().apply {
                addInterceptor(
                    Interceptor { chain ->
                        val builder = chain.request().newBuilder()
                        builder.header("X-App-Version", getAppVersion())
                        builder.header("X-Platform", "Android")
                        builder.header("authusername", getAuthUsername())
                        builder.header("mobile", "true")
                        return@Interceptor chain.proceed(builder.build())
                    }
                )
            }
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(ApiInterface::class.java)

        }

    private fun getAuthUsername(): String = "tony"

    private fun getAppVersion(): String = BuildConfig.VERSION_NAME
}