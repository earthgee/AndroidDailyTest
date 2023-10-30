package com.earthgee.performance.cpu

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.earthgee.performance.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 *  Created by zhaoruixuan1 on 2023/8/11
 *  test
 *  功能：
 */
class ProcessTrackActivity : AppCompatActivity() {

    private val processCpuTracker = ProcessCpuTracker(android.os.Process.myPid())

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_track)

        val testGc = findViewById<View>(R.id.test_gc) as Button
        testGc.setOnClickListener {
            processCpuTracker.update()
            testGc()
            processCpuTracker.update()
            printCurrentCpuState()
        }

        val testIO = findViewById<View>(R.id.test_io) as Button
        testIO.setOnClickListener {
            processCpuTracker.update()
            testIO()
            handler.postDelayed(Runnable {
                processCpuTracker.update()
                printCurrentCpuState()
            }, 5000)
        }

        val processOut = findViewById<View>(R.id.test_process) as Button
        processOut.setOnClickListener {
            processCpuTracker.update()
            printCurrentCpuState()
        }

    }

    private fun printCurrentCpuState() {
        Log.e(
            "ProcessCpuTracker",
            processCpuTracker.printCurrentState(SystemClock.uptimeMillis())
        )
    }

    private fun testIO() {
        val thread = Thread {
            writeSth()
            try {
                Thread.sleep(100000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        thread.name = "SingleThread"
        thread.start()
    }


    private fun testGc() {
        for (i in 0..9999) {
            val test = IntArray(100000)
            System.gc()
        }
    }

    private fun writeSth() {
        try {
            val f = File(filesDir, "processtrackerio")
            if (f.exists()) {
                f.delete()
            }
            val fos = FileOutputStream(f)
            val data = ByteArray(1024 * 4 * 3000)
            for (i in 0..29) {
                Arrays.fill(data, i.toByte())
                fos.write(data)
                fos.flush()
            }
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}