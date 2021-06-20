package com.afaneca.myfin.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by me on 11/06/2021
 *
 * source: https://proandroiddev.com/securing-androids-datastore-ad56958ca6ee
 */
class SecurityUtils
constructor() {
    private val provider = "AndroidKeyStore"
    private val cipher by lazy {
        Cipher.getInstance("AES/GCM/NoPadding")
    }
    private val charset by lazy {
        charset("UTF-8")

    }
    private val keyStore by lazy {
        KeyStore.getInstance(provider).apply {
            load(null)
        }
    }
    private val keyGenerator by lazy {
        KeyGenerator.getInstance(KEY_ALGORITHM_AES, provider)
    }

    fun encryptData(keyAlias: String, text: String): ByteArray {
        val secretKey = generateSecretKey(keyAlias)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(text.toByteArray(charset))
    }

    fun decryptData(keyAlias: String, encryptedData: ByteArray): String {
        cipher.init(
            Cipher.DECRYPT_MODE,
            getSecretKey(keyAlias),
            GCMParameterSpec(128, ByteArray(12))
        )
        return cipher.doFinal(encryptedData).toString(charset)
    }

    private fun generateSecretKey(keyAlias: String): SecretKey {
        return keyGenerator.apply {
            init(
                KeyGenParameterSpec
                    .Builder(keyAlias, PURPOSE_ENCRYPT or PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE_GCM)
                    .setEncryptionPaddings(ENCRYPTION_PADDING_NONE)
                    .build()
            )
        }.generateKey()
    }

    private fun getSecretKey(keyAlias: String) =
        (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
}

