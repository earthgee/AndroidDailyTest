package com.earthgee.kotlin.jetpack.car.demo

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.earthgee.kotlin.jetpack.car.BaseRepository
import com.earthgee.kotlin.jetpack.car.BaseViewModel
import kotlinx.coroutines.launch

/**
 *  Created by zhaoruixuan1 on 2023/12/13
 *  CopyRight (c) haodf.com
 *  功能：
 */
class CarViewModel(repository: CarRepository) : BaseViewModel<CarRepository>(repository) {

    private val _result = repository.data
    val result: LiveData<String> = _result

    fun requestTemperature() {
        viewModelScope.launch {
            repository.requestTemperature()
        }
    }

    fun setTemperature(view: View) {
//        repository.setTemperatu
    }


}