package com.earthgee.view.recyclerviewdsl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.earthgee.view.databinding.ActivityMultiRvBinding
import com.earthgee.view.recyclerview.MultiRvAdapter
import com.earthgee.view.recyclerview.Text
import com.earthgee.view.recyclerview.TextProxy

class RecyclerViewDSLActivity:  AppCompatActivity() {

    private val binding: ActivityMultiRvBinding by lazy {
        ActivityMultiRvBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_multi_rv)
        setContentView(binding.root)

        binding.rvMulti.layoutManager = LinearLayoutManager(this)
//        binding.rvMulti.
    }

}