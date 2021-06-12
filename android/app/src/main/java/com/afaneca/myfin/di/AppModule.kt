package com.afaneca.myfin.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.afaneca.myfin.data.UserDataManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by me on 10/06/2021
 */
val appModule = module {
    single { provideUserDataManager() }
    single { provideSecretUserData(androidContext()) }
}

fun provideUserDataManager() = UserDataManager()


fun provideSecretUserData(context: Context): EncryptedSharedPreferences {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    return EncryptedSharedPreferences.create(
        "user_data_encrypted",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    ) as EncryptedSharedPreferences
}