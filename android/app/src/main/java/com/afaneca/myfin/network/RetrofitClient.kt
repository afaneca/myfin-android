package com.afaneca.myfin.network


import com.afaneca.myfin.BuildConfig
import com.afaneca.myfin.Consts
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by me on 09/12/2020
 */

class RetrofitClient {
    companion object {
        private val BASE_URL = Consts.BASE_URL
    }

    fun <Api> buildApi(
        api: Class<Api>,
        authToken: String? = null
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        chain.proceed(chain.request().newBuilder().also {
                            /*it.addHeader("Authorization", "Bearer $authToken")*/
                            it.addHeader("X-App-Version", getAppVersion())
                            it.addHeader("X-Platform", "Android")
                            it.addHeader("authusername", getAuthUsername())
                            it.addHeader("mobile", "true")
                        }.build())
                    }.also { client ->
                        if (BuildConfig.DEBUG) {
                            val logging = HttpLoggingInterceptor()
                            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                            client.addInterceptor(logging)
                        }
                    }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

    private fun getAuthUsername(): String = "tony"

    private fun getAppVersion(): String = BuildConfig.VERSION_NAME
}

