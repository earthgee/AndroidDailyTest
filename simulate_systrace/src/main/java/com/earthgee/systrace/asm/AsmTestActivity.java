package com.earthgee.systrace.asm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.earthgee.simulate_systrace.R;

/**
 * Created by zhaoruixuan1 on 2023/8/21
 * CopyRight (c) haodf.com
 * 功能：测试asm编译插桩，方法前后调用（卡顿监控准备）
 */
@Route(path = "/atrace/asmtest")
public class AsmTestActivity extends AppCompatActivity {

    private static final String TAG = "AsmTestActivity";

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "[onResume]");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asm_test);
        Button button = findViewById(R.id.test_gc);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TestIgnoreFile.TestGc();
            }
        });
    }

}
