package com.example.pmf_engine_android
import com.example.pmf_engine_android.pmf_engine.base.PMFEngine
import androidx.appcompat.app.AppCompatActivity
import java.util.UUID

class PMFEngineManager {

    fun configure(activity: AppCompatActivity)
    {
        val userID = UUID.randomUUID().toString()
        val accountId = "earkick"
        PMFEngine.default.configure(accountId = accountId, userId = userID, activity = activity)
        PMFEngine.default.trackKeyEvent("journal")
        PMFEngine.default.trackKeyEvent("")
    }

    fun showPopupIfNeeded()
    {
        PMFEngine.default.forceShowPMFPopup(null)
    }
}