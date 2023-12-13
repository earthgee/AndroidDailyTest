package com.earthgee.kotlin.jetpack.car

import androidx.lifecycle.ViewModel

/**
 *  Created by zhaoruixuan1 on 2023/12/13
 *  CopyRight (c) haodf.com
 *  功能：
 */
open class BaseViewModel<T: BaseRepository>(val repository: T): ViewModel()