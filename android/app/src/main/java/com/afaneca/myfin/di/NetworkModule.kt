package com.afaneca.myfin.di

import android.content.Context
import androidx.preference.PreferenceManager
import com.afaneca.myfin.BuildConfig
import com.afaneca.myfin.Consts
import com.afaneca.myfin.R
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.MyFinAPIServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
object NetworkModule {

    @Singleton
    @Provides
    fun provideService(
        @ApplicationContext context: Context,
        userDataManager: UserDataManager,
    ): MyFinAPIServices =
        Retrofit.Builder()
            .baseUrl("https://" + context.getString(R.string.default_api_base_url))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        // Dynamically set the base url for api calls based on saved user preferences
                        var request = chain.request()
                        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                        val host: String = preferences.getString(
                            context.getString(R.string.preferences_key_api_url),
                            context.getString(R.string.default_api_base_url)
                        )!!

                        val newUrl = request.url.newBuilder()
                            .host(host)
                            .build()

                        request = request.newBuilder()
                            .url(newUrl)
                            .build()

                        chain.proceed(request)
                    }
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

    private fun getAuthToken(userDataManager: UserDataManager): String {
        return userDataManager.getSessionKey() ?: ""
    }

    private fun getAuthUsername(userDataManager: UserDataManager): String =
        userDataManager.getLastUsername()

    private fun getAppVersion(): String = BuildConfig.VERSION_NAME
}


