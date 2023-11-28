package com.earthgee.dailytest.binder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.earthgee.dailytest.R

/**
 *  Created by zhaoruixuan1 on 2023/11/28
 *  CopyRight (c) haodf.com
 *  功能：
 */
@Route(path = "/binder/main")
class BinderClientActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binder_client)

        val intent=Intent(this, BinderService::class.java)
        bindService(intent, object: ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val calculator = service?.let { ICalculator.Proxy(it) }
                val result = calculator?.add(1,1)
                findViewById<TextView>(R.id.title).text = "$result"
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                //none
            }

        }, Context.BIND_AUTO_CREATE)
    }

}