package com.afaneca.myfin.di

import com.afaneca.myfin.BuildConfig
import com.afaneca.myfin.Consts
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.MyFinAPIServices
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by me on 10/06/2021
 */
val retrofitModule = module {

    single {
        okHttp()
    }
    single {
        retrofit(Consts.BASE_URL, get())
    }
    single {
        buildApi(get(), MyFinAPIServices::class.java)
        /*get<Retrofit>()
            .create(MyFinAPIServices::class.java)*/
    }
}

private fun okHttp() = OkHttpClient.Builder()
    .build()

private fun <Api> buildApi(retrofit: Retrofit, api: Class<Api>): Api {
    return retrofit.create(api)
}

private fun retrofit(
    baseUrl: String,
    userDataManager: UserDataManager,
) =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    chain.proceed(chain.request().newBuilder().also {
                        /*it.addHeader("Authorization", "Bearer $authToken")*/
                        it.addHeader("X-App-Version", getAppVersion())
                        it.addHeader("X-Platform", "Android")
                        it.addHeader("authusername", getAuthUsername(userDataManager))
                        it.addHeader("sessionkey", getAuthToken(userDataManager))
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


private fun getAuthToken(userDataManager: UserDataManager): String {
    return userDataManager.getSessionKey() ?: ""
}

private fun getAuthUsername(userDataManager: UserDataManager): String =
    userDataManager.getLastUsername()

private fun getAppVersion(): String = BuildConfig.VERSION_NAME
