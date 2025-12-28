package com.afaneca.myfin.domain.usecase

import androidx.fragment.app.FragmentActivity
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.model.BiometricAuthResult
import com.afaneca.myfin.domain.model.LoginResult
import com.afaneca.myfin.domain.repository.BiometricGateway
import javax.inject.Inject

class BiometricLoginUseCase @Inject constructor(
    private val biometricGateway: BiometricGateway,
    private val loginUseCase: LoginUseCase,
) : BaseUseCase<BiometricLoginUseCase.Params, LoginResult, BiometricLoginUseCase.FeatureError> {

    data class Params(val activity: FragmentActivity)

    override suspend fun invoke(params: Params): Result<LoginResult, FeatureError> {
        // 1. Get stored credentials
        val credentials = biometricGateway.getBiometricCredentials()
            ?: return Result.Error(FeatureError.CredentialsNotFound)

        // 2. Authenticate with biometric and decrypt password
        val authResult = biometricGateway.authenticateWithBiometric(params.activity)

        val decryptedPassword = when (authResult) {
            is BiometricAuthResult.Success -> authResult.data
            BiometricAuthResult.Cancelled -> {
                return Result.Error(FeatureError.Cancelled)
            }
            is BiometricAuthResult.Failure -> {
                return if (authResult.errorCode != null) {
                    Result.Error(FeatureError.AuthenticationFailed(authResult.errorCode))
                } else {
                    Result.Error(FeatureError.AuthenticationFailed(null))
                }
            }
            // These shouldn't occur but handle them anyway
            BiometricAuthResult.NotAvailable,
            BiometricAuthResult.NotEnrolled,
            is BiometricAuthResult.EncryptionSuccess -> {
                return Result.Error(FeatureError.UnexpectedError)
            }
        }

        // 3. Perform login with decrypted credentials
        val loginParams = LoginUseCase.LoginParams(
            username = credentials.username,
            password = decryptedPassword
        )

        return when (val loginResult = loginUseCase(loginParams)) {
            is Result.Success -> Result.Success(loginResult.data)
            is Result.Error -> {
                // Map login errors to biometric login errors
                val error = when (loginResult.error) {
                    LoginUseCase.FeatureError.InvalidCredentials -> FeatureError.InvalidCredentials
                    LoginUseCase.FeatureError.GenericError -> FeatureError.LoginFailed
                }
                Result.Error(error)
            }
        }
    }

    sealed interface FeatureError {
        data object CredentialsNotFound : FeatureError
        data object Cancelled : FeatureError
        data class AuthenticationFailed(val errorCode: Int?) : FeatureError
        data object InvalidCredentials : FeatureError
        data object LoginFailed : FeatureError
        data object UnexpectedError : FeatureError
    }
}
