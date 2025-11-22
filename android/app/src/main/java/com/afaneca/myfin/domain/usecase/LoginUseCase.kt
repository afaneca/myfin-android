package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.model.LoginResult
import com.afaneca.myfin.domain.repository.AuthGateway
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.repository.AccountGateway
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authGateway: AuthGateway,
    private val accountGateway: AccountGateway,
) : BaseUseCase<LoginUseCase.LoginParams, LoginResult, LoginUseCase.FeatureError> {

    data class LoginParams(val username: String, val password: String)

    override suspend operator fun invoke(params: LoginParams): Result<LoginResult, FeatureError> {
        return when (val result = authGateway.login(params.username, params.password)) {
            is Result.Success -> {
                accountGateway.refreshBalanceDigest(result.data.accounts)
                Result.Success(result.data)
            }

            is Result.Error -> {
                val loginError = when (result.error) {
                    DomainError.Unauthorized -> FeatureError.InvalidCredentials
                    else -> FeatureError.GenericError
                }
                Result.Error(loginError)
            }
        }
    }

    sealed interface FeatureError {
        data object InvalidCredentials : FeatureError
        data object GenericError : FeatureError
    }
}
