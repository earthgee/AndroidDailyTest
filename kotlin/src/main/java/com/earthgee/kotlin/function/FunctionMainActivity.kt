package com.earthgee.kotlin.function

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.earthgee.kotlin.R

/**
 *  Created by zhaoruixuan1 on 2023/11/29
 *  CopyRight (c) haodf.com
 *  功能：
 */
class FunctionMainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_function_main)

        show()
    }

    private fun show() {
        findViewById<Button>(R.id.btn_map).setOnClickListener {
            val numbers = listOf(1,2,3,4,5)
            findViewById<TextView>(R.id.tv_map).text = numbers.map { it * it }.toString()
        }

        findViewById<Button>(R.id.btn_filter).setOnClickListener {
            val numbers = listOf(1,2,3,4,5)
            findViewById<TextView>(R.id.tv_filter).text = numbers.filter { it % 2 ==0 }.toString()
        }

        findViewById<Button>(R.id.btn_reduce).setOnClickListener {
            val numbers = listOf(1,2,3,4,5)
            findViewById<TextView>(R.id.tv_reduce).text = numbers.reduce { acc, i -> acc + i }.toString()
        }

        findViewById<Button>(R.id.btn_fold).setOnClickListener {
            val numbers = listOf(1,2,3,4,5)
            findViewById<TextView>(R.id.tv_fold).text = numbers.fold(5) { acc, i -> acc + i }.toString()
        }

        findViewById<Button>(R.id.btn_flat_map).setOnClickListener {
            val words = listOf("hello", "world", "kotlin")
            findViewById<TextView>(R.id.tv_flat_map).text = words.flatMap { it.toList() }.toString()
        }

    }

}








