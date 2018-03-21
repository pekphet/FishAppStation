package com.xiaozi.appstore.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import com.google.gson.Gson
import com.umeng.analytics.MobclickAgent
import com.xiaozi.appstore.R
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.component.Analisys
import com.xiaozi.appstore.component.Device
import com.xiaozi.appstore.component.Framework
import kotlinx.android.synthetic.main.a_web.*

/**
 * Created by fish on 18-1-18.
 */
class WebActivity : Activity() {

    companion object {
        fun start(context: Context, url: String?) {
            if (url == null || url.equals("")) {
                return
            }
            context.startActivity(Intent(context, WebActivity::class.java).apply {
                putExtra("open_url", url)
            })
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_web)
        initWebView()
    }

    private fun initWebView() {
        WebViewKit.default(this@WebActivity, web_single)
        val url: String? = intent.getStringExtra("open_url")
        if (url?.equals("") ?: true) {
            finish()
            return
        }
        web_single.loadUrl(url)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && web_single.canGoBack()) {
            web_single.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        Analisys.resume(this)
    }

    override fun onPause() {
        super.onPause()
        Analisys.pause(this)
    }
}

object WebViewKit : DownloadListener {
    fun default(activity: Activity, mWeb: WebView?) {
        mWeb?.settings?.apply {
            javaScriptEnabled = true
            pluginState = WebSettings.PluginState.ON
            javaScriptCanOpenWindowsAutomatically = true
            allowFileAccess = true
            useWideViewPort = true
            loadWithOverviewMode = false
            setAppCacheEnabled(false)
            setRenderPriority(WebSettings.RenderPriority.HIGH)
        }
        mWeb?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url?.startsWith("http://") ?: true || url?.startsWith("https://") ?: true) {
                    return false
                }
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    activity.startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    ex.printStackTrace()
                    return true
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                mWeb?.loadUrl("about:blank")
                activity.finish()
                return true
            }
        }
        mWeb?.addJavascriptInterface(JSInterface(activity, mWeb), "bridge")
        mWeb?.setDownloadListener(this)
    }

    override fun onDownloadStart(url: String?, userAgent: String?, contentDisposition: String?, mimetype: String?, contentLength: Long) {
        callDownload(url ?: return)
    }

    fun callDownload(url: String) {
        Framework._C.startActivity(Intent().apply {
            flags = FLAG_ACTIVITY_NEW_TASK
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        })
    }

    class JSInterface(val activity: Activity, val web: WebView?) {

        @JavascriptInterface
        fun back(isForce: Boolean) {
            Framework._H.post {
                if (web?.canGoBack() ?: return@post)
                    web.goBack()
                else if (isForce)
                    activity.finish()
            }
        }

        @JavascriptInterface
        fun close() {
            Framework._H.post { activity.finish() }
        }

        @JavascriptInterface
        fun toast(msg: String) = activity.ZToast(msg)

        @JavascriptInterface
        fun getImei() = Device.getIMEI()

    }
}