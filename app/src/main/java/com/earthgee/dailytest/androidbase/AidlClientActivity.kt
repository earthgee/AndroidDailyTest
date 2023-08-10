package com.earthgee.dailytest.androidbase

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.earthgee.aidlservice.IMyAidlInterface
import com.earthgee.dailytest.R

class AidlClientActivity : AppCompatActivity() {

    private lateinit var myAidlInterface: IMyAidlInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidl_client)

        val text=findViewById<TextView>(R.id.text)

        val serviceConnection = object: ServiceConnection {

            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                myAidlInterface = IMyAidlInterface.Stub.asInterface(p1)
                val str = myAidlInterface.basicTypes(0,0L,false,0.0f,0.0f.toDouble(),"")
                text.text = str
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
            }

        }

        val intent = Intent()
        intent.action = "com.earthgee.aidlservice"
        intent.`package` = "com.earthgee.aidlservice"
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }
}