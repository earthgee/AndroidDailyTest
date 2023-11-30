package com.earthgee.aidlservice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return MyBinder()
    }

    class MyBinder: IMyAidlInterface.Stub() {

        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ): String {
            return "Hello World2"
        }

        override fun add(a: Int, b: Int) {
            Log.d("earthgee", "${a+b}")
        }

    }

}