package com.earthgee.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earthgee.view.flipboard.FlipboardViewActivity
import com.earthgee.view.sportview.SportViewActivity
import com.julive.adapter.core.getModel
import com.julive.adapter.core.getView
import com.julive.adapter.core.into
import com.julive.adapter.core.layoutViewModelDsl
import com.julive.adapter.core.listAdapter

/**
 *  Created by zhaoruixuan1 on 2024/3/14
 *  功能：自定义视图主页
 */
@Route(path = "/view/main")
class ViewListMainActivity: AppCompatActivity() {

    val mainItemList = arrayListOf(
        "sportView", "FlipboardView"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_list_main)

        val recyclerviewView = findViewById<RecyclerView>(R.id.recycler_view)
        listAdapter {
            addAll(createViewModelList(mainItemList.size))
            into(recyclerviewView)
        }
    }

    private fun createViewModelList(max: Int = 10) = (0 until max).map { index ->
        layoutViewModelDsl(R.layout.view_item_kotlin, mainItemList[index]) {
            onBindViewHolder {
                val model = getModel<String>()
                getView<TextView>(R.id.tv_content).text = model
            }
            itemView.setOnClickListener {
                val model = getModel<String>()
                when(model) {
                    "sportView" -> {
                        startActivity(Intent(this@ViewListMainActivity, SportViewActivity::class.java))
                    }
                    "FlipboardView" -> {
                        startActivity(Intent(this@ViewListMainActivity, FlipboardViewActivity::class.java))
                    }
                }
            }
        }
    }

}