package com.afaneca.myfin.domain.model

sealed interface BiometricAuthResult {
    data class Success(val data: String) : BiometricAuthResult
    data class EncryptionSuccess(val encryptedPassword: String, val iv: String) : BiometricAuthResult
    data class Failure(val reason: String? = null, val errorCode: Int? = null) : BiometricAuthResult
    data object Cancelled : BiometricAuthResult
    data object NotAvailable : BiometricAuthResult
    data object NotEnrolled : BiometricAuthResult
}
