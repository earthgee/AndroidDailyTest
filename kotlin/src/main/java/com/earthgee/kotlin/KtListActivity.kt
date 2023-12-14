package com.earthgee.kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.earthgee.kotlin.conroutie.ConroutineMainActivity
import com.earthgee.kotlin.function.FunctionMainActivity
import com.earthgee.kotlin.jetpack.car.demo.CarActivity
import com.earthgee.kotlin.jetpack.room.WordListActivity
import com.earthgee.kotlin.lottie.LottiePlayActivity
import com.julive.adapter.core.*

/**
 *  Created by zhaoruixuan1 on 2023/9/26
 *  test
 *  功能：
 */
@Route(path = "/kotlin/main")
class KtListActivity : AppCompatActivity() {

    val mainItemList = arrayListOf(
        "dsl", "asycn&wait", "function", "jetpack room", "Jetpack base", "Lottie"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kt_list)

        val recyclerviewKotlin = findViewById<RecyclerView>(R.id.recycler_kotlin)
        listAdapter {
            addAll(createViewModelList(mainItemList.size))
            into(recyclerviewKotlin)
        }
    }

    private fun createViewModelList(max: Int = 10) = (0..max-1).map { index ->
        layoutViewModelDsl(R.layout.list_item_kotlin, mainItemList[index]) {
            onBindViewHolder {
                val model = getModel<String>()
                getView<TextView>(R.id.tv_content).text = model
            }
            itemView.setOnClickListener {
                val model = getModel<String>()
                when(model) {
                    "dsl" -> {
                        startActivity(Intent(this@KtListActivity, MainActivity::class.java))
                    }
                    "asycn&wait" -> {
                        startActivity(Intent(this@KtListActivity, ConroutineMainActivity::class.java))
                    }
                    "function" -> {
                        startActivity(Intent(this@KtListActivity, FunctionMainActivity::class.java))
                    }
                    "jetpack room" -> {
                        startActivity(Intent(this@KtListActivity, WordListActivity::class.java))
                    }
                    "Jetpack base" -> {
                        startActivity(Intent(this@KtListActivity, CarActivity::class.java))
                    }
                    "Lottie" -> {
                        startActivity(Intent(this@KtListActivity, LottiePlayActivity::class.java))
                    }
                }
            }
        }
    }

}