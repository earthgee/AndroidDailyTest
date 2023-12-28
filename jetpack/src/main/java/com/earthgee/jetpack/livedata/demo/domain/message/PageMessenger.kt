package com.earthgee.jetpack.livedata.demo.domain.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.earthgee.jetpack.livedata.demo.data.Moment
import com.kunminx.architecture.domain.message.MutableResult

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
class PageMessenger : ViewModel() {

    val momentResult = MutableResult<Moment>(null)

    val testDelayMsgResult = MutableResult("")

    val dispatchStringResult = MutableResult("")


}