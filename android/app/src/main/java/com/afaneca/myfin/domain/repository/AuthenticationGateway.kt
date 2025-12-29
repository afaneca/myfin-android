package com.afaneca.myfin.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthenticationGateway {
    fun observeIsAuthenticated(): Flow<Boolean>
}

