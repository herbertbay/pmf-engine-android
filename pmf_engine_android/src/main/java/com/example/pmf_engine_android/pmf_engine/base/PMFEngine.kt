package com.example.pmf_engine_android.pmf_engine.base

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.earkickandroid.pmf_engine.PMFEngineNetworkService
import com.example.pmf_engine_android.R
import com.example.pmf_engine_android.base.DATA_KEY
import com.example.pmf_engine_android.base.Data
import com.example.pmf_engine_android.base.PMFEngineWebViewFragment
import org.json.JSONObject

public interface PMFEngineInterface {
    fun configure(accountId: String, userId: String, activity: AppCompatActivity)
    fun trackKeyEvent(name: String)
    fun showPMFPopup(eventName: String?)
    fun showPMFPopup()
}

 public class PMFEngine : PMFEngineInterface {
    companion object {
        val default = PMFEngine()
    }

    private var networkService = PMFEngineNetworkService()
    private lateinit var activity: AppCompatActivity
    private var preferences: PMFEnginePreferencesInterface? = null
    override fun configure(accountId: String, userId: String, activity: AppCompatActivity) {
        this.activity = activity
        this.preferences = PMFEnginePreferences(context = activity.applicationContext)
        this.preferences?.setAccountID(accountId)
        this.preferences?.setUserID(userId)
    }

    override fun trackKeyEvent(name: String) {
        var eventName = name
        if (name.isEmpty()) {
            eventName = "default"
        }

        val newActions = preferences?.getKeyActionsPerformedCount()?.toMutableMap()

        val count = newActions?.getOrDefault(eventName, 0)
        if (count != 0 && count != null) {
            newActions[eventName] = count + 1
        } else {
            newActions?.set(eventName, 1)
        }
        newActions?.let { preferences?.setKeyActionsPerformedCount(it) }

        val accountId = preferences?.getAccountID()
        val userId = preferences?.getUserID()

        if ((accountId != null) && (userId != null)) {
            networkService.trackEvent(accountId, userId = userId, eventName = eventName)
        }
    }

    override fun showPMFPopup() {
        showPMFPopup(null)
    }

    override fun showPMFPopup(eventName: String?) {
        showFormPopupIfNeeded(eventName, activity = activity)
    }

    private fun showFormPopupIfNeeded(eventName: String?, activity: AppCompatActivity) {
        val accountId = preferences?.getAccountID()
        val userId = preferences?.getUserID()

        if ((accountId != null) && (userId != null)) {
            networkService.getFormActions(
                userId = userId,
                accountId = accountId,
                eventName = eventName
            ) { it ->
                it.let { forms ->
                    if (forms != null) {
                        if (forms.isNotEmpty()) {
                            val form = forms?.filter { it.type == "form" }?.first()
                            val url = form?.url

                            if (url != null) {
                                activity.runOnUiThread {
                                    showPopup(
                                        url = url,
                                        bgColor = form.formData?.colors?.background,
                                        activity = activity
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showPopup(url: String, bgColor: String?, activity: AppCompatActivity) {
        val accountId = preferences?.getAccountID()
        val userId = preferences?.getUserID()

        if ((accountId != null) && (userId != null)) {
            showWebFragment(url = url, bgColor = bgColor, accountId = accountId, userId = userId, activity = activity)
        }
    }

    private fun showWebFragment(url: String, bgColor: String?, accountId: String, userId: String, activity: AppCompatActivity) {
        val data = Data(url, bgColor)
        val webViewFragment = PMFEngineWebViewFragment()
        val bundle = Bundle()

        bundle.putParcelable(DATA_KEY, data)
        webViewFragment.arguments = bundle

        val screenRootView = FrameLayout(activity.applicationContext)
        screenRootView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        screenRootView.id = ViewCompat.generateViewId()
        screenRootView.setBackgroundResource(R.color.transparent)

        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        activity.addContentView(screenRootView, params)

        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)

        transaction.add(screenRootView.id, webViewFragment)
        transaction.disallowAddToBackStack()
        transaction.commitNowAllowingStateLoss()

        webViewFragment.view?.visibility = View.GONE

        screenRootView.visibility = View.GONE

        webViewFragment.setCloseCallback {
            screenRootView.visibility = View.GONE
        }

        webViewFragment.setLoadedCallback {
            networkService.trackFormShowing(accountId, userId)

            webViewFragment.view?.visibility = View.VISIBLE
            screenRootView.visibility = View.VISIBLE
        }
    }
}

interface PMFEnginePreferencesInterface {
    fun getAccountID(): String?
    fun getUserID(): String?

    fun setAccountID(accountId: String)
    fun setUserID(userId: String)

    fun getKeyActionsPerformedCount(): Map<String, Int>
    fun setKeyActionsPerformedCount(values: MutableMap<String, Int>)
}

@Suppress("SameParameterValue")
class PMFEnginePreferences constructor(private val context: Context) :
    PMFEnginePreferencesInterface {
    private val prefName = "PMFEnginePreferences"
    private val mode = Context.MODE_PRIVATE
    private val userIdKey = "userID"
    private val accountIKey = "accountID"
    private val actionsPerformedCountKey = "keyActionsPerformedCount"
    private fun writeString(context: Context, key: String?, value: String?) {
        getEditor(context).putString(key, value).commit()
    }

    private fun readString(context: Context, key: String?, defaultValue: String?): String? {
        return getPreferences(context).getString(key, defaultValue)
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(prefName, mode)
    }

    private fun getEditor(context: Context): Editor {
        return getPreferences(context).edit()
    }

    override fun getAccountID(): String? {
        return readString(context, accountIKey, null)
    }

    override fun getUserID(): String? {
        return readString(context, userIdKey, null)
    }

    override fun setAccountID(accountId: String) {
        writeString(context, accountIKey, accountId)
    }

    override fun setUserID(userId: String) {
        writeString(context, userIdKey, userId)
    }

    override fun getKeyActionsPerformedCount(): Map<String, Int> {
        val outputMap: Map<String, Int> = HashMap()
        val preferences = getPreferences(context)

        val jsonString =
            preferences.getString(actionsPerformedCountKey, JSONObject().toString()) ?: ""
        val jsonObject = JSONObject(jsonString)
        val keysItr = jsonObject.keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            outputMap[key]
        }
        return outputMap
    }

    override fun setKeyActionsPerformedCount(values: MutableMap<String, Int>) {
        val preferences = getPreferences(context)
        val jsonObject = (values as Map<*, *>?)?.let { JSONObject(it) }
        val jsonString = jsonObject.toString()
        val editor = preferences.edit()
        editor.remove(actionsPerformedCountKey).apply()
        editor.putString(actionsPerformedCountKey, jsonString)
        editor.commit()
    }
}