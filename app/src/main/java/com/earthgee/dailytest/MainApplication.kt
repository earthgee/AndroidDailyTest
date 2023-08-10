package com.earthgee.dailytest

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter

/**
 *  Created by zhaoruixuan1 on 2023/8/9
 *  CopyRight (c) haodf.com
 *  功能：
 */
class MainApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        initArouter()
    }

    private fun initArouter() {
        if(BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }

}