package com.earthgee.camera.base;

import android.view.View;

import java.util.Set;

/**
 * Created by zhaoruixuan1 on 2023/10/7
 * CopyRight (c) haodf.com
 * 功能：
 */
public abstract class CameraViewImpl {

    protected final Callback mCallback;
    protected final PreviewImpl mPreview;

    public CameraViewImpl(Callback callback, PreviewImpl preview) {
        this.mCallback=callback;
        this.mPreview=preview;
    }

    public View getView() {
        return mPreview.getView();
    }

    public abstract boolean start();

    public abstract void stop();

    public abstract boolean isCameraOpened();

    public abstract void setFacing(int facing);

    public abstract int getFacing();

    public abstract Set<AspectRatio> getSupportedAspectRatios();

    public abstract boolean setAspectRatio(AspectRatio ratio);

    public abstract AspectRatio getAspectRatio();

    public abstract void setAutoFocus(boolean autoFocus);

    public abstract boolean getAutoFocus();

    public abstract void setFlash(int flash);

    public abstract int getFlash();

    public abstract void takePicture();

    public abstract void setDisplayOrientation(int displayOrientation);

    public interface Callback {
        void onCameraOpened();
        void onCameraClosed();
        void onPictureTaken(byte[] data);
    }

}
