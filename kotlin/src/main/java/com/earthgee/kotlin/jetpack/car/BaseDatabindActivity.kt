package com.earthgee.kotlin.jetpack.car

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 *  Created by zhaoruixuan1 on 2023/12/13
 *  CopyRight (c) haodf.com
 *  功能：
 */
abstract class BaseDatabindActivity<V: ViewDataBinding>: AppCompatActivity() {

    lateinit var mBinding: V

    abstract fun initView()

    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding.lifecycleOwner = this
        mBinding.executePendingBindings()
        initView()
    }

}