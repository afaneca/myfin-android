package com.afaneca.myfin.data.source

import com.afaneca.myfin.data.model.AttemptLoginResponseDto
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.common.Result

interface NetworkDataSource {

    suspend fun attemptLogin(
        username: String,
        password: String
    ): Result<AttemptLoginResponseDto, DomainError>

}
