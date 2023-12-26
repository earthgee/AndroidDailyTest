package com.earthgee.jetpack.livedata

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
open class Result<T>(value: T?) : ProtectedUnPeekLiveData<T>(value)

class MutableResult<T>(value: T?) : Result<T>(value) {

    override fun setValue(value: T) {
        super.setValue(value)
    }

    override fun postValue(value: T) {
        super.postValue(value)
    }

}