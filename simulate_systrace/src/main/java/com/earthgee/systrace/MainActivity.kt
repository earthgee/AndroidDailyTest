package com.earthgee.systrace

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.earthgee.simulate_systrace.R

/**
 *  Created by zhaoruixuan1 on 2023/8/11
 *  CopyRight (c) haodf.com
 *  功能：模拟systrace
 */
@Route(path = "/atrace/main")
class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_systrace)
        findViewById<Button>(R.id.button).setOnClickListener {
            if(ATrace.hasHacks()) {
                ATrace.enableSystrace()
                Toast.makeText(this@MainActivity, "开启成功", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
