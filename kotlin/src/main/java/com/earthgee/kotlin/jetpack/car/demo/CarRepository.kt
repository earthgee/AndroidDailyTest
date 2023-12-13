package com.earthgee.kotlin.jetpack.car.demo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.earthgee.kotlin.jetpack.car.BaseRepository
import com.fwk.sdk.hvac.HvacManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 *  Created by zhaoruixuan1 on 2023/12/13
 *  CopyRight (c) haodf.com
 *  功能：
 */
class CarRepository(val hvacManager: HvacManager) : BaseRepository() {

    init {
        hvacManager.registerCallback {
            data.postValue(it.toString())
        }
    }

    val data = MutableLiveData<String>()

    suspend fun requestTemperature() {
        //hvacManager.requestTemperature()
        delay(2000)
        data.postValue("haha")
    }

}