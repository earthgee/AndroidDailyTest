package com.earthgee.dailytest.hotfix

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.earthgee.dailytest.R
import com.earthgee.dailytest.androidbase.AidlClientActivity
import com.earthgee.dailytest.sharedpreferenceimpl.TestSharedPreferencesActivity
import com.julive.adapter.core.*

/**
 *  Created by zhaoruixuan1 on 2023/10/31
 *  CopyRight (c) haodf.com
 *  功能：
 */
@Route(path = "/hotfix/main")
class HotfixMainActivity : AppCompatActivity(){

    val mainItemList = arrayListOf(
        "nuwa",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotfix_main)
        val recyclerviewHotfix = findViewById<RecyclerView>(R.id.recycler_main)
        listAdapter {
            addAll(createViewModelList(mainItemList.size))
            into(recyclerviewHotfix)
        }
    }

    private fun createViewModelList(max: Int = 10) = (0..max - 1).map { index ->
        layoutViewModelDsl(R.layout.list_item_main, mainItemList[index]) {
            onBindViewHolder {
                val model = getModel<String>()
                getView<TextView>(R.id.tv_content).text = model
            }
            itemView.setOnClickListener {
                val model = getModel<String>()
                when (model) {
                    "nuwa" -> {
                        startActivity(
                            Intent(
                                this@HotfixMainActivity,
                                HotfixNuwaActivity::class.java
                            )
                        )
                    }
                }
            }
        }
    }

}