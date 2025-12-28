package com.afaneca.myfin.data.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.afaneca.myfin.R
import com.afaneca.myfin.domain.model.BiometricAuthResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import javax.crypto.Cipher

@Singleton
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager,
) {

    private val biometricManager = BiometricManager.from(context)

    companion object {
        private const val BIOMETRIC_TIMEOUT_MS = 60_000L // 60 seconds
        const val ERROR_KEY_INVALIDATED = -100 // Custom error code for key invalidation
    }

    fun isBiometricAvailable(): Boolean {
        val result = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        Timber.d("Biometric availability check: $result")
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isBiometricEnrolled(): Boolean {
        return isBiometricAvailable()
    }

    suspend fun authenticateForEncryption(
        activity: FragmentActivity,
        password: String
    ): BiometricAuthResult {
        Timber.d("Starting biometric authentication for encryption")

        if (!isBiometricAvailable()) {
            Timber.w("Biometric not available")
            return BiometricAuthResult.NotAvailable
        }

        if (!isBiometricEnrolled()) {
            Timber.w("Biometric not enrolled")
            return BiometricAuthResult.NotEnrolled
        }

        // Check if activity is finishing before showing prompt
        if (activity.isFinishing || activity.isDestroyed) {
            Timber.w("Activity is finishing or destroyed, cannot show biometric prompt")
            return BiometricAuthResult.Failure(reason = "Activity not available")
        }

        val cipher = try {
            cryptoManager.getEncryptCipher()
        } catch (e: BiometricKeyInvalidatedException) {
            Timber.e(e, "Biometric key invalidated - enrollment changed")
            // Return special result so caller can clear stored credentials
            return BiometricAuthResult.Failure(
                reason = "Biometric enrollment changed. Please setup biometric authentication again.",
                errorCode = ERROR_KEY_INVALIDATED
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to get encrypt cipher")
            return BiometricAuthResult.Failure(reason = "Cipher init failed: ${e.message}")
        }

        val resultChannel = Channel<BiometricAuthResult>(Channel.CONFLATED)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_setup_title))
            .setSubtitle(context.getString(R.string.biometric_setup_subtitle))
            .setNegativeButtonText(context.getString(R.string.cancel))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        val cryptoObject = BiometricPrompt.CryptoObject(cipher)

        val biometricPrompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Timber.d("Encryption authentication succeeded")
                    result.cryptoObject?.cipher?.let { authenticatedCipher ->
                        try {
                            val (encryptedPassword, iv) = cryptoManager.encryptData(password, authenticatedCipher)
                            Timber.d("Password encrypted successfully")
                            resultChannel.trySend(BiometricAuthResult.EncryptionSuccess(encryptedPassword, iv))
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to encrypt password")
                            resultChannel.trySend(BiometricAuthResult.Failure(
                                reason = "Encryption failed: ${e.message}"
                            ))
                        }
                    } ?: run {
                        Timber.e("Crypto object or cipher is null")
                        resultChannel.trySend(BiometricAuthResult.Failure(
                            reason = "Crypto object null"
                        ))
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Timber.w("Encryption authentication error: code=$errorCode, message=$errString")
                    resultChannel.trySend(
                        if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                            errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                            BiometricAuthResult.Cancelled
                        } else {
                            BiometricAuthResult.Failure(
                                reason = errString.toString(),
                                errorCode = errorCode
                            )
                        }
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Timber.w("Encryption authentication failed (biometric not recognized)")
                    // Don't close the channel here, user can retry
                }
            }
        )

        biometricPrompt.authenticate(promptInfo, cryptoObject)

        return try {
            withTimeout(BIOMETRIC_TIMEOUT_MS) {
                resultChannel.receive()
            }
        } catch (e: Exception) {
            Timber.e(e, "Biometric authentication timeout or cancelled")
            BiometricAuthResult.Failure(reason = "Timeout or cancelled: ${e.message}")
        } finally {
            resultChannel.close()
        }
    }

    suspend fun authenticateForDecryption(
        activity: FragmentActivity,
        encryptedPassword: String,
        iv: String
    ): BiometricAuthResult {
        Timber.d("Starting biometric authentication for decryption")

        if (!isBiometricAvailable()) {
            Timber.w("Biometric not available")
            return BiometricAuthResult.NotAvailable
        }

        if (!isBiometricEnrolled()) {
            Timber.w("Biometric not enrolled")
            return BiometricAuthResult.NotEnrolled
        }

        // Check if activity is finishing before showing prompt
        if (activity.isFinishing || activity.isDestroyed) {
            Timber.w("Activity is finishing or destroyed, cannot show biometric prompt")
            return BiometricAuthResult.Failure(reason = "Activity not available")
        }

        val cipher = try {
            val ivBytes = android.util.Base64.decode(iv, android.util.Base64.NO_WRAP)
            cryptoManager.getDecryptCipher(ivBytes)
        } catch (e: BiometricKeyInvalidatedException) {
            Timber.e(e, "Biometric key invalidated - enrollment changed")
            // Return special result so caller can clear stored credentials
            return BiometricAuthResult.Failure(
                reason = "Biometric enrollment changed. Please login with password to re-enable biometric authentication.",
                errorCode = ERROR_KEY_INVALIDATED
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to get decrypt cipher")
            return BiometricAuthResult.Failure(reason = "Cipher init failed: ${e.message}")
        }

        val resultChannel = Channel<BiometricAuthResult>(Channel.CONFLATED)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_login_title))
            .setSubtitle(context.getString(R.string.biometric_login_subtitle))
            .setNegativeButtonText(context.getString(R.string.cancel))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        val cryptoObject = BiometricPrompt.CryptoObject(cipher)

        val biometricPrompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Timber.d("Decryption authentication succeeded")
                    result.cryptoObject?.cipher?.let { authenticatedCipher ->
                        try {
                            val decryptedPassword = authenticatedCipher.doFinal(
                                android.util.Base64.decode(encryptedPassword, android.util.Base64.NO_WRAP)
                            ).toString(Charsets.UTF_8)
                            Timber.d("Password decrypted successfully")
                            resultChannel.trySend(BiometricAuthResult.Success(decryptedPassword))
                        } catch (e: Exception) {
                            Timber.e(e, "Failed to decrypt password")
                            resultChannel.trySend(BiometricAuthResult.Failure(
                                reason = "Decryption failed: ${e.message}"
                            ))
                        }
                    } ?: run {
                        Timber.e("Crypto object or cipher is null")
                        resultChannel.trySend(BiometricAuthResult.Failure(
                            reason = "Crypto object null"
                        ))
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Timber.w("Decryption authentication error: code=$errorCode, message=$errString")
                    resultChannel.trySend(
                        if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                            errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                            BiometricAuthResult.Cancelled
                        } else {
                            BiometricAuthResult.Failure(
                                reason = errString.toString(),
                                errorCode = errorCode
                            )
                        }
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Timber.w("Decryption authentication failed (biometric not recognized)")
                    // Don't close the channel here, user can retry
                }
            }
        )

        biometricPrompt.authenticate(promptInfo, cryptoObject)

        return try {
            withTimeout(BIOMETRIC_TIMEOUT_MS) {
                resultChannel.receive()
            }
        } catch (e: Exception) {
            Timber.e(e, "Biometric authentication timeout or cancelled")
            BiometricAuthResult.Failure(reason = "Timeout or cancelled: ${e.message}")
        } finally {
            resultChannel.close()
        }
    }
}
