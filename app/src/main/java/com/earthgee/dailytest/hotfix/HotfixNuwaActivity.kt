package com.earthgee.dailytest.hotfix

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.earthgee.dailytest.R
import java.io.File

/**
 *  Created by zhaoruixuan1 on 2023/10/31
 *  CopyRight (c) haodf.com
 *  功能：
 */
class HotfixNuwaActivity : AppCompatActivity(){

    private val mTvContent: TextView by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_content) }
    private val mBtnApplyHotfix: Button by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_apply_hotfix) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotfix_nuwa)

        mTvContent.text = LoadBugClass().bugString
        mBtnApplyHotfix.setOnClickListener {
            val dexPath = File(getDir("dex", Context.MODE_PRIVATE), "fix_dex.jar")
            Utils.prepareDex(this.applicationContext, dexPath, "fix_dex.jar")
            Hotfix.patch(this, dexPath.absolutePath, "com.earthgee.dailytest.hotfix.BugClass")

            mTvContent.text = LoadBugClass().bugString
        }

    }

}