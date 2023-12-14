package com.earthgee.dailytest.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast

/**
 *  Created by zhaoruixuan1 on 2023/12/14
 *  CopyRight (c) haodf.com
 *  功能：
 */
class DrawSurfaceView(
    context: Context,
    attrs: AttributeSet? = null,
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path: Path
    private val surfaceHolder: SurfaceHolder
    private var flag: Boolean = false

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
        paint.isAntiAlias = true

        path = Path()
        surfaceHolder = holder.apply {
            addCallback(this@DrawSurfaceView)
        }

//        surfaceHolder.setFormat(PixelFormat.TRANSPARENT)
//        setZOrderOnTop(true)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?: return true
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
            }
        }

        return true
    }

    private fun drawCanvas() {
        Thread {
            while(flag) {
                val canvas = surfaceHolder.lockCanvas()
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
                canvas.drawColor(Color.WHITE)
                canvas.drawPath(path, paint)
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d("DrawSurfaceView", "onSurfaceCreated")
        flag = true
        drawCanvas()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

}