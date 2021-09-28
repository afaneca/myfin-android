package com.afaneca.myfin.di

import android.content.Context
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.afaneca.myfin.BuildConfig
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.MyFinDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import javax.inject.Singleton


/**
 * Created by me on 10/06/2021
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserDataManager(userSharedPreferences: EncryptedSharedPreferences) =
        UserDataManager(userSharedPreferences)

    @Singleton
    @Provides
    fun provideMyFinDatabase(
        @ApplicationContext context: Context,
        userDataManager: UserDataManager
    ): MyFinDatabase {
        return Room.databaseBuilder(
            context,
            MyFinDatabase::class.java,
            "${BuildConfig.APPLICATION_ID}_db"
        ).fallbackToDestructiveMigration()
            .openHelperFactory(
                SupportFactory(
                    SQLiteDatabase.getBytes(
                        getDBEncryptionPassphrase(
                            context,
                            userDataManager
                        ).toCharArray()
                    )
                )
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideSecretUserData(@ApplicationContext context: Context): EncryptedSharedPreferences {
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

    /**
     * If there's no passphrase stored in the EncryptedSharedPrefs yet,
     * it generates a new one, stores it there and returns it
     */
    fun getDBEncryptionPassphrase(context: Context, userDataManager: UserDataManager): String {
        var passphrase = userDataManager.getDBPassphrase()
        if (passphrase.isNullOrBlank()) {
            passphrase = generatePassword(context, userDataManager)
            GlobalScope.launch(Dispatchers.IO) {
                userDataManager.saveDBPassphrase(passphrase)
            }
        }
        return passphrase
    }

    /**
     * Generate random and quasi secure password
     * Source: https://stackoverflow.com/a/62052122
     */
    fun generatePassword(context: Context, userDataManager: UserDataManager): String {
        val androidId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        val passPassphraseBase = "${BuildConfig.APPLICATION_ID}_$androidId:"

        val random = SecureRandom()
        val salt = ByteArray(32)
        random.nextBytes(salt)

        val passPhrase = passPassphraseBase.toByteArray() + salt

        val pass = Base64.encodeToString(passPhrase, Base64.NO_WRAP)

        return pass
    }

    private fun getAuthToken(userDataManager: UserDataManager): String {
        return userDataManager.getSessionKey() ?: ""
    }
}
