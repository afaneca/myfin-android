package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.repository.AuthGateway
import com.afaneca.myfin.domain.repository.BiometricGateway
import com.afaneca.myfin.domain.common.Result
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authGateway: AuthGateway,
    private val biometricGateway: BiometricGateway,
) : BaseUseCase<Unit, Unit, LogoutUseCase.FeatureError> {

    override suspend fun invoke(params: Unit): Result<Unit, FeatureError> {
        authGateway.logout()
        biometricGateway.disableBiometric()
        return Result.Success(Unit)
    }

    sealed interface FeatureError {
        data object GenericError : FeatureError
    }
}
