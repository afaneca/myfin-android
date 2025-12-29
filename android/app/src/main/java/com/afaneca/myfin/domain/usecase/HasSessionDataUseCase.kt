package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.repository.AuthGateway
import javax.inject.Inject

/**
 * Checks if session data exists in memory.
 * Returns false if data was lost (e.g., due to process death).
 */
class HasSessionDataUseCase @Inject constructor(
    private val authGateway: AuthGateway,
) {
    operator fun invoke(): Boolean {
        return authGateway.getSessionKey() != null
    }
}

