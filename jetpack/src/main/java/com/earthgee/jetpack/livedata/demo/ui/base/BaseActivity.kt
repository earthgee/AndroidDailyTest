package com.earthgee.jetpack.livedata.demo.ui.base

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.earthgee.jetpack.JetpackApp
import com.earthgee.jetpack.livedata.demo.utils.AdaptScreenUtils
import com.earthgee.jetpack.livedata.demo.utils.BarUtils
import com.earthgee.jetpack.livedata.demo.utils.ScreenUtils

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
open class BaseActivity : AppCompatActivity() {

    val mActivityProvider: ViewModelProvider by lazy {
        ViewModelProvider(this)
    }

    val mApplicationProvider: ViewModelProvider by lazy {
        ViewModelProvider(JetpackApp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
        BarUtils.setStatusBarLightMode(this, true)
    }

    override fun getResources(): Resources {
        if (ScreenUtils.isPortrait()) {
            return AdaptScreenUtils.adaptWidth(super.getResources(), 360)
        } else {
            return AdaptScreenUtils.adaptHeight(super.getResources(), 640)
        }
    }

    fun showLongToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    fun showShortToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    inline fun <reified T : ViewModel> getActivityViewModel(): T {
        return mActivityProvider[T::class.java]
    }

    inline fun <reified T : ViewModel> getApplicationViewModel(): T {
        return mApplicationProvider[T::class.java]
    }

}

