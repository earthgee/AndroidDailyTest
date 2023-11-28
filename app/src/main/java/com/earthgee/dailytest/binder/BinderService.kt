package com.earthgee.dailytest.binder

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 *  Created by zhaoruixuan1 on 2023/11/28
 *  CopyRight (c) haodf.com
 *  功能：
 */
class BinderService : Service(){

    private var mCalculator = Calculator()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mCalculator
    }

    class Calculator: ICalculator.Stub() {

        override fun add(a: Int, b: Int): Int {
            return a + b
        }

    }

}