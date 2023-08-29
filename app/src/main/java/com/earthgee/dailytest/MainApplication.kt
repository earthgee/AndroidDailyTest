package com.earthgee.dailytest

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.alibaba.android.arouter.launcher.ARouter
import com.earthgee.dailytest.sharedpreferenceimpl.SharedPreferencesHelper
import com.earthgee.dailytest.sharedpreferenceimpl.SharedPreferencesImpl

/**
 *  Created by zhaoruixuan1 on 2023/8/9
 *  CopyRight (c) haodf.com
 *  功能：
 */
class MainApplication : Application(){

    private val sSharedPref = HashMap<String, SharedPreferencesImpl>()

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

    override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences {
        name?: return super.getSharedPreferences(name, mode)

        if(!SharedPreferencesHelper.canUseCustomSp()) {
            return super.getSharedPreferences(name, mode)
        }

        var sp: SharedPreferencesImpl
        synchronized(sSharedPref) {
            sp = sSharedPref[name] ?: return super.getSharedPreferences(name, mode)
            if(sp == null) {
                val prefsFile = SharedPreferencesHelper.getSharedPrefsFile(this, name)
                sp = SharedPreferencesImpl(prefsFile, mode)
                sSharedPref[name] = sp
                return sp
            }
        }

        if((mode and Context.MODE_MULTI_PROCESS) != 0) {
            sp.startReloadIfChangedUnexpectedly()
        }

        return sp
    }

}