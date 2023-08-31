package com.earthgee.systrace

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.earthgee.simulate_systrace.R
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 *  Created by zhaoruixuan1 on 2023/8/31
 *  CopyRight (c) haodf.com
 *  功能：
 */
@Route(path = "/atrace/sockethook")
class SocketHookActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_socket_hook)
        findViewById<View>(R.id.button).setOnClickListener {
            ATrace.enableSocketHook()
            Toast.makeText(this@SocketHookActivity, "开启成功", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.newrequest).setOnClickListener {
            Thread {
                Log.e("HOOOOOOOOK", "respond:" + getURLResponse("https://www.baidu.com"))
            }.start()
        }
    }

    private fun getURLResponse(urlString: String): String? {
        var conn: HttpURLConnection? = null
        var `is`: InputStream? = null
        val stringBuffer = StringBuffer()
        try {
            val url = URL(urlString)
            conn = url.openConnection() as HttpURLConnection
            conn.setRequestMethod("GET")
            `is` = conn.getInputStream()
            val isr = InputStreamReader(`is`)
            val bufferReader = BufferedReader(isr)
            var inputLine: String?
            while (bufferReader.readLine().also { inputLine = it } != null) {
                stringBuffer.append(inputLine).append("\n")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (conn != null) {
                conn.disconnect()
            }
        }
        return stringBuffer.toString()
    }

}