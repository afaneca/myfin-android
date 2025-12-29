package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.repository.AuthenticationGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthenticatedUseCase @Inject constructor(
    private val authenticationGateway: AuthenticationGateway,
) {
    operator fun invoke(): Flow<Boolean> {
        return authenticationGateway.observeIsAuthenticated()
    }
}

