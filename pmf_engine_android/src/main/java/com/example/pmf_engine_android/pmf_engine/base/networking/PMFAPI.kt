package com.example.earkickandroid.pmf_engine.networking
import com.example.earkickandroid.pmf_engine.BoolResponse
import com.example.earkickandroid.pmf_engine.CommandResponse
import com.example.earkickandroid.pmf_engine.EventData
import com.example.earkickandroid.pmf_engine.UserData
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PMFApi {
    @POST("/eventRecord")
    suspend fun trackEnent(
        @Header("Content-Type") contentType: String,
        @Body body: Map<String, EventData>
    ): BoolResponse

    @POST("/userGetCommand")
    suspend fun getFormActions(
        @Header("Content-Type") contentType: String,
        @Body body:  Map<String, UserData>
    ): CommandResponse
}