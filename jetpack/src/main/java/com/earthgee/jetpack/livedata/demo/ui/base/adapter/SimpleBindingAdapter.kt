package com.earthgee.jetpack.livedata.demo.ui.base.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil

/**
 *  Created by zhaoruixuan1 on 2023/12/20
 *  CopyRight (c) haodf.com
 *  功能：
 */
abstract class SimpleBindingAdapter<M, B : ViewDataBinding>(
    private val layoutId: Int,
    itemCallback: DiffUtil.ItemCallback<M>
) : BaseBindingAdapter<M, B>(itemCallback) {

    override fun getLayoutResId(viewType: Int): Int = layoutId

}