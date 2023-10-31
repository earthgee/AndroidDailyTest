package com.earthgee.dailytest

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.julive.adapter.core.*

/**
 *  Created by zhaoruixuan1 on 2023/8/9
 *  test
 *  功能：主页面
 */
class MainActivity : AppCompatActivity() {

    val mainItemList = arrayListOf(
        ItemModule("Android Base", "/base/aidl"),
        ItemModule("Performance", "/performance/main"),
        ItemModule("Kotlin", "/kotlin/main"),
        ItemModule("Camera", "/camera/main"),
        ItemModule("Hotfix", "/hotfix/main")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerviewMain = findViewById<RecyclerView>(R.id.recycler_main)
        listAdapter {
            addAll(createViewModelList(mainItemList.size))
            into(recyclerviewMain)
        }
    }

    private fun createViewModelList(max: Int = 10) = (0..max-1).map { index ->
        layoutViewModelDsl(R.layout.list_item_main, mainItemList[index]) {
            onBindViewHolder {
                val model = getModel<ItemModule>()
                getView<TextView>(R.id.tv_content).text = model?.content
            }
            itemView.setOnClickListener {
                val url = getModel<ItemModule>()?.url
                ARouter.getInstance().build(url).navigation()
            }
        }
    }

    data class ItemModule(val content: String = "", val url: String = "")

}