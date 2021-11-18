package com.example.sjtu_network

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sjtu_network.api.DoubanBean
import com.example.sjtu_network.dict.youdaoDict
import com.example.sjtu_network.interceptor.TimeConsumeInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.File
import java.io.IOException
import okhttp3.Cache
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    var requestBtn: Button? = null
    var showText: TextView? = null
    var inputText: EditText? = null
    var word: String? = null
//    var cacheSize = 10 * 1024 * 1024
    var mcontext: Context? = null
    var cache:Cache? = null

    val okhttpListener = object : EventListener() {
        override fun dnsStart(call: Call, domainName: String) {
            super.dnsStart(call, domainName)
            showText?.text = showText?.text.toString() + "\nDns Search:" + domainName
        }

        override fun responseBodyStart(call: Call) {
            super.responseBodyStart(call)
            showText?.text = showText?.text.toString() + "\nResponse Start"
        }
    }

    val gson = GsonBuilder().create()
//    val client: OkHttpClient = OkHttpClient
//        .Builder()
//        .cache(cache)
//        .addInterceptor(TimeConsumeInterceptor())
//        .eventListener(okhttpListener)
//        .build()
    var client : OkHttpClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestBtn = findViewById(R.id.send_request)
        showText = findViewById(R.id.show_text)
        inputText = findViewById(R.id.researchbox)
        mcontext = applicationContext
//        cache = Cache(File(mcontext?.cacheDir,"a_cache"), maxSize = cacheSize.toLong())
        cache = Cache(File(mcontext?.cacheDir, "a_cache"), maxSize = 50L * 1024L * 1024L)

        client = OkHttpClient
            .Builder()
            .cache(cache)
            .addInterceptor(TimeConsumeInterceptor())
            .eventListener(okhttpListener)
            .build()

        requestBtn?.setOnClickListener {
            showText?.text = ""
            word = inputText?.text.toString()
            click()
        }
    }

    fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .cacheControl(CacheControl.Builder().maxStale(10,TimeUnit.DAYS).build())
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        client?.newCall(request)?.enqueue(callback)
    }

    fun click() {
        val url = "http://dict.youdao.com/suggest?q=${word}&num=1&doctype=json"
        request(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showText?.text = e.message
            }

            override fun onResponse(call: Call, response: Response) {
//                if (response.isSuccessful) {
//                    if (response.networkResponse != null) {
//                        Log.d("cacheCount:", cache?.hitCount().toString());
                        val bodyString = response.body?.string()
                        val youdaoDict = gson.fromJson(bodyString, youdaoDict::class.java)
                        if (youdaoDict.data.entries == null){
                            showText?.text = "no answer"}
                        else{

                        showText?.text = "${showText?.text.toString()} \n\n\n" +
                                "input: $word \n" +
                                "output: ${youdaoDict.data.entries.get(0).explain}  "
                            Log.d("cacheCount:", cache?.hitCount().toString())
                        }
                }

            })
    }
}