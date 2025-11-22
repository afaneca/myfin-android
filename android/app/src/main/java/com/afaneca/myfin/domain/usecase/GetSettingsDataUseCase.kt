package com.afaneca.myfin.domain.usecase

import android.util.Log
import com.afaneca.myfin.domain.error.DomainError

import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.model.SettingsData
import com.afaneca.myfin.domain.repository.SettingsGateway
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.HttpUrl
import javax.inject.Inject

class GetSettingsDataUseCase @Inject constructor(
    private val settingsGateway: SettingsGateway
) : BaseUseCase<Unit, SettingsData, GetSettingsDataUseCase.FeatureError> {

    override suspend operator fun invoke(params: Unit): Result<SettingsData, FeatureError> =
        coroutineScope {
            val baseApiUrlDeferred = async { getBaseApiUrl() }
            val useDynamicThemeDeferred = async { getUseDynamicTheme() }
            val baseApiUrl = (baseApiUrlDeferred.await() as? Result.Success)?.data
                ?: return@coroutineScope Result.Error(FeatureError.GenericError)
            val useDynamicTheme = (useDynamicThemeDeferred.await() as? Result.Success)?.data
                ?: return@coroutineScope Result.Error(FeatureError.GenericError)
            Result.Success(
                SettingsData(
                    baseApiUrl = baseApiUrl,
                    useDynamicTheme = useDynamicTheme,
                ),
            )
        }

    private suspend fun getBaseApiUrl(): Result<HttpUrl, DomainError> {
        return settingsGateway.getBaseApiUrl()
    }

    private suspend fun getUseDynamicTheme(): Result<Boolean, DomainError> {
        return settingsGateway.getUseDynamicTheme()
    }


    sealed interface FeatureError {
        data object GenericError : FeatureError
    }
}