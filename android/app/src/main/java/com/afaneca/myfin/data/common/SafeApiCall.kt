package com.afaneca.myfin.data.common

import com.afaneca.myfin.data.source.TransientDataSource
import com.afaneca.myfin.domain.error.DomainError
import com.afaneca.myfin.domain.common.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * A utility function that wraps a network API call in a try-catch block and ensures it runs on the IO dispatcher.
 * It translates raw exceptions into a structured [DomainError].
 *
 * When a 401 (Unauthorized) error is detected, it automatically updates the global authentication state
 * which triggers a logout and redirect in the MainViewModel.
 *
 * @param T The type of the successful response data.
 * @param transientDataSource The transient data source managing in-memory state.
 * @param apiCall The suspend lambda function representing the network call.
 * @return A [Result] wrapper containing either the success data [T] or a [DomainError].
 */
suspend fun <T> safeApiCall(
    transientDataSource: TransientDataSource,
    apiCall: suspend () -> T,
): Result<T, DomainError> {
    return withContext(Dispatchers.IO) {
        try {
            Result.Success(apiCall())
        } catch (e: HttpException) {
            val error = when (e.code()) {
                401, 403 -> {
                    transientDataSource.setUnauthenticated()
                    DomainError.Unauthorized
                }
                in 500..599 -> DomainError.ServerError
                else -> DomainError.Unknown
            }
            Result.Error(error = error, exception = e)
        } catch (e: IOException) {
            Result.Error(error = DomainError.Network, exception = e)
        } catch (e: Exception) {
            Result.Error(error = DomainError.Unknown, exception = e)
        }
    }
}
