package com.afaneca.myfin.di

import com.afaneca.myfin.BuildConfig
import com.afaneca.myfin.Consts
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.MyFinAPIServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by me on 10/06/2021
 */

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    fun provideOkHttp() = OkHttpClient.Builder()
        .build()

    @Provides
    @Named("retrofit_base_url")
    fun provideRetrofitBaseUrl() = Consts.BASE_URL

    @Singleton
    @Provides
    fun provideRetrofit(
        @Named("retrofit_base_url") baseUrl: String,
        userDataManager: UserDataManager,
    ): MyFinAPIServices =
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
            .create(MyFinAPIServices::class.java)

    /*@Provides
    fun <Api> provideBuildApi(retrofit: Retrofit, api: Class<Api>): Api {
        return retrofit.create(api)
    }*/
}


private fun okHttp() = OkHttpClient.Builder()
    .build()


private fun getAuthToken(userDataManager: UserDataManager): String {
    return userDataManager.getSessionKey() ?: ""
}

private fun getAuthUsername(userDataManager: UserDataManager): String =
    userDataManager.getLastUsername()

private fun getAppVersion(): String = BuildConfig.VERSION_NAME
