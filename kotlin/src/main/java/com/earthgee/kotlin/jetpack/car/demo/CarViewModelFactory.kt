package com.earthgee.kotlin.jetpack.car.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 *  Created by zhaoruixuan1 on 2023/12/13
 *  CopyRight (c) haodf.com
 *  功能：
 */
val CarViewModelFactory = object: ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            if (modelClass.isAssignableFrom(CarViewModel::class.java)) {
                return modelClass.getConstructor(CarRepository::class.java)
                    .newInstance(AppInjection.getCarRepository())
            } else {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } catch (e: Exception) {
            //none
            throw RuntimeException(e)
        }
    }

}