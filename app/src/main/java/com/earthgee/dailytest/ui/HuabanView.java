package com.earthgee.dailytest.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by zhaoruixuan1 on 2023/12/14
 * CopyRight (c) haodf.com
 * 功能：
 */
public class HuabanView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Path path = new Path();


    public HuabanView(Context context) {
        super(context);
    }

    public HuabanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);//获得surfaceview的生命周期
    }

    public HuabanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HuabanView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new HuabanThread().start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {//按下
            path.moveTo(x, y);
        } else if (action == MotionEvent.ACTION_MOVE) {//移动
            path.lineTo(x, y);
        }
        return true;
    }

    class HuabanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //TODO:画笔
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(20);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            //TODO:画布
            while (true) {
                Canvas canvas = surfaceHolder.lockCanvas();
                //避免空指针
                if (canvas == null){
                    return;
                }
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                canvas.drawColor(Color.WHITE);
                canvas.drawPath(path,paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
    public void close(){
        path.reset();
    }
}