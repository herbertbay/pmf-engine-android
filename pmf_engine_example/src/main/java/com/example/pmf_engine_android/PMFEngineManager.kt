package com.example.pmf_engine_android
import androidx.appcompat.app.AppCompatActivity
import com.example.pmf_engine_android.pmf_engine.base.PMFEngine
import java.util.UUID

class PMFEngineManager {

    fun configure(activity: AppCompatActivity)
    {
        val userID = "68d001fb-2cee-45b4-8627-fc01cbc1242f"
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