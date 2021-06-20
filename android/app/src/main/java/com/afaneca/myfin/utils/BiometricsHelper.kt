package com.afaneca.myfin.utils

import androidx.fragment.app.Fragment
import com.afaneca.myfin.R
import com.mikhaellopez.biometric.BiometricHelper
import com.mikhaellopez.biometric.BiometricPromptInfo

/**
 * Created by me on 20/06/2021
 */
class BiometricsHelper(private val fragment: Fragment) {
    private val biometricHelper by lazy { BiometricHelper(fragment) }

    fun showBiometricPrompt(
        onError: ((Int, CharSequence) -> Unit)? = null,
        onFailed: (() -> Unit)? = null,
        onSuccess: () -> Unit
    ) {
        biometricHelper.showBiometricPrompt(
            BiometricPromptInfo(
                title = fragment.getString(R.string.biometric_auth_prompt_title),
                negativeButtonText = fragment.getString(R.string.generic_cancel),
                subtitle = fragment.getString(R.string.biometric_auth_prompt_subtitle),
                description = fragment.getString(R.string.biometric_auth_prompt_description),
                confirmationRequired = false
            ),
            onError = { errorCode: Int, errString: CharSequence ->
                onError?.let { it(errorCode, errString) }
            }, onFailed = {
                onFailed?.let { it() }
            }, onSuccess = {
                onSuccess()
            })
    }

    fun areBiometricsAvailable(): Boolean {
        return biometricHelper.biometricEnable()
    }
}