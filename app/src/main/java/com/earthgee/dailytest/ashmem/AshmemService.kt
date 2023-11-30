package com.earthgee.dailytest.ashmem

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.MemoryFile
import android.os.Parcel
import android.os.ParcelFileDescriptor
import java.io.FileDescriptor

/**
 *  Created by zhaoruixuan1 on 2023/11/30
 *  CopyRight (c) haodf.com
 *  功能：
 */
class AshmemService : Service(){

    override fun onBind(intent: Intent?): IBinder = MyBinder()

    class MyBinder: Binder() {

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if(code == 1) {
                val sb = "earthgee".toByteArray()
                val memoryFile = MemoryFile("memFile", sb.size)
                memoryFile.writeBytes(sb, 0, 0, sb.size)
                val method = MemoryFile::class.java.getDeclaredMethod("getFileDescriptor")
                val fd = method.invoke(memoryFile) as FileDescriptor
                val pfd = ParcelFileDescriptor.dup(fd)
                reply?.writeFileDescriptor(fd)
                return true
            }
            return super.onTransact(code, data, reply, flags)
        }

    }

}