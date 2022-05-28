package com.lib.polkalib

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.math.BigDecimal
import org.json.JSONObject





class PolkaUtils (val onSuccess : (message:String)-> Unit) {
    private lateinit var mycontext: Context

    private fun getCustomHeaders(): Map<String, String>? {
        val headers: MutableMap<String, String> = HashMap()
        headers["Access-Control-Allow-Origin"] = "*"
        return headers
    }

    fun createMnemonics(c:Context) {
        mycontext=c
        var webvw = WebView(mycontext)
        webvw.addJavascriptInterface(this, "Android")
        webvw.settings.javaScriptEnabled = true

        webvw.loadUrl("file:///android_asset/substrate.html")

    }

    fun importMnemonics(c:Context,mnemonics: String) {
        mycontext=c
        var webvw = WebView(mycontext)
        webvw.addJavascriptInterface(this, "Android")
        webvw.settings.javaScriptEnabled = true
        webvw.loadUrl("file:///android_asset/substrate_custom_address.html")
        webvw.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                webvw.loadUrl("javascript:var userMnemonics = '$mnemonics'")
            }
        }
    }

    fun sendTrx(c:Context, from_address: String, fromMnemonics: String, amount: String, toAddress: String) {
        mycontext=c
        var webvw = WebView(mycontext)
        webvw.addJavascriptInterface(this, "Android")
        webvw.settings.javaScriptEnabled = true
        webvw.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                getCustomHeaders()?.let { view!!.loadUrl(request!!.getUrl().toString(), it) };
                return true;
            }

        }

        webvw.loadUrl("file:///android_asset/send.html")
        val amtChanged = amount.toDouble() * 1000000000000
        val amtTobeTransferred = BigDecimal(amtChanged).toBigInteger()
        webvw.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                webvw.loadUrl("javascript:var from_address = '$from_address'")
                webvw.loadUrl("javascript:var fromMnemonics = '$fromMnemonics'")
                webvw.loadUrl("javascript:var amtTobeTransferred = '$amtTobeTransferred'")
                webvw.loadUrl("javascript:var toAddress = '$toAddress'")
                webvw.loadUrl("javascript:var endPoint = 'http://54.225.175.51:9933'")
//              webvw.loadUrl("javascript:var endPoint = 'https://polkadot-westend--rpc.datahub.figment.io/apikey/b5209f5c8a2ac8f2220df51a00606921'")

            }


        }
    }

    @JavascriptInterface
    fun showWebViewMessage(result: String) {
        onSuccess(result)
    }


}