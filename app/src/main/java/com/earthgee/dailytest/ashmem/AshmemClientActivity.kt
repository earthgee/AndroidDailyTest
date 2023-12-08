package com.earthgee.dailytest.ashmem

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.earthgee.dailytest.R
import java.io.FileReader

/**
 *  Created by zhaoruixuan1 on 2023/11/30
 *  CopyRight (c) haodf.com
 *  功能：
 */
class AshmemClientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binder_client)

        val intent = Intent(this, AshmemService::class.java)
        bindService(intent, object : ServiceConnection {

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()

//                data.writeBlob()

                service?.transact(1, data, reply, 0)
                val fd = reply.readFileDescriptor().fileDescriptor
                findViewById<TextView>(R.id.title).text = FileReader(fd).readText()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                //none
            }

        }, Context.BIND_AUTO_CREATE)
    }

}