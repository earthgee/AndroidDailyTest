package com.earthgee.view.flipboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.earthgee.view.R

/**
 *  Created by zhaoruixuan1 on 2024/3/15
 *  CopyRight (c) haodf.com
 *  功能：
 */
class FlipboardViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip_board_view)

        findViewById<FlipboardView>(R.id.view_flip_board).startAnimation()
    }

}