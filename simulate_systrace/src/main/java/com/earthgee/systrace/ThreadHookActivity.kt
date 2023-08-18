package com.earthgee.systrace

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.earthgee.simulate_systrace.R

/**
 *  Created by zhaoruixuan1 on 2023/8/18
 *  CopyRight (c) haodf.com
 *  功能：
 */
@Route(path = "/atrace/threadhook")
class ThreadHookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_thread_hook)
        findViewById<View>(R.id.button).setOnClickListener {
            ATrace.enableThreadHook()
            Toast.makeText(this@ThreadHookActivity, "开启成功", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.newthread).setOnClickListener {
            Thread {
                Log.e("HOOOOOOOOK", "thread name:" + Thread.currentThread().name)
                Log.e("HOOOOOOOOK", "thread id:" + Thread.currentThread().id)
                Thread {
                    Log.e(
                        "HOOOOOOOOK",
                        "inner thread name:" + Thread.currentThread().name
                    )
                    Log.e("HOOOOOOOOK", "inner thread id:" + Thread.currentThread().id)
                }.start()
            }.start()
        }
    }

}