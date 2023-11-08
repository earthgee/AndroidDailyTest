package com.earthgee.dailytest

import android.content.Context
import android.content.SharedPreferences
import com.alibaba.android.arouter.launcher.ARouter
import com.earthgee.dailytest.hotfix.Hotfix
import com.earthgee.dailytest.hotfix.Utils
import com.earthgee.dailytest.hotfix.tinker.SampleApplicationContext
import com.earthgee.dailytest.sharedpreferenceimpl.SharedPreferencesHelper
import com.earthgee.dailytest.sharedpreferenceimpl.SharedPreferencesImpl
import java.io.File

import com.earthgee.dailytest.BuildConfig

/**
 *  Created by zhaoruixuan1 on 2023/11/7
 *  CopyRight (c) haodf.com
 *  功能：
 */
object ApplicationLikeUtil {

    private val sSharedPref = HashMap<String, SharedPreferencesImpl>()

    @JvmStatic
    fun onCreate() {
        //initNuwaHotfix()
        initArouter()
    }

    private fun initNuwaHotfix() {
        val dexPath = File(SampleApplicationContext.application.getDir("dex", Context.MODE_PRIVATE), "hooked_dex.jar")
        Utils.prepareDex(SampleApplicationContext.application, dexPath, "hooked_dex.jar")
        Hotfix.patch(SampleApplicationContext.application, dexPath.absolutePath, "com.earthgee.nuwaref.AntilazyLoad")
        try {
            SampleApplicationContext.application.classLoader.loadClass("com.earthgee.nuwaref.AntilazyLoad")
        } catch (exception: Exception) {
            //none
        }
    }

    private fun initArouter() {
        if(BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(SampleApplicationContext.application)
    }

//    @JvmStatic
//    fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
//        name?: return super.getSharedPreferences(name, mode)
//
//        if(!SharedPreferencesHelper.canUseCustomSp()) {
//            return super.getSharedPreferences(name, mode)
//        }
//
//        var sp: SharedPreferencesImpl
//        synchronized(sSharedPref) {
//            sp = sSharedPref[name] ?: return super.getSharedPreferences(name, mode)
//            if(sp == null) {
//                val prefsFile = SharedPreferencesHelper.getSharedPrefsFile(this, name)
//                sp = SharedPreferencesImpl(prefsFile, mode)
//                sSharedPref[name] = sp
//                return sp
//            }
//        }
//
//        if((mode and Context.MODE_MULTI_PROCESS) != 0) {
//            sp.startReloadIfChangedUnexpectedly()
//        }
//
//        return sp
//    }

}