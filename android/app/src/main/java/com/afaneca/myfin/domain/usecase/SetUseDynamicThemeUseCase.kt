package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.repository.SettingsGateway
import javax.inject.Inject

class SetUseDynamicThemeUseCase @Inject constructor(
    private val settingsGateway: SettingsGateway
) : BaseUseCase<SetUseDynamicThemeUseCase.Params, Boolean, SetUseDynamicThemeUseCase.FeatureError> {

    data class Params(val useDynamicTheme: Boolean)

    override suspend operator fun invoke(params: Params): Result<Boolean, FeatureError> {
        return when (val result = settingsGateway.setUseDynamicTheme(params.useDynamicTheme)) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> {
                val error = when (result.error) {
                    else -> FeatureError.GenericError
                }
                Result.Error(error)
            }
        }
    }

    sealed interface FeatureError {
        data object GenericError : FeatureError
    }
}