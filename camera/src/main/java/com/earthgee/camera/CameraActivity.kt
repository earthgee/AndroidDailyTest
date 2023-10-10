package com.earthgee.camera

import android.graphics.Camera
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 *  Created by zhaoruixuan1 on 2023/10/9
 *  CopyRight (c) haodf.com
 *  功能：
 */
@Route(path = "/camera/main")
class CameraActivity : AppCompatActivity() {

    private val mCameraView: CameraView by lazy(LazyThreadSafetyMode.NONE) {
        findViewById(R.id.camera)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        mCameraView.addCallback(mCallback)
        findViewById<Button>(R.id.take_picture).setOnClickListener {
            mCameraView.takePicture()
        }
        findViewById<Button>(R.id.switch_camera).setOnClickListener {
            val facing = mCameraView.facing
            mCameraView.facing = if(facing == CameraView.FACING_BACK) CameraView.FACING_FRONT else CameraView.FACING_BACK
        }
    }

    override fun onResume() {
        super.onResume()
        mCameraView.start()
    }

    override fun onPause() {
        mCameraView.stop()
        super.onPause()
    }

    private val mCallback = object: CameraView.Callback() {

        override fun onCameraOpened(cameraView: CameraView?) {
            Log.d("earthgee","onCameraOpened")
        }

        override fun onCameraClosed(cameraView: CameraView?) {
            Log.d("earthgee","onCameraClosed")
        }

        override fun onPictureTaken(cameraView: CameraView?, data: ByteArray?) {
            Toast.makeText(this@CameraActivity, "take picture", Toast.LENGTH_LONG).show()
            lifecycleScope.launch(Dispatchers.IO) {
                val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture${System.currentTimeMillis()}.jpg")
                file.outputStream().use {
                    it.write(data)
                }
            }
        }

    }


}