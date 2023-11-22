package com.example.pmf_engine_android.base

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.pmf_engine_android.databinding.FragmentPMFEngineWebViewBinding
import androidx.core.os.BundleCompat
import com.example.pmf_engine_android.pmf_engine.base.PMFBaseDialogFragment
import kotlinx.android.parcel.Parcelize

const val DATA_KEY = "data"

class PMFEngineWebViewFragment: PMFBaseDialogFragment<FragmentPMFEngineWebViewBinding>() {

    private var closeCallback: () -> Unit = {}
    private var loadedCallback: () -> Unit = {}

    private var webURL: String? = ""
    private var isLoaded: Boolean = false
    private var bgColor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            val data = BundleCompat.getParcelable(bundle, DATA_KEY, Data::class.java)
            webURL = data?.urlString
            bgColor = data?.bgColor
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        loadWebView()
    }

    fun setCloseCallback(closeCallback: () -> Unit) {
        this.closeCallback = closeCallback
    }

    fun setLoadedCallback(loadedCallback: () -> Unit) {
        this.loadedCallback = loadedCallback
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            closeCallback()
            dismiss()
        }
    }

    private fun loadWebView() = with(binding) {
        if (bgColor != null) {
            webView.setBackgroundColor(Color.parseColor(bgColor))
        }

        webView.settings.javaScriptEnabled = true
        CookieManager.getInstance().setAcceptCookie(true)

        if (webURL != null) {
            webView.loadUrl(webURL ?: "")
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url.toString()
                    view?.loadUrl(url)
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    isLoaded = true
                    super.onPageFinished(view, url)
                    Log.d("PMFEngineWebViewFragment", "callback")
                    checkElementVisibility()
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    isLoaded = false
                    val errorMessage = "Something went wrong $error"
                    showToast(errorMessage)
                    super.onReceivedError(view, request, error)
                }
            }
        } else {
            val errorMessage = "Web link is empty!"
            showToast(errorMessage)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkElementVisibility() = with(binding) {
        val javascript = "var elements = document.getElementsByClassName('step-heading'); elements.length > 0 && elements[0].offsetParent !== null;"
        webView.evaluateJavascript(javascript) { result ->
                if (result.toBoolean()) {
                    loadedCallback()
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        recheckElementVisibility()
                    }, 500)
            }
        }
    }

    private fun recheckElementVisibility() {
        checkElementVisibility()
    }

}
@Parcelize
class Data(val urlString: String, val bgColor: String?) : Parcelable {
}