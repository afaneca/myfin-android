package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.repository.SettingsGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUseDynamicThemeUseCase @Inject constructor(
    private val settingsGateway: SettingsGateway,
) {
    operator fun invoke(): Flow<Boolean> {
        return settingsGateway.observeUseDynamicTheme()
    }
}
