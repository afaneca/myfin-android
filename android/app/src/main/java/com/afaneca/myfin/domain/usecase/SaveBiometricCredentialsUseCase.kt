package com.afaneca.myfin.domain.usecase

import androidx.fragment.app.FragmentActivity
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.model.BiometricAuthResult
import com.afaneca.myfin.domain.repository.BiometricGateway
import javax.inject.Inject

class SaveBiometricCredentialsUseCase @Inject constructor(
    private val biometricGateway: BiometricGateway,
) : BaseUseCase<SaveBiometricCredentialsUseCase.Params, SaveBiometricCredentialsUseCase.SaveResult, SaveBiometricCredentialsUseCase.FeatureError> {

    data class Params(
        val username: String,
        val password: String,
        val activity: FragmentActivity
    )

    override suspend fun invoke(params: Params): Result<SaveResult, FeatureError> {
        val result = biometricGateway.saveBiometricCredentials(
            username = params.username,
            password = params.password,
            activity = params.activity
        )

        // Map data layer result to domain-specific result
        val domainResult = when (result) {
            is BiometricAuthResult.Success,
            is BiometricAuthResult.EncryptionSuccess -> SaveResult.Success
            BiometricAuthResult.NotAvailable -> SaveResult.NotAvailable
            BiometricAuthResult.NotEnrolled -> SaveResult.NotEnrolled
            BiometricAuthResult.Cancelled -> SaveResult.Cancelled
            is BiometricAuthResult.Failure -> SaveResult.Failure(result.reason)
        }

        return Result.Success(domainResult)
    }

    sealed interface SaveResult {
        data object Success : SaveResult
        data object NotAvailable : SaveResult
        data object NotEnrolled : SaveResult
        data object Cancelled : SaveResult
        data class Failure(val reason: String? = null) : SaveResult
    }

    sealed interface FeatureError {
        data object GenericError : FeatureError
    }
}
