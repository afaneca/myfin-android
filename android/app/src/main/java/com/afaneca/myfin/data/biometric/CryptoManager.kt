package com.afaneca.myfin.data.biometric

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor() {

    private val keyStore by lazy {
        KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
            load(null)
        }
    }

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "MyFinBiometricKey"
        private const val TRANSFORMATION = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
        private const val GCM_TAG_LENGTH = 128
    }

    fun getOrCreateKey(): SecretKey {
        try {
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                Timber.d("Creating new biometric key")
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    KEYSTORE_PROVIDER
                )

                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).apply {
                    setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    setUserAuthenticationRequired(true)
                    setInvalidatedByBiometricEnrollment(true)

                    // Use different APIs based on Android version
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // API 30+ - Use new method with biometric strong requirement
                        setUserAuthenticationParameters(
                            0, // timeout in seconds, 0 means auth required for each use
                            KeyProperties.AUTH_BIOMETRIC_STRONG
                        )
                    } else {
                        // API 24-29 - Use deprecated method but still works
                        @Suppress("DEPRECATION")
                        setUserAuthenticationValidityDurationSeconds(-1) // -1 means auth required for each use
                    }
                }.build()

                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }

            return keyStore.getKey(KEY_ALIAS, null) as SecretKey
        } catch (e: Exception) {
            Timber.e(e, "Error getting or creating key")
            throw BiometricCryptoException("Failed to get or create encryption key", e)
        }
    }

    fun getEncryptCipher(): Cipher {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            return cipher
        } catch (e: KeyPermanentlyInvalidatedException) {
            Timber.w(e, "Key permanently invalidated, deleting and recreating")
            deleteKey()
            throw BiometricKeyInvalidatedException("Biometric key was invalidated (biometric enrollment changed)", e)
        } catch (e: Exception) {
            Timber.e(e, "Error getting encrypt cipher")
            throw BiometricCryptoException("Failed to initialize encryption cipher", e)
        }
    }

    fun getDecryptCipher(iv: ByteArray): Cipher {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
            return cipher
        } catch (e: KeyPermanentlyInvalidatedException) {
            Timber.w(e, "Key permanently invalidated, deleting and recreating")
            deleteKey()
            throw BiometricKeyInvalidatedException("Biometric key was invalidated (biometric enrollment changed)", e)
        } catch (e: Exception) {
            Timber.e(e, "Error getting decrypt cipher")
            throw BiometricCryptoException("Failed to initialize decryption cipher", e)
        }
    }

    fun encryptData(plaintext: String, cipher: Cipher): Pair<String, String> {
        try {
            val encryptedBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            val iv = cipher.iv ?: throw BiometricCryptoException("Cipher IV is null")

            return Pair(
                Base64.encodeToString(encryptedBytes, Base64.NO_WRAP),
                Base64.encodeToString(iv, Base64.NO_WRAP)
            )
        } catch (e: BiometricCryptoException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Error encrypting data")
            throw BiometricCryptoException("Failed to encrypt data", e)
        }
    }

    fun deleteKey() {
        try {
            if (keyStore.containsAlias(KEY_ALIAS)) {
                Timber.d("Deleting biometric key")
                keyStore.deleteEntry(KEY_ALIAS)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error deleting key")
            // Don't throw - this is a cleanup operation
        }
    }
}

/**
 * Exception thrown when biometric cryptographic operations fail
 */
class BiometricCryptoException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Exception thrown when the biometric key is permanently invalidated
 * This typically happens when the user adds/removes biometric credentials
 */
class BiometricKeyInvalidatedException(message: String, cause: Throwable? = null) : Exception(message, cause)
