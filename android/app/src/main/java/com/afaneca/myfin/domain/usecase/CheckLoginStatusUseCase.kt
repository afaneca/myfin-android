package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.repository.AuthGateway
import com.afaneca.myfin.domain.repository.BiometricGateway
import javax.inject.Inject

class CheckLoginStatusUseCase @Inject constructor(
    private val authGateway: AuthGateway,
    private val biometricGateway: BiometricGateway,
) : BaseUseCase<Unit, CheckLoginStatusUseCase.LoginStatus, CheckLoginStatusUseCase.FeatureError> {

    override suspend fun invoke(params: Unit): Result<LoginStatus, FeatureError> {
        val savedUsername = authGateway.getSavedUsername()
        val isAvailable = biometricGateway.isBiometricAvailable()
        val isEnrolled = biometricGateway.isBiometricEnrolled()
        val isEnabled = biometricGateway.isBiometricEnabled()

        return Result.Success(
            LoginStatus(
                savedUsername = savedUsername,
                biometricStatus = BiometricStatus(
                    isAvailable = isAvailable,
                    isEnrolled = isEnrolled,
                    isEnabled = isEnabled,
                    canUseBiometric = isAvailable && isEnrolled
                )
            )
        )
    }

    data class LoginStatus(
        val biometricStatus: BiometricStatus,
        val savedUsername: String? = null,
    )

    data class BiometricStatus(
        val isAvailable: Boolean,
        val isEnrolled: Boolean,
        val isEnabled: Boolean,
        val canUseBiometric: Boolean
    )

    sealed interface FeatureError {
        data object GenericError : FeatureError
    }
}
