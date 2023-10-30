package com.earthgee.camera.base;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * Created by zhaoruixuan1 on 2023/10/7
 * test
 * 功能：
 */
public abstract class PreviewImpl {

    private int mWidth;

    private int mHeight;

    public interface Callback {
        void onSurfaceChanged();
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback=callback;
    }

    public abstract Surface getSurface();

    public abstract View getView();

    public abstract Class getOutputClass();

    public abstract void setDisplayOrientation(int displayOrientation);

    public abstract boolean isReady();

    public void dispatchSurfaceChanged() {
        mCallback.onSurfaceChanged();
    }

    public SurfaceHolder getSurfaceHolder() {
        return null;
    }

    public Object getSurfaceTexture() {
        return null;
    }

    public void setBufferSize(int width, int height) {
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }


}
