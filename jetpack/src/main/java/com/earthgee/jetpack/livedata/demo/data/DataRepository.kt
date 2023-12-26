package com.earthgee.jetpack.livedata.demo.data

import androidx.lifecycle.MutableLiveData
import java.util.UUID

/**
 *  Created by zhaoruixuan1 on 2023/12/20
 *  CopyRight (c) haodf.com
 *  功能：
 */
object DataRepository {

    fun requestList(dataList: MutableLiveData<List<Moment>>) {
        val list = ArrayList<Moment>()

        repeat(10) {
            list.add(
                Moment(
                    getUUID(),
                    "天安门上太阳升",
                    "我爱北京天安门",
                    "",
                    "earthgee",
                    "https://tva1.sinaimg.cn/large/e6c9d24ely1h4exa8m7quj20ju0juaax.jpg"
                )
            )
        }

        dataList.value = list
    }

    fun getUUID(): String = UUID.randomUUID().toString().replace("-", "")

}