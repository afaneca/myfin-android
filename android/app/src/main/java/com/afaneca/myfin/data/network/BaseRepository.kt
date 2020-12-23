package com.afaneca.myfin.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by me on 21/12/2020
 */
abstract class BaseRepository {
    suspend fun <T> safeAPICall(
        apiCall: suspend () -> T
    ) : Resource<T>{
        return withContext(Dispatchers.IO){
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable){
                Resource.Failure(throwable.localizedMessage)
            }
        }
    }
}