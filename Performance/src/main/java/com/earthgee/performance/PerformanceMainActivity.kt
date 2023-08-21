package com.earthgee.performance

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earthgee.performance.allocationtracker.IntroActivity
import com.earthgee.performance.cpu.ProcessTrackActivity
import com.julive.adapter.core.*

/**
 *  Created by zhaoruixuan1 on 2023/8/9
 *  CopyRight (c) haodf.com
 *  功能：性能模块主页
 */
@Route(path = "/performance/main")
class PerformanceMainActivity : AppCompatActivity() {

    val mainItemList = arrayListOf(
        "Allocation Tracker","Process Tracker","ATrace","PThread","AsmTest"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance_main)

        val recyclerviewPerformance = findViewById<RecyclerView>(R.id.recycler_performance)
        listAdapter {
            addAll(createViewModelList(mainItemList.size))
            into(recyclerviewPerformance)
        }
    }

    private fun createViewModelList(max: Int = 10) = (0..max-1).map { index ->
        layoutViewModelDsl(R.layout.list_item_performance, mainItemList[index]) {
            onBindViewHolder {
                val model = getModel<String>()
                getView<TextView>(R.id.tv_content).text = model
            }
            itemView.setOnClickListener {
                val model = getModel<String>()
                when(model) {
                    "Allocation Tracker" -> {
                        startActivity(Intent(this@PerformanceMainActivity, IntroActivity::class.java))
                    }
                    "Process Tracker" -> {
                        startActivity(Intent(this@PerformanceMainActivity, ProcessTrackActivity::class.java))
                    }
                    "ATrace" -> {
                        ARouter.getInstance().build("/atrace/main").navigation()
                    }
                    "PThread" -> {
                        ARouter.getInstance().build("/atrace/threadhook").navigation()
                    }
                    "AsmTest" -> {
                        ARouter.getInstance().build("/atrace/asmtest").navigation()
                    }
                }
            }
        }
    }

}