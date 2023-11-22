package com.example.earkickandroid.pmf_engine
import android.util.Log
import com.example.earkickandroid.pmf_engine.networking.PMFNetworkService
import com.example.earkickandroid.pmf_engine.networking.PMFRequestHandler
import com.example.earkickandroid.pmf_engine.networking.PMFResultWrapper
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface PMFEngineNetworkServiceInterface {
    fun trackEvent(accountId: String, userId: String, eventName: String): Job
    fun getFormActions(forceShow: Boolean, accountId: String, userId: String, eventName: String?, completionHandler: (List<CommandEntity>?) -> Unit): Job
    fun trackFormShowing(accountId: String, userId: String)
}

class PMFEngineNetworkService: PMFEngineNetworkServiceInterface {
    override fun trackEvent(accountId: String, userId: String, eventName: String): Job = GlobalScope.launch {
        var eventData = EventData(userId = userId, accountId = accountId, eventName = eventName)
        val api = PMFNetworkService.getApi()
        val data = mapOf("data" to eventData)
        val result = PMFRequestHandler.doRequest(
            request = { api.trackEnent(contentType = "application/json", body = data) }
        )
    }

    override fun getFormActions(
        forceShow: Boolean,
        accountId: String,
        userId: String,
        eventName: String?,
        completionHandler: (List<CommandEntity>?) -> Unit
    ): Job = GlobalScope.launch {
        var userData = UserData(userId = userId, accountId = accountId, userAgent = "android",forceShow = forceShow, eventName = eventName)
        val api = PMFNetworkService.getApi()
        val data = mapOf("data" to userData)

        val result = PMFRequestHandler.doRequest(
            request = { api.getFormActions(contentType = "application/json", body = data) }
        )

        if (result is PMFResultWrapper.Success){
            completionHandler(result.data.result?.commands)
        } else {
            completionHandler(null)
        }
    }

    override fun trackFormShowing(accountId: String, userId: String) {
        trackEvent(accountId = accountId, userId = userId, eventName = "feedback-form-shown")
    }
}

data class UserData(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("userAgent")
    val userAgent: String,
    @SerializedName("forceShowForm")
    val forceShow: Boolean,
    @SerializedName("eventName")
    val eventName: String?
) {}

data class EventData(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("eventName")
    val eventName: String
) {}
data class CommandEntity(
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("formData")
    val formData: FormData?
) {}

data class FormData(
    @SerializedName("colors")
    val colors: ColorData?
) {}

data class ColorData(
    @SerializedName("background")
    val background: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("brandLight")
    val brandLight: String?,
    @SerializedName("brand")
    val brand: String?
) {}


data class CommandResponse(
    @SerializedName("result")
    val result: CommandResponseResult?
) {}

data class CommandResponseResult(
    @SerializedName("commands")
    val commands: List<CommandEntity>?,
    @SerializedName("success")
    val success: Boolean
) {}


data class BoolResponse(
    @SerializedName("result")
    val result: BoolResult
) {}

data class BoolResult(
    @SerializedName("success")
    val success: Boolean
) {}