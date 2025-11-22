package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.repository.SettingsGateway
import javax.inject.Inject

class SetBaseApiUrlUseCase @Inject constructor(
    private val settingsGateway: SettingsGateway,
) : BaseUseCase<SetBaseApiUrlUseCase.Params, Boolean, SetBaseApiUrlUseCase.FeatureError> {

    data class Params(val url: String)

    override suspend operator fun invoke(params: Params): Result<Boolean, FeatureError> {
        return when (val result = settingsGateway.setBaseApiUrl(params.url)) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> {
                val error = when (result.error) {
                    DomainError.InvalidFormat -> FeatureError.InvalidUrlFormat
                    else -> FeatureError.GenericError
                }
                Result.Error(error)
            }
        }
    }

    sealed interface FeatureError {
        data object InvalidUrlFormat : FeatureError
        data object GenericError : FeatureError
    }
}