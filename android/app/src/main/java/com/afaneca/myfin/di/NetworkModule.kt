package com.afaneca.myfin.di

import dagger.Lazy
import android.content.Context
import com.afaneca.myfin.R
import com.afaneca.myfin.data.network.MyFinApiServices
import com.afaneca.myfin.BuildConfig
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.repository.AuthGateway
import com.afaneca.myfin.domain.repository.SettingsGateway
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        settingsGateway: SettingsGateway,
        authHeaderInterceptor: AuthHeaderInterceptor,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Dynamically set the base url for api calls based on saved user preferences
                var request = chain.request()

                // Get the base URL from settings repository
                val baseUrlResult = runBlocking { settingsGateway.getBaseApiUrl() }
                val parsedUrl = when (baseUrlResult) {
                    is Result.Success -> baseUrlResult.data
                    is Result.Error -> {
                        // Fallback to default URL if there's an error
                        val defaultUrl = context.getString(R.string.default_api_base_url)
                        val urlString =
                            if (defaultUrl.startsWith("http")) defaultUrl else "https://$defaultUrl"
                        urlString.toHttpUrlOrNull()
                    }
                }

                parsedUrl?.let {
                    val newUrl = request.url.newBuilder()
                        .scheme(it.scheme)
                        .host(it.host)
                        .port(it.port)
                        .build()
                    request = request.newBuilder().url(newUrl).build()
                }

                chain.proceed(request)
            }
            .addInterceptor(authHeaderInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.default_api_base_url)).client(
                okHttpClient,
            ).addConverterFactory(MoshiConverterFactory.create(moshi)).build()
    }

    private fun getAppVersion(): String = BuildConfig.VERSION_NAME

    @Provides
    @Singleton
    fun provideMyFinApiServices(retrofit: Retrofit): MyFinApiServices {
        return retrofit.create(MyFinApiServices::class.java)
    }
}

class AuthHeaderInterceptor @Inject constructor(
    private val authGateway: Lazy<AuthGateway>,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-App-Version", BuildConfig.VERSION_NAME)
            .addHeader("X-Platform", "Android")
            .addHeader("authusername", authGateway.get().getUsername() ?: "")
            .addHeader("sessionkey", authGateway.get().getSessionKey() ?: "")
            .addHeader("mobile", "true")
            .build()

        return chain.proceed(request)
    }
}
