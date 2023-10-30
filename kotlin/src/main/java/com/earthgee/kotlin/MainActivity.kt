package com.earthgee.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.earthgee.kotlin.databinding.KotlinActivityMainBinding
import com.earthgee.kotlin.dsl.htmlDsl
import kotlin.text.StringBuilder

/**
 *  Created by zhaoruixuan1 on 2023/9/21
 *  test
 *  功能：
 */

class MainActivity : AppCompatActivity(){

    private lateinit var binding : KotlinActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KotlinActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.generateHtml.setOnClickListener {
            htmlDsl {
                head {
                    title { +"hello world" }
                }
                body {
                }
            }.apply {
                val stringBuilder = StringBuilder()
                render(stringBuilder, "")
                binding.tvContent.text = stringBuilder.toString()
            }
        }

    }

}