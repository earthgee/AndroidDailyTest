package com.earthgee.dailytest.androidbase

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.earthgee.dailytest.R
import com.earthgee.dailytest.ashmem.AshmemClientActivity
import com.earthgee.dailytest.binder.BinderClientActivity
import com.earthgee.dailytest.sharedpreferenceimpl.TestSharedPreferencesActivity
import com.earthgee.dailytest.ui.SurfaceViewActivity
import com.julive.adapter.core.*

/**
 *  Created by zhaoruixuan1 on 2023/8/10
 *  test
 *  功能：
 */
@Route(path = "/base/aidl")
class AndroidBaseMainActivity : AppCompatActivity() {

    val mainItemList = arrayListOf(
        "Simple Aidl",
        "Custom SharedPreferences",
        "Simple Binder",
        "Simpel Ashmem",
        "SurfaceView"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_androidbase_main)

        val recyclerviewPerformance = findViewById<RecyclerView>(R.id.recycler_main)
        listAdapter {
            addAll(createViewModelList(mainItemList.size))
            into(recyclerviewPerformance)
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
                    "Simple Aidl" -> {
                        startActivity(
                            Intent(
                                this@AndroidBaseMainActivity,
                                AidlClientActivity::class.java
                            )
                        )
                    }
                    "Custom SharedPreferences" -> {
                        startActivity(
                            Intent(
                                this@AndroidBaseMainActivity,
                                TestSharedPreferencesActivity::class.java
                            )
                        )
                    }
                    "Simple Binder" -> {
                        startActivity(Intent(this@AndroidBaseMainActivity, BinderClientActivity::class.java))
                    }
                    "Simpel Ashmem" -> {
                        startActivity(Intent(this@AndroidBaseMainActivity, AshmemClientActivity::class.java))
                    }
                    "SurfaceView" -> {
                        startActivity(Intent(this@AndroidBaseMainActivity, SurfaceViewActivity::class.java))
                    }
                }
            }
        }
    }

}