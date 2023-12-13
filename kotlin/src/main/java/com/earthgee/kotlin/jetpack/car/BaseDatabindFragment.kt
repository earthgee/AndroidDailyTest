package com.earthgee.kotlin.jetpack.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 *  Created by zhaoruixuan1 on 2023/12/13
 *  CopyRight (c) haodf.com
 *  功能：
 */
abstract class BaseDatabindFragment<V: ViewDataBinding> : Fragment(){

    lateinit var mBinding: V

    abstract fun initView()

    abstract fun getLayoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.executePendingBindings()
        initView()
        return mBinding.root
    }

}