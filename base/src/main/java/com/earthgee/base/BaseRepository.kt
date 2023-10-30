package com.earthgee.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

/**
 *  Created by zhaoruixuan1 on 2023/10/30
 *  test
 *  功能：
 */
open class BaseRepository

open class BaseViewModel<M : BaseRepository>(val mRepository: M) : ViewModel()

open class BaseAndroidViewMode<M : BaseRepository>(application: Application, val mRepository: M) :
    AndroidViewModel(application)

