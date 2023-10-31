package com.earthgee.dailytest

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.alibaba.android.arouter.launcher.ARouter
import com.earthgee.dailytest.hotfix.Hotfix
import com.earthgee.dailytest.hotfix.Utils
import com.earthgee.dailytest.sharedpreferenceimpl.SharedPreferencesHelper
import com.earthgee.dailytest.sharedpreferenceimpl.SharedPreferencesImpl
import java.io.File

/**
 *  Created by zhaoruixuan1 on 2023/8/9
 *  test
 *  功能：
 */
class MainApplication : Application(){

    private val sSharedPref = HashMap<String, SharedPreferencesImpl>()

    override fun onCreate() {
        super.onCreate()

        initNuwaHotfix()

        initArouter()
    }

    private fun initNuwaHotfix() {
        val dexPath = File(getDir("dex", Context.MODE_PRIVATE), "hooked_dex.jar")
        Utils.prepareDex(applicationContext, dexPath, "hooked_dex.jar")
        Hotfix.patch(this, dexPath.absolutePath, "com.earthgee.nuwaref.AntilazyLoad")
        try {
            classLoader.loadClass("com.earthgee.nuwaref.AntilazyLoad")
        } catch (exception: Exception) {
            //none
        }
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