package com.earthgee.jetpack.livedata.demo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *  Created by zhaoruixuan1 on 2023/12/19
 *  CopyRight (c) haodf.com
 *  功能：
 */
@Parcelize
data class Moment(
    val uuid: String = "",
    val content: String = "", val location: String = "",
    val imgUrl: String = "", val userName: String = "", val userAvatar: String = ""
) : Parcelable {

    companion object {
        const val MOMENT = "MOMENT"
    }

}
