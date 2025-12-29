package com.afaneca.myfin.data.repository

import com.afaneca.myfin.data.source.TransientDataSource
import com.afaneca.myfin.domain.repository.AuthenticationGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val transientDataSource: TransientDataSource,
) : AuthenticationGateway {
    override fun observeIsAuthenticated(): Flow<Boolean> {
        return transientDataSource.observeIsAuthenticated()
    }
}

