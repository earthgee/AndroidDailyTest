package com.earthgee.dailytest.binder

import android.os.Binder
import android.os.IBinder
import android.os.Parcel

/**
 *  Created by zhaoruixuan1 on 2023/11/28
 *  CopyRight (c) haodf.com
 *  功能：
 */
interface ICalculator {

    abstract class Stub: Binder(), ICalculator {

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if(code == 1) {
                val a = data.readInt()
                val b = data.readInt()
                val result = add(a, b)
                reply?.writeInt(result)
                return true
            }
            return super.onTransact(code, data, reply, flags)
        }

    }

    class Proxy(val mRemote: IBinder): ICalculator {

        override fun add(a: Int, b: Int): Int {
            val data1 = Parcel.obtain()
            data1.writeInt(a)
            data1.writeInt(b)
            val reply1 = Parcel.obtain()
            mRemote.transact(1, data1, reply1, 0)
            return reply1.readInt()
        }

    }

    fun add(a: Int, b: Int): Int

}