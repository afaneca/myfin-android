package com.afaneca.myfin.domain.repository

import androidx.fragment.app.FragmentActivity
import com.afaneca.myfin.domain.model.BiometricAuthResult
import com.afaneca.myfin.domain.model.BiometricCredentials

interface BiometricGateway {
    fun isBiometricAvailable(): Boolean
    fun isBiometricEnrolled(): Boolean
    suspend fun isBiometricEnabled(): Boolean
    suspend fun getBiometricCredentials(): BiometricCredentials?
    suspend fun saveBiometricCredentials(username: String, password: String, activity: FragmentActivity): BiometricAuthResult
    suspend fun authenticateWithBiometric(activity: FragmentActivity): BiometricAuthResult
    suspend fun disableBiometric()
}
