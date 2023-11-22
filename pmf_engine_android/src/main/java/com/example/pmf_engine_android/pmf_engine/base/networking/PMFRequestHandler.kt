package com.example.earkickandroid.pmf_engine.networking
import retrofit2.HttpException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object PMFRequestHandler {

    private val onErrorDefault = { e: Exception ->
    }

    suspend fun <T> doRequest(
        request: suspend () -> T?,
        onError: (e: Exception) -> Any = onErrorDefault
    ): PMFResultWrapper<T> {
        var result: T? = null
        return try {
            result = request()
            PMFResultWrapper.Success<T>(result!!)
        } catch (e: Exception) {
            onError(e)
            e.printStackTrace()
            when (e) {
                is UnknownHostException, is SocketTimeoutException -> PMFResultWrapper.NetworkError
                is HttpException -> PMFResultWrapper.HttpError(e, e.code())
                else -> {
                    PMFResultWrapper.GenericError(e)
                }
            }
        }
    }
}