package com.afaneca.myfin.di

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.MyFinDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


/**
 * Created by me on 10/06/2021
 */
val appModule = module {
    single { provideUserDataManager() }
    single { provideSecretUserData(androidContext()) }
    single { provideMyFinDatabase(androidContext()) }
}

fun provideUserDataManager() = UserDataManager()

fun provideMyFinDatabase(context: Context): MyFinDatabase {
    // TODO - https://sonique6784.medium.com/protect-your-room-database-with-sqlcipher-on-android-78e0681be687
    return Room.databaseBuilder(
        context,
        MyFinDatabase::class.java,
        "myfin_db"
    ).fallbackToDestructiveMigration()
        //.openHelperFactory(SupportFactory(SQLiteDatabase.getBytes("PassPhrase".toCharArray())))
        .build()
}

fun provideSecretUserData(context: Context): EncryptedSharedPreferences {
    val spec = KeyGenParameterSpec.Builder(
        MasterKey.DEFAULT_MASTER_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
        .build()

    val masterKey = MasterKey.Builder(context)
        .setKeyGenParameterSpec(spec)
        .build()

    return EncryptedSharedPreferences.create(
        context,
        "user_data_encrypted",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    ) as EncryptedSharedPreferences
}
