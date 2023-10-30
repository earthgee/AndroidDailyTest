package com.earthgee.dailytest.sharedpreferenceimpl

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.earthgee.dailytest.databinding.ActivitySharedpreferencesBinding

/**
 *  Created by zhaoruixuan1 on 2023/8/29
 *  test
 *  功能：
 */

class TestSharedPreferencesActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySharedpreferencesBinding

    override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences {
        return applicationContext.getSharedPreferences(name, mode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedpreferencesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.testWritesp.setOnClickListener {
            writeMySharedPreferences()
        }

        binding.testReadsp.setOnClickListener {
            readMySharedPreferences()
        }
    }

    private fun readMySharedPreferences() {
        val sharedPreferences = getSharedPreferences("test", Context.MODE_PRIVATE)
        for (i in 0..99) {
            val key = "test:$i"
            Log.e(
                this@TestSharedPreferencesActivity::class.java.name,
                "key:" + key + ", value:" + sharedPreferences.getInt(key, -1)
            )
        }
    }

    private fun writeMySharedPreferences() {
        val sharedPreferences = getSharedPreferences("test", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        for (i in 0..99) {
            editor.putInt("test:$i", i)
            editor.apply()
        }
    }

}