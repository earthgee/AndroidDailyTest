package com.earthgee.performance.allocationtracker

import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.earthgee.performance.R
import java.io.File

/**
 *  Created by zhaoruixuan1 on 2023/8/10
 *  test
 *  功能：通过inline hook监控java/kotlin对象分配，并写入文件中，附带DumpPrinter可以对文件进行解析.
 */
class IntroActivity : AppCompatActivity(){

    private val tracker: AllocTracker = AllocTracker()

    private val dumpLogBtn: Button by lazy(LazyThreadSafetyMode.NONE) {
        findViewById(R.id.dump_log)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allocationtracker_main)

        tracker.initForArt(0, 5000)
        initExternalReportPath()

        findViewById<Button>(R.id.btn_start).setOnClickListener(View.OnClickListener {
            tracker.startAllocationTracker()
            dumpLogBtn.setEnabled(true)
        })

        findViewById<Button>(R.id.btn_stop).setOnClickListener(View.OnClickListener {
            tracker.stopAllocationTracker()
            dumpLogBtn.isEnabled = false
        })

        findViewById<Button>(R.id.gen_obj).setOnClickListener(View.OnClickListener {
            for (i in 0..999) {
                val msg = Message()
                msg.what = i
            }
        })

        dumpLogBtn.setOnClickListener { Thread { tracker.dumpAllocationDataInLog() }.start() }

    }

    private fun initExternalReportPath() {
        val externalReportPath = File(getExternalFilesDir(null), "crashDump")
        if (!externalReportPath.exists()) {
            externalReportPath.mkdirs()
        }
        tracker.setSaveDataDirectory(externalReportPath.getAbsolutePath())
    }

}