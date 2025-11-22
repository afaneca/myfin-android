package com.afaneca.myfin.domain.common

import com.afaneca.myfin.domain.error.DomainError

/**
 * A generic sealed class that represents a result of an operation,
 * holding either a success value [S] or an error value [E] which must implement [DomainError].
 */
sealed class Result<out S, out E> {
    data class Success<out S>(val data: S) : Result<S, Nothing>()
    data class Error<out E>(val error: E, val exception: Exception? = null) : Result<Nothing, E>()

    fun isSuccess() = this is Success
    fun isError() = this is Error
}

/**
 * Maps a [Result<S, E>] to [Result<R, E>] by applying a transform function to a success value.
 */
inline fun <S, R, E> Result<S, E>.map(transform: (S) -> R): Result<R, E> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
    }
}
