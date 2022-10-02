package com.example.mygitapplication.infra.repo

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Error(val throwable: Throwable? = null, val errorMessage: String? = null) : ResultWrapper<Nothing>()
}

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher = Dispatchers.IO, apiCall: suspend () -> T): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            Log.i("ResultWrapper",throwable.toString())

            when (throwable) {
                is IOException -> ResultWrapper.Error(throwable, null)
                is HttpException -> {
                    val errorResponse = convertErrorBody(throwable)
                    ResultWrapper.Error(throwable, errorResponse)
                }
                else -> {
                    ResultWrapper.Error(null, null)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.source()?.toString()
    } catch (exception: Exception) {
        null
    }
}
