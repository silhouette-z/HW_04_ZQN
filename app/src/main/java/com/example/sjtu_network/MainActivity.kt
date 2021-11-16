package com.example.sjtu_network

import android.annotation.SuppressLint
import android.os.Bundle
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
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var requestBtn: Button? = null
    var showText: TextView? = null
    var inputText: EditText? = null
    var word: String? = null

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
    val client: OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(TimeConsumeInterceptor())
        .eventListener(okhttpListener).build()

    val gson = GsonBuilder().create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestBtn = findViewById(R.id.send_request)
        showText = findViewById(R.id.show_text)
        inputText = findViewById(R.id.researchbox)

        requestBtn?.setOnClickListener {
            showText?.text = ""
            word = inputText?.text.toString()
            click()
        }
    }

    fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun click() {
        val url = "http://dict.youdao.com/suggest?q=${word}&num=1&doctype=json"
        request(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showText?.text = e.message
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                val youdaoDict = gson.fromJson(bodyString, youdaoDict::class.java)

                    showText?.text = "${showText?.text.toString()} \n\n\n" +
                            "input: $word \n" +
                            "output: ${youdaoDict.data.entries.get(0).explain}  "

            }
        })
    }
}