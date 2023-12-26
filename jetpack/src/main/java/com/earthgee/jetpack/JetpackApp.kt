package com.earthgee.jetpack

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
object JetpackApp: ViewModelStoreOwner {

    override fun getViewModelStore(): ViewModelStore {
        return ViewModelStore()
    }

}