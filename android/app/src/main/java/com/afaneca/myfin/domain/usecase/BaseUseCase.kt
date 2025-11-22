package com.afaneca.myfin.domain.usecase

import com.afaneca.myfin.domain.common.Result

/**
 * A base interface for all use cases in the application.
 *
 * @param P The type of the parameters for the use case.
 * @param S The type of the success output.
 * @param E The type of the error output.
 */
interface BaseUseCase<in P, out S, out E> where E : Any {
    suspend operator fun invoke(params: P): Result<S, E>
}
