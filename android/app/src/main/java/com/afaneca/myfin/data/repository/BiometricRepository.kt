package com.afaneca.myfin.data.repository

import androidx.fragment.app.FragmentActivity
import timber.log.Timber
import com.afaneca.myfin.data.biometric.BiometricAuthManager
import com.afaneca.myfin.data.biometric.BiometricAuthManager.Companion.ERROR_KEY_INVALIDATED
import com.afaneca.myfin.data.biometric.CryptoManager
import com.afaneca.myfin.data.source.PersistentDataSource
import com.afaneca.myfin.domain.model.BiometricAuthResult
import com.afaneca.myfin.domain.model.BiometricCredentials
import com.afaneca.myfin.domain.repository.BiometricGateway
import javax.inject.Inject

class BiometricRepository @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager,
    private val cryptoManager: CryptoManager,
    private val persistentDataSource: PersistentDataSource,
) : BiometricGateway {

    override fun isBiometricAvailable(): Boolean {
        return biometricAuthManager.isBiometricAvailable()
    }

    override fun isBiometricEnrolled(): Boolean {
        return biometricAuthManager.isBiometricEnrolled()
    }

    override suspend fun isBiometricEnabled(): Boolean {
        return try {
            persistentDataSource.isBiometricEnabled()
        } catch (e: Exception) {
            Timber.e(e, "Error checking if biometric is enabled")
            false
        }
    }

    override suspend fun getBiometricCredentials(): BiometricCredentials? {
        return try {
            persistentDataSource.getBiometricCredentials()
        } catch (e: Exception) {
            Timber.e(e, "Error getting biometric credentials")
            null
        }
    }

    override suspend fun saveBiometricCredentials(
        username: String,
        password: String,
        activity: FragmentActivity
    ): BiometricAuthResult {
        Timber.d("Saving biometric credentials for user: $username")

        val result = biometricAuthManager.authenticateForEncryption(activity, password)

        if (result is BiometricAuthResult.EncryptionSuccess) {
            try {
                val credentials = BiometricCredentials(
                    username = username,
                    encryptedPassword = result.encryptedPassword,
                    initializationVector = result.iv
                )

                persistentDataSource.setBiometricCredentials(credentials)
                Timber.d("Biometric credentials saved successfully")
                return BiometricAuthResult.Success("") // Convert to generic success
            } catch (e: Exception) {
                Timber.e(e, "Failed to save biometric credentials")
                return BiometricAuthResult.Failure(reason = "Save failed: ${e.message}")
            }
        }

        return result
    }

    override suspend fun authenticateWithBiometric(activity: FragmentActivity): BiometricAuthResult {
        Timber.d("Authenticating with biometric")

        val credentials = getBiometricCredentials()
        if (credentials == null) {
            Timber.w("No biometric credentials found")
            return BiometricAuthResult.NotAvailable
        }

        val result = biometricAuthManager.authenticateForDecryption(
            activity = activity,
            encryptedPassword = credentials.encryptedPassword,
            iv = credentials.initializationVector
        )

        // If key was invalidated, automatically clear stored credentials
        if (result is BiometricAuthResult.Failure && result.errorCode == ERROR_KEY_INVALIDATED) {
            Timber.w("Key invalidated, clearing biometric credentials")
            disableBiometric()
        }

        return result
    }

    override suspend fun disableBiometric() {
        Timber.d("Disabling biometric authentication")
        try {
            persistentDataSource.clearBiometricCredentials()
            cryptoManager.deleteKey()
            Timber.d("Biometric authentication disabled successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error disabling biometric authentication")
            // Continue anyway - best effort cleanup
        }
    }
}
