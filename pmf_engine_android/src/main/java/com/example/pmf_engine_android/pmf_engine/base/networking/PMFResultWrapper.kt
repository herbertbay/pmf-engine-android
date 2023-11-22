package com.example.earkickandroid.pmf_engine.networking
import retrofit2.HttpException
import java.lang.Exception

sealed class PMFResultWrapper<out T> {
    data class Success<T>(val data: T) : PMFResultWrapper<T>()
    data class HttpError<T>(val e: HttpException, val code: Int) : PMFResultWrapper<T>()
    data class GenericError(val e: Exception) : PMFResultWrapper<Nothing>()
    data class HttpErrorWithBody<T>(val data: T?) : PMFResultWrapper<T>()
    object NetworkError : PMFResultWrapper<Nothing>()
}