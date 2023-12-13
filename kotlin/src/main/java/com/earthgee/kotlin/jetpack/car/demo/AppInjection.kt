package com.earthgee.kotlin.jetpack.car.demo

import com.fwk.sdk.hvac.HvacManager

/**
 *  Created by zhaoruixuan1 on 2023/12/13
 *  CopyRight (c) haodf.com
 *  功能：
 */
object AppInjection {

    fun getCarRepository(): CarRepository {
        return CarRepository(HvacManager.getInstance())
    }

}