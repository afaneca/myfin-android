package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.repository.AuthGateway
import com.afaneca.myfin.domain.common.Result
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authGateway: AuthGateway
) : BaseUseCase<Unit, Unit, LogoutUseCase.FeatureError> {

    override suspend fun invoke(params: Unit): Result<Unit, FeatureError> {
        return Result.Success(authGateway.logout())
    }

    sealed interface FeatureError {
        data object GenericError : FeatureError
    }
}
