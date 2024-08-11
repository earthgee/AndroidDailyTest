package com.earthgee.view.recyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.earthgee.view.R
import com.earthgee.view.databinding.ActivityMultiRvBinding

class MultiRvActivity : AppCompatActivity(){

    private val binding: ActivityMultiRvBinding by lazy {
        ActivityMultiRvBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_multi_rv)
        setContentView(binding.root)

        val multiAdapter = MultiRvAdapter().apply {
            addProxy(TextProxy())
            dataList = (0..10).map {
                Text("hello world$it")
            }.toMutableList()
            notifyDataSetChanged()
        }
        binding.rvMulti.adapter = multiAdapter
        binding.rvMulti.layoutManager = LinearLayoutManager(this)
    }

}