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
        val iv = IvParameterSpec(
            (keyAlias + text)
                .substring(0, 16).toByteArray(Charsets.UTF_8)
        )
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

/*
object ChCrypto {
    @JvmStatic
    fun aesEncrypt(v: String, secretKey: String) = AES256.encrypt(v, secretKey)

    @JvmStatic
    fun aesDecrypt(v: String, secretKey: String) = AES256.decrypt(v, secretKey)
}

private object AES256 {
    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()

    private fun cipher(opmode: Int, secretKey: SecretKey, iv: IvParameterSpec): Cipher {
        */
/*if (secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars")*//*

        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        */
/*val sk = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")*//*

        */
/*val iv = IvParameterSpec(iv.substring(0, 16).toByteArray(Charsets.UTF_8))*//*

        c.init(opmode, secretKey, iv)
        return c
    }

    fun encrypt(str: String, secretKey: SecretKey): String {
        val encrypted =
            cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.toByteArray(Charsets.UTF_8))
        return String(encoder.encode(encrypted))
    }

    fun decrypt(str: String, secretKey: SecretKey): String {
        val byteStr = decoder.decode(str.toByteArray(Charsets.UTF_8))
        val iv = IvParameterSpec(getIv().substring(0,16).toByteArray(Charsets.UTF_8))
        return String(cipher(Cipher.DECRYPT_MODE, secretKey, getIv()).doFinal(byteStr))
    }

    private fun getIv(): String {

    }
}
*/
