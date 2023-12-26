package com.earthgee.jetpack.livedata.demo.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.earthgee.jetpack.R
import com.earthgee.jetpack.databinding.JetpackActivityMainBinding
import com.earthgee.jetpack.livedata.demo.domain.message.PageMessenger
import com.earthgee.jetpack.livedata.demo.ui.base.BaseActivity

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
@Route(path = "/jetpack/main")
class MainActivity : BaseActivity() {

    private val mPageMessenger by lazy {
        getApplicationViewModel<PageMessenger>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<JetpackActivityMainBinding>(
            this,
            R.layout.jetpack_activity_main
        )
        binding.click = ClickProxy()

    }

    inner class ClickProxy {

        fun toSecondActivity() {

        }

        fun toMultiObserverTest() {

        }

        fun toObserverForeverTest() {

        }

    }

}